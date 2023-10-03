#!/bin/bash
export servername=$(echo $(hostname))
echo servername $servername
sed -i s#HOSTNAME#"$servername"#g /usr/share/nginx/html/index.html
cat /usr/share/nginx/html/index.html | grep PODNAME