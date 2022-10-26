1. Install docker 
2. Enable kubernetes vi docker desktop preferences 
4. Download and install istio
   1. ``curl -L https://istio.io/downloadIstio | sh -``
   2. ``cd istio-1.15.2``
   3. ``./istio-1.15.2/bin/istioctl install --set profile=demo -y``
5. Create namespace and enable istio injection ``kubectl apply -f 0_setup``
6. Create and switch context for namespace 
   1. ``kubectl config set-context dev --namespace=demo \
      --cluster=docker-desktop \
      --user=docker-desktop ``
   2. ``kubectl config use-context dev``
7. Deploy API Gateway ``kubectl apply -f 1_api_gateway``
8. Deploy Service ``kubectl apply -f 2_service/hello-world``
9. Call service v2 ``curl localhost/hello``



See kubectl [cheetsheet](https://kubernetes.io/docs/reference/kubectl/cheatsheet/) for more commands.. 
