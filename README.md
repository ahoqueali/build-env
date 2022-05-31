# Helloworld service

This sample includes two versions of a simple helloworld service that returns its version
and instance (hostname) when called.
It can be used as a test service when experimenting with version routing.

This service is also used to demonstrate canary deployments working in conjunction with autoscaling.
See [Canary deployments using Istio](https://istio.io/blog/2017/0.1-canary).

## Create minikube cluster and setup Istio service mesh

install and start docker go docker site and download

install Minikube ``` brew install minikube ```

install Istio ``` brew install istioctl ```

create kubernetes cluster
```bash
minikube start --memory=7851 --cpus=4 --kubernetes-version=v1.23.3 \
    --extra-config=controller-manager.cluster-signing-cert-file="/var/lib/minikube/certs/ca.crt" \
    --extra-config=controller-manager.cluster-signing-key-file="/var/lib/minikube/certs/ca.key"
```

```bash

😄  minikube v1.25.2 on Darwin 12.4 (arm64)
✨  Using the docker driver based on user configuration
👍  Starting control plane node minikube in cluster minikube
🚜  Pulling base image ...
🔥  Creating docker container (CPUs=4, Memory=7851MB) ...
🐳  Preparing Kubernetes v1.23.3 on Docker 20.10.12 ...
    ▪ controller-manager.cluster-signing-cert-file=/var/lib/minikube/certs/ca.crt
    ▪ controller-manager.cluster-signing-key-file=/var/lib/minikube/certs/ca.key
    ▪ kubelet.housekeeping-interval=5m
    ▪ Generating certificates and keys ...
    ▪ Booting up control plane ...
    ▪ Configuring RBAC rules ...
🔎  Verifying Kubernetes components...
    ▪ Using image gcr.io/k8s-minikube/storage-provisioner:v5
🌟  Enabled addons: storage-provisioner, default-storageclass
🏄  Done! kubectl is now configured to use "minikube" cluster and "default" namespace by default


```

install istio demo profile

```bash
istioctl install --set profile=demo -y
```

```bash

kubectl label namespace default istio-injection=enabled

```

## Start the helloworld service

The following commands assume you have
[automatic sidecar injection](https://istio.io/docs/setup/additional-setup/sidecar-injection/#automatic-sidecar-injection)
enabled in your cluster.
If not, you'll need to modify them to include
[manual sidecar injection](https://istio.io/docs/setup/additional-setup/sidecar-injection/#manual-sidecar-injection).

To run both versions of the helloworld service, use the following command:

```bash
kubectl apply -f helloworld.yaml
```

see deployment

```bash
kubectl get deployment -o wide  

```


Alternatively, you can run just one version at a time by first defining the service:

```bash
kubectl apply -f helloworld.yaml -l service=helloworld
```

and then deploying version v1, v2, or both:

```bash
kubectl apply -f helloworld.yaml -l version=v1
kubectl apply -f helloworld.yaml -l version=v2
```

For even more flexibility, there is also a script, `gen-helloworld.sh`, that will
generate YAML for the helloworld service. This script takes the following
arguments:

Argument | Default | Description
-------- | ------- | -----------
`--version` | `v1` | Specifies the version that will be returned by the helloworld service.
`--includeService` | `true` | If `true` the service will be included in the YAML.
`--includeDeployment` | `true` | If `true` the deployment will be included in the YAML.

You can use this script to deploy a custom version:

```bash
./gen-helloworld.sh --version customversion | \
    kubectl apply -f -
```

## Configure the helloworld gateway

Apply the helloworld gateway configuration:

```bash
kubectl apply -f helloworld-gateway.yaml
```

Open tunnel to kubernetes cluster. In a new terminal run.

```bash
minikube tunnel
```

Sometimes the tunnle is not cleaned properly by minikube

```bash 

minikube tunnel --cleanup

```

Get external IP

```bash
kubectl get svc istio-ingressgateway -n istio-system

```


Follow [these instructions](https://istio.io/docs/tasks/traffic-management/ingress/ingress-control/#determining-the-ingress-ip-and-ports)
to set the INGRESS_HOST and INGRESS_PORT variables and then confirm the sample is running using curl:

```bash
export GATEWAY_URL=$INGRESS_HOST:$INGRESS_PORT
curl http://$GATEWAY_URL/hello
```

## Autoscale the services

Note that a Kubernetes [Horizontal Pod Autoscaler](https://kubernetes.io/docs/tasks/run-application/horizontal-pod-autoscale/)
only works if all containers in the pods request cpu. In this sample the deployment
containers in `helloworld.yaml` are configured with the request.
The injected istio-proxy containers also include cpu requests,
making the helloworld service ready for autoscaling.

Enable autoscaling on both versions of the service:

```bash
kubectl autoscale deployment helloworld-v1 --cpu-percent=50 --min=1 --max=10
kubectl autoscale deployment helloworld-v2 --cpu-percent=50 --min=1 --max=10
kubectl get hpa
```

## Generate load

```bash
./loadgen.sh &
./loadgen.sh & # run it twice to generate lots of load
```

Wait for about 2 minutes and then check the number of replicas:

```bash
kubectl get hpa
```

If the autoscaler is functioning correctly, the `REPLICAS` column should have a value > 1.

## Cleanup

```bash
kubectl delete -f helloworld.yaml
kubectl delete -f helloworld-gateway.yaml
kubectl delete hpa helloworld-v1 helloworld-v2
```
