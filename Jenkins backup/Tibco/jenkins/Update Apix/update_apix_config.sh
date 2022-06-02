#!/bin/bash
## Usage of script :: /tibco/scripts/starters/asg/updateconfig.sh "$ENVIRONMENT" ./Jenkins/Apix/$APPLICATION_NAME.properties

operation=""
SOAPAction=""
partner=""
uri=""
DEV_queue=""
version=0

#hosts
DEV_host="http://172.16.4.44:9222"
SIT_host="http://172.16.204.136:9222"
UAT_host="http://172.16.20.99:9222"
PRD_host="http://172.16.X.XX:9222"

updateURL="/config?apikey=internal-config-api-key"
unlockURL="/config/unlock/CoopBank_SOA/test?apikey=internal-config-api-key"

# read values from api_config.txt file from GIT
filename=$2
here=0

while read -r line
do
 here=$((here+1))
 name="$line"
 value=$(echo ${line:`expr index "$line" =`})
 if [ $here -eq 1  ]
 then
  operation="$value"
 elif [ $here -eq 2 ]
 then
  SOAPAction="$value"
 elif [ $here -eq 3 ]
 then
  partner="$value"
 elif [ $here -eq 4 ]
 then
  uri="$value"
 elif [ $here -eq 5 ]
 then
  DEV_queue="$value"
 elif [ $here -eq 6 ]
 then
  version="$value"
 fi 
done <"$filename"

# Check environment to apply
env_=$1
echo input_environment="$env_"

if [ "$env_" == "DEV" ]
then
	queue="$DEV_queue"
	host="$DEV_host"
elif [ "$env_" == "SIT" ]
then
	queue=$(echo ${DEV_queue/DEV/SIT})
	host="$SIT_host"
elif [ "$env_" == "UAT" ]
then
        queue=$(echo ${DEV_queue/DEV/UAT})
        host="$UAT_host"
elif [ "$env_" == "PRD" ]
then
	queue=$(echo ${DEV_queue/DEV/PRD})
	host="$PRD_host"
else
	echo No environment passed in the input. Quiting.....
	echo "usage : ./updateconfig.sh ENV apix_config.property"
	exit 1
fi


echo Environment = "$host"
echo QUEUE = "$queue"
echo operation_name = "$operation"
echo partner = "$partner"
echo SOAPAction = "$SOAPAction"
echo URI = "$uri"
echo version = "$version"

#r_name="\\\"/co-opbank.co.ke/test/2.0\\\""
#echo r_name $r_name

#body="[{\"clientId\":\"test\",\"isReplaceEnabled\":false,\"enableReload\":false,\"isActive\":true,\"version\":1,\"name\":\"CoopBank_SOA\",\"facadeOperations\":[{\"SOAPAction\":\"\\\"$SOAPAction\\\"\",\"uri\":\"/test/1.0\",\"facadeMethod\":\"\",\"serviceName\":\"\",\"errorStatusCode\":\"500\",\"processBodyXSLT\":{\"filePath\":\"\",\"isBinary\":false},\"faultTransform\":{\"filePath\":\"SOA_FaultHandler.xsl\",\"isBinary\":false},\"extId\":\"operation_facade.$operation\",\"name\":\"facade.$operation\"}]}]"
#echo body===== $body

