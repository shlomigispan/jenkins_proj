pipelineJob('python-flask-app') {
    definition {
        cps {
            script('''
                pipeline {
                    agent any

                    stages {
                        stage('Checkout Code') {
                            steps {
                                git branch: 'main', url: 'https://github.com/shlomigispan/jenkins_proj.git'
                            }
                        }
                        stage('Build Docker Image') {
                            steps {
                                script {
                                    sh 'docker build -t shlomigis/get_running_docker_containers ./get_running_docker_containers'
                                }
                            }
                        }
                        stage('Push Docker Image') {
                            steps {
                                script {
                                    withCredentials([usernamePassword(credentialsId: 'dockerHub', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                                        sh 'docker login --username $DOCKER_USERNAME --password $DOCKER_PASSWORD'
                                        sh 'docker push shlomigis/get_running_docker_containers'
                                    }
                                }
                            }
                        }
                    }

                    post {
                        success {
                            echo 'Docker image built and pushed successfully.'
                        }
                        failure {
                            echo 'Failed to build or push Docker image.'
                        }
                    }
                }
            ''')
        }
    }
}


pipelineJob('nginx-proxy-builder'){
    definition {
        cps {
            script('''
                pipeline{
                    agent any
                    stages{
                        stage('Prepare Nginx Configuration'){
                            steps{
                                script{
                                    writeFile file: 'nginx.conf', text: """
                                    server {
                                        listen 80;
                                        location / {
                                            proxy_pass http://get_running_docker_containers:5000/containers;
                                            proxy_set_header X-Real-IP \\$remote_addr;
                                            proxy_set_header X-Forwarded-For \\$proxy_add_x_forwarded_for;
                                        }
                                    }
                                    """
                                }
                            }
                        }
                        stage('Create Dockerfile'){
                            steps{
                                writeFile file: 'Dockerfile', text: """
                                FROM nginx:latest
                                COPY nginx.conf /etc/nginx/conf.d/default.conf
                                """
                            }
                        }
                        stage('Build Docker Image'){
                            steps{
                                sh 'docker build -t shlomigis/nginx-proxy .'
                            }
                        }
                        stage('Push to Docker Hub'){
                            steps{
                                withCredentials([usernamePassword(credentialsId: 'dockerHub', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]){
                                    sh 'docker login --username $DOCKER_USERNAME --password $DOCKER_PASSWORD'
                                    sh 'docker push shlomigis/nginx-proxy'
                                }
                            }
                        }
                    }
                    post{
                        always{
                            sh 'docker logout'
                        }
                    }
                }
            ''')
            sandbox()
        }
    }
}

pipelineJob('run-and-test-containers') {
    definition {
        cps {
            script('''
                pipeline {
                    agent any

                    stages {
                        stage('Run Containers') {
                            steps {
                                script {
                                    // Run Flask app container
                                    sh 'docker run -d --name get_running_docker_containers --network jenkins_network -v /var/run/docker.sock:/var/run/docker.sock shlomigis/get_running_docker_containers'

                                    // Run Nginx container with port mapping
                                    sh 'docker run -d --name nginx-proxy --network jenkins_network -p 5001:80 --link get_running_docker_containers:flask-app shlomigis/nginx-proxy'
                                }
                            }
                        }

                        stage('Verify Setup') {
                            steps {
                                script {
                                    // Wait for a few seconds to ensure containers are up and running
                                    sleep 10

                                    // Test the Nginx proxy
                                    def response = sh(script: 'curl -s -o /dev/null -w "%{http_code}" http://nginx-proxy:80', returnStdout: true).trim()
                                
                                    
                                    if (response == '200') {
                                        echo 'Nginx proxy setup is working correctly.'
                                    } else {
                                        error 'Nginx proxy setup failed.'
                                    }
                                }
                            }
                        }
                    }

                    post {
                        always {
                            script {
                                // Cleanup containers
                                sh 'docker stop get_running_docker_containers nginx-proxy || true'
                                sh 'docker rm get_running_docker_containers nginx-proxy || true'
                            }
                        }
                    }
                }
            ''')
        }
    }
}