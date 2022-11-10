#!/bin/bash

operation=""
SOAPAction=""
partner=""
uri=""
DEV_queue=""
port=""
apiType=""
version="2"

#API-hosts
DEV_host="http://172.16.4.44:9222"
SIT_host="http://172.16.204.136:9222"
UAT_host="http://172.16.20.99:9222"
PRD_host="http://172.16.X.XX:9222"
prd_host_2="http://172.16.X.YY:9222"

#bw hosts
dev_bw_host="127.0.0.1"
sit_bw_host="127.0.0.1"
uat_bw_host="127.0.0.1"
prd_bw_host_1="127.0.0.1"
prd_bw_host_2="127.0.0.2"

updateURL="/config"
unlockURL="/config/unlock/CoopBank_SOA/test"
reloadURL="/config/reload/CoopBank_SOA/2"

# read values from api_config.txt file from GIT
filename=$2
read -r fline < $2
#echo $2
#echo $1

python2.7 ./restConfig.py $1 $2 $DEV_host $SIT_host $UAT_host $PRD_host $prd_host_2 $dev_bw_host $sit_bw_host $uat_bw_host $prd_bw_host_1 $prd_bw_host_2