body="[{
\"clientId\":\"test\",
\"isReplaceEnabled\":false,
\"enableReload\":true,
\"isActive\":true,
\"version\":"$version",
\"name\":\"CoopBank_SOA\",
\"facadeOperations\":[{
	\"SOAPAction\":\"\\\""$SOAPAction"\\\"\",
	\"uri\":\""$uri"\",
	\"facadeMethod\":\"\",
	\"serviceName\":\"\",
	\"errorStatusCode\":\"500\",
	\"processBodyXSLT\":{
		\"filePath\":\"\",
		\"isBinary\":false},
	\"faultTransform\":{
		\"filePath\":\"SOA_FaultHandler.xsl\",
		\"isBinary\":false},
	\"extId\":\"operation_facade."$operation"\",
	\"name\":
	\"facade."$operation"\"}],
	\"targetOperations\": [{
				\"jmsSoapAction\":\"\\\""$SOAPAction"\\\"\",
				\"jmsPriority\":\"\",
				\"jmsExpiration\":\"\",
				\"destinationName\":\""$queue"\",
				\"destinationType\":\"queue\",
				\"targetService\":\"\",
				\"contentType\":\"Text Message\",
				\"isAsync\":\"false\",
				\"timeout\":\"10000\",
				\"weight\":\"\",
				\"type\":\"SOAPJMS\",
				\"extId\":\"service_target."$operation"\",
				\"name\":\"target."$operation"\"
			}
		],
		\"facadeAccess\": [{
				\"partner\": {
					\"email\":\"\",
					\"phone\":\"\",
					\"sslExpDate\":\"\",
					\"group\": {
						\"extId\":\"group_SOA_Clients\",
						\"name\":\"SOA_Clients\"
					},
					\"serialNumber\":\""$partner"\",
					\"issuerCA\":\" urn: www.tibco.com\",
					\"spidList\": [],
					\"spidInclude\": false,
					\"extId\":\"partner_"$partner"\",
					\"name\":\""$partner"\"
				},
				\"facadeOperation\": {
					\"SOAPAction\":\"\\\""$SOAPAction"\\\"\",
					\"uri\":\""$uri"\",
					\"facadeMethod\":\"\",
					\"serviceName\":\"\",
					\"errorStatusCode\":\"500\",
					\"processBodyXSLT\": {
						\"filePath\":\"\",
						\"isBinary\": false
					},
					\"faultTransform\": {
						\"filePath\":\"SOA_FaultHandler.xsl\",
						\"isBinary\": false
					},
					\"extId\":\"operation_facade."$operation"\",
					\"name\":\"facade."$operation"\"
				},
				\"timeOut\":\"12000\",
				\"spidCheck\":\"false\",
				\"scopes\":\"\",
				\"allowedRequestors\": [],
				\"extId\":\"po_"$partner"_facade."$operation"\",
				\"name\":\""$patner"_facade."$operation"\"
			}
		],
		\"policyBindings\": [{
				\"policyMapping\": {
					\"extId\":\"policyMapping_AuthenticationByXml\",
					\"name\":\"AuthenticationByXml\"
				},
				\"uri\":\""$uri"\",
				\"binding\":\"service\",
				\"transportType\":\"SOAP\",
				\"flow\":\"in\",
				\"operation\": {
					\"extId\":\"operation_facade."$operation"\",
					\"name\":\"facade."$operation"\"
				},
				\"extId\":\"policyBinding_AuthenticationByXml+"$uri"+facade."$operation"\",
				\"name\":\"AuthenticationByXml+"$uri"+facade."$operation"\"
			}
		],
		\"routers\": [{
				\"facadeOperation\": {
					\"extId\":\"operation_facade."$operation"\",
					\"name\":\"facade."$operation"\"
				},
				\"targetOperationVersion\":\"\",
				\"targetOperation\": {
					\"type\":\"NOOP\",
					\"extId\":\"service_target."$operation"\",
					\"name\":\"target."$operation"\"
				},
				\"spids\": [],
				\"routingKey\":\"default\",
				\"extId\":\"facade."$operation"//target."$operation"/default/\",
				\"name\":\"facade."$operation"//target."$operation"/default/\"
			}
		]
}]"


#echo "body========= $body"

echo "=========Applying settings to API-X $host$updateURL================"
curl -X PUT "$host$updateURL" \
-H 'Content-Type:application/json' \
-H 'Cache-Control: no-cache' \
--header 'Accept:application/json' \
--header 'Authorization:Basic YWRtaW46YWRtaW4=' \
--trace log.txt \
-d \
"$body"

echo "==============Done applying=================="

echo "==============Unlocking project .cfg files=================="
sleep 10

curl -X GET --header 'Accept: text/plain' \
--header 'Authorization: Basic YWRtaW46YWRtaW4=' \
"$host$unlockURL"

echo "==============Done Unlocking=================="

