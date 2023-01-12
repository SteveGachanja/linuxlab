#!/bin/sh

echo -n "Enter username:" && read username
echo -n "Enter password:" && read password


git config --global http.proxy http://$username:$password@172.30.100.1:8080
git config --global https.proxy https://$username:$password@172.30.100.1:8080
