# Jenkins & K8s Project
The project aims to create a Jenkins groovy file that creates jobs (using job DSL plugin). 

## Items : 
### Step 1:
Jenkins groovy file that creates a pipeline job that pulls code from my GitHub repo.
Build a docker container and push it to the DockerHub. 
The docker that it builds is a python (flask simple web application that talks to the local docker engine and gets the list of running containers ).

### Step 2:
Another job that takes a default Nginx docker file, modifies it and pushes a proxy pass to the first container.
Injects in the request headers a source IP.
Then push the container to DockerHub.

### Step 3:
A third job that runs the two containers and exposes the Nginx container ports only on the local Jenkins machine.
Then, it sends a request to verify the request has gone ok and finished successfully.

### Step 4:
Build any k8s cluster and integrate Keda in the cluster.
Build a Python app that will show the Keda work
 


# Steps:
### Step 1:
Create the app and push it to GitHub
docker-compose -f docker-compose-jenkins.yaml up -d
Plugins: job dsl, pipeline, git
Credentials: GitHub, DockerHub

Pull the app
Build
Push to DockerHub

### Step 2:
Get the nginx image
Build and modify nginx.conf
Build a docker image with the new nginx.conf
Push to DockerHub

### Step 3:
Run the containers with the correct settings
Send the req

### Step 4:
commands:
minikube stop
minikube delete
minikube start

docker build -t shlomigis/simulate-workload:latest .
docker push shlomigis/simulate-workload:latest

kubectl create namespace keda
helm repo add kedacore https://kedacore.github.io/charts
helm repo update
helm install keda kedacore/keda --namespace keda

minikube addons enable metrics-server
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml

kubectl apply -f deployment.yaml
kubectl apply -f service.yaml
kubectl apply -f scaled-object.yaml

kubectl delete -f deployment.yaml
kubectl delete -f service.yaml
kubectl delete -f scaled-object.yaml

test:
kubectl get svc simulate-workload
kubectl get pods -w

while true; do curl http://$(minikube ip):32447/cpu?duration=10; done

delete jenkins + volume
delete minikube
empty dockerHub
