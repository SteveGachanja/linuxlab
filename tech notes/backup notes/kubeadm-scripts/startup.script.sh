#!/bin/sh

#https://kubernetes.io/docs/setup/production-environment/tools/kubeadm/install-kubeadm/

#Disable swap
sudo swapoff -a

cat <<EOF | sudo tee /etc/modules-load.d/k8s.conf
overlay
br_netfilter
EOF

sudo modprobe overlay
sudo modprobe br_netfilter

#Verify that the br_netfilter, overlay modules are loaded
lsmod | grep br_netfilter
lsmod | grep overlay

############################################################

# sysctl params required by setup, params persist across reboots
cat <<EOF | sudo tee /etc/sysctl.d/k8s.conf
net.bridge.bridge-nf-call-iptables  = 1
net.bridge.bridge-nf-call-ip6tables = 1
net.ipv4.ip_forward                 = 1
EOF

#Alternative command
#sudo sysctl net.bridge.bridge-nf-call-iptables=1

# Apply sysctl params without reboot
sudo sysctl --system

## Verify that system variables are set to 1 in your sysctl config
sysctl net.bridge.bridge-nf-call-iptables net.bridge.bridge-nf-call-ip6tables net.ipv4.ip_forward


############################################################

sudo sed -i --follow-symlinks 's/SELINUX=enforcing/SELINUX=disabled/g' /etc/sysconfig/selinux

#sudo yum install -y kubelet-1.24.6 kubeadm-1.24.6 kubectl-1.24.6
#sudo yum install -y kubelet-1.26.0 kubeadm-1.26.0 kubectl-1.26.0
#sudo yum install -y kubelet kubeadm kubectl kubernetes-cni-1.1.1-0 --disableexcludes=kubernetes
#sudo yum install -y kubelet-1.27.3 kubeadm-1.27.3 kubectl-1.27.3
#sudo yum install -y  kubelet-1.23.0 kubeadm-1.23.0 kubectl-1.23.0 kubernetes-cni-1.1.1-0 --disableexcludes=kubernetes
#sudo yum install -y  kubelet-1.22.1 kubeadm-1.22.1 kubectl-1.22.1 kubernetes-cni-1.1.1-0 --disableexcludes=kubernetes
#sudo yum install -y  kubelet-1.28.0 kubeadm-1.28.0 kubectl-1.28.0 --disableexcludes=kubernetes


# This overwrites any existing configuration in /etc/yum.repos.d/kubernetes.repo
cat <<EOF | sudo tee /etc/yum.repos.d/kubernetes.repo
[kubernetes]
name=Kubernetes
baseurl=https://pkgs.k8s.io/core:/stable:/v1.29/rpm/
enabled=1
gpgcheck=1
gpgkey=https://pkgs.k8s.io/core:/stable:/v1.29/rpm/repodata/repomd.xml.key
EOF

sudo yum install kubelet-1.27.3 kubeadm kubectl --disableexcludes=kubernetes

#####################################################################################

# This overwrites any existing configuration in /etc/yum.repos.d/kubernetes.repo
cat <<EOF | sudo tee /etc/yum.repos.d/kubernetes.repo
[kubernetes]
name=Kubernetes
baseurl=https://packages.cloud.google.com/yum/repos/kubernetes-el7-x86_64
enabled=1
gpgcheck=1
repo_gpgcheck=1
gpgkey=https://packages.cloud.google.com/yum/doc/yum-key.gpg
       https://packages.cloud.google.com/yum/doc/rpm-package-key.gpg
proxy=http://192.168.4.11:8080/
EOF

cat <<EOF | sudo tee /etc/resolv.conf
# Generated by NetworkManager
search CO-OPBANK.CO.KE
nameserver      172.30.4.117
nameserver      172.16.206.30
nameserver      172.16.204.14
nameserver      192.168.0.5
EOF

#Install separately kubelet version 1.27.3 
sudo yum install kubelet-1.27.3 --disableexcludes=kubernetes

#Restart docker, containerd and kubelet
sudo systemctl restart docker
sudo systemctl restart containerd

sudo systemctl enable docker
sudo systemctl enable containerd
sudo systemctl enable --now kubelet

sudo mkdir /etc/kubernetes/tmp
sudo chmod -R 777 /etc/kubernetes/tmp

sudo mkdir -p /var/lib/kubelet/pki
sudo chmod -R 777 /var/lib/kubelet/pki
