#!/bin/sh
kubernetesimages=SVSACCODOCKER.CO-OPBANK.CO.KE:5000/kubernetesimages

sudo kubeadm config images list 2>/dev/null

#sudo kubeadm config images pull

for i in $(sudo kubeadm config images list 2>/dev/null);
do
echo "=========================================="
sudo docker pull $i
echo "------------------------------------------"
image=$(echo $i | awk -F "/" '{print $NF}')
echo image $i
echo new image $kubernetesimages/$image
sudo docker tag $i $kubernetesimages/$image
echo "------------------------------------------"
sleep 2
sudo docker push $kubernetesimages/$image
sleep 2
done


#sudo kubeadm init --image-repository SVSACCODOCKER.CO-OPBANK.CO.KE:5000/kubernetesimages --cri-socket=unix:///var/run/containerd/containerd.sock --v=5
#sudo kubeadm init --image-repository SVSACCODOCKER.CO-OPBANK.CO.KE:5000/kubernetesimages --v=5
#kubeadm init --config=kubeadm-config.yaml --v=5

#sudo journalctl -f -u kubelet
#sudo journalctl -xe | kubelet

#sudo containerd config default > config.toml   
#sudo rm /etc/containerd/config.toml--- 


sudo systemctl status kubelet
sudo systemctl start kubelet
sudo systemctl enable kubelet
sudo systemctl daemon-reload