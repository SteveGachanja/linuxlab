#!/bin/sh
#The reset process does not clean CNI configuration. To do so, you must remove /etc/cni/net.d
sudo kubeadm reset --force
sleep 5

sudo yum remove kubeadm kubelet kubectl
sleep 5

sudo rm -rf /etc/cni/net.d
sudo rm -rf $HOME/.kube/config
sudo rm -rf /home/appadmin/kube

sudo rm -rf /var/lib/cni
sudo rm -rf /var/lib/kubelet
sudo rm -rf /var/lib/etcd

sudo rm -rf /etc/kubernetes/
sudo rm -rf /etc/cni/net.d/

sudo rm -rf /opt/cni

#Delete kubelet old files in /usr/bin
sudo rm -rf /usr/bin/kubelet
sudo rm -rf /usr/bin/kubectl
sudo rm -rf /usr/bin/kubeadm

echo "Done with clean up, reboot server"
sudo reboot
