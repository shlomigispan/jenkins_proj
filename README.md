# Jenkins & K8s Project
a Jenkins groovy file that creates jobs ( look for job DSL plugin ). 

Items : 
1
Jenkins groovy file creates a pipeline job that pulls code from your GitHub repo.
Build a docker container and push it to the docker hub. 
The docker it builds is a python ( flask simple web application that talks to the local docker engine and gets the list of running containers ) 

2
Another job that takes a default Nginx docker file and modifies it and pushes a proxy pass to the first container (and injects
in the request headers a source IP ) then push the container to docker hub  

3
A third job that runs the two containers and exposes the Nginx container ports only on the local Jenkins machine
then sends a request to verify the request has gone ok and finishes successfully

4
What is Keda utility? Build any k8s cluster and integrate this tool in the cluster.
 
In the end, push everything to your GitHub project and send me the link.


# steps:
1
create the app and push it to gitHub

## jenkins:
docker-compose -f docker-compose-jenkins.yaml up -d

plagins: job dsl, pipeline, git
cred: gitHub, dockerHub

pull the app
build
push to dockerhub

2
build and modifiy nginx.conf
build docker image with the new nginx.conf
push to dockerHub

3
run the cointaines with the correct settings
send the req

4
# commands
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
