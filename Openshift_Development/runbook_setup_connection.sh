#!/bin/bash

export username=stevegachanja
export token=sha256~q_xPgo9CjXdxhe0DycKlS9jlOfGvBLw7xC-IvVC-Zkw
export cluster_name=api-sandbox-x8i5-p1-openshiftapps-com:6443
export context=${username}-dev/$cluster_name/${username}
export api_server_url=https://api.sandbox.x8i5.p1.openshiftapps.com:6443

#-----------------------------------------------------------------------

kubectl config set-credentials ${username}/${cluster_name} --token=${token}
sleep 3
kubectl config set-cluster ${cluster_name} --server=${api_server_url}
sleep 3
kubectl config set-context ${context} --user=${username}/${cluster_name} --namespace=${username}-dev --cluster=${cluster_name}
sleep 3
kubectl config use-context ${context}