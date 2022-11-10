import requests
import sys
import time

#API-X configurations
updateURL="/config"
unlockURL="/config/unlock/CoopBank_SOA/test?apikey=internal-config-api-key"
reloadURL="/config/reload/CoopBank_SOA/2"

#Define functions
#REST config
def RestConfig(env,file_name, dev_apix_host,sit_apix_host,uat_apix_host,prd_apix_host,prd_apix_host_2,\
     dev_bw_host,sit_bw_host,uat_bw_host,prd_bw_host_1,prd_bw_host_2,\
        operation,partner,front_uri,backend_uri,port,version):
    print("Processing REST API config for "+file_name+" for env: "+env)

    print( 'Operation: '+operation+'\n Partner:' +partner+'\n front_URI: '+front_uri+\
    '\n backend_URI: '+backend_uri+'\n port: '+port+'\n version:'+version)

    if env=="DEV":
        api_host=dev_apix_host
        bw_host=dev_bw_host
    elif env=="SIT":
        api_host=sit_apix_host
        bw_host=sit_bw_host
    elif env=="UAT":
        api_host=uat_apix_host
        bw_host=uat_bw_host
    elif env=="PRD":
        api_host=prd_apix_host
        api_host_2=prd_apix_host_2
        bw_host=prd_bw_host_1
        bw_host_2=prd_bw_host_2
    else:
        print("No environment found.")
        print("usage : ./updateconfig.sh ENV apix_config.property")
        print("Failed. Exiting configuration")
        exit()

    print("BW host : "+bw_host)

    json_string='[{'+\
        '"clientId": "test",'+\
        '"isReplaceEnabled": false,'+\
        '"enableReload": true,'+\
        '"isActive": true,'+\
        '"version": "'+version+'",'+\
        '"name": "CoopBank_SOA",'+\
        '"facadeOperations": ['+\
            '{'+\
                '"SOAPAction": "",'+\
                '"uri": "'+front_uri+'",'+\
                '"facadeMethod": "POST",'+\
                '"serviceName": "",'+\
                '"errorStatusCode": "500",'+\
                '"processBodyXSLT": {'+\
                    '"filePath": "",'+\
                    '"isBinary": false'+\
                '},'+\
                '"faultTransform": {'+\
                    '"filePath": "SOA_FaultHandler.xsl",'+\
                    '"isBinary": false'+\
                '},'+\
                '"extId": "operation_facade.'+operation+'",'+\
                '"name": "facade.'+operation+'"'+\
            '}'+\
        '],'+\
        '"targetOperationGroups": ['+\
            '{'+\
                '"extId": "sg_targetgroup.'+operation+'",'+\
                '"name": "targetgroup.'+operation+'",'+\
                '"description": "",'+\
                '"type": "LoadBalanced"'+\
            '}'+\
        '],'+\
        '"targetOperations": ['+\
            '{'+\
                '"host": "'+bw_host+'",'+\
                '"port": "'+port+'",'+\
                '"uri": "'+backend_uri+'",'+\
                '"headersToForward": "*, -apikey",'+\
                '"communicationsType": "SYNC",'+\
                '"timeout": "10000",'+\
                '"method":"POST",'+\
                '"targetOperationGroup": {'+\
                    '"extId": "sg_targetgroup.'+operation+'",'+\
                    '"name": "targetgroup.'+operation+'",'+\
                    '"description": "",'+\
                    '"type": "LoadBalanced"'+\
                '},'+\
                '"type": "HTTP",'+\
                '"extId": "service_target.'+operation+'",'+\
                '"name": "target.'+operation+'"'+\
            '}'
    if env=="PRD":
        json_target_2=',{'+\
                '"host": "'+bw_host_2+'",'+\
                '"port": "'+port+'",'+\
                '"uri": "'+backend_uri+'",'+\
                '"headersToForward": "*, -apikey",'+\
                '"communicationsType": "SYNC",'+\
                '"timeout": "10000",'+\
                '"method":"POST",'+\
                '"targetOperationGroup": {'+\
                    '"extId": "sg_targetgroup.'+operation+'",'+\
                    '"name": "targetgroup.'+operation+'",'+\
                    '"description": "",'+\
                    '"type": "LoadBalanced"'+\
                '},'+\
                '"type": "HTTP",'+\
                '"extId": "service_target.'+operation+'_2",'+\
                '"name": "target.'+operation+'_2"'+\
            '}'
        json_string=json_string+json_target_2
    json_string2='],'+\
        '"facadeAccess": ['+\
            '{'+\
                '"partner": {'+\
                    '"serialNumber": "'+partner+'",'+\
                    '"extId": "partner_'+partner+'",'+\
                    '"name": "'+partner+'"'+\
                '},'+\
                '"partnerApiKey": {'+\
                    '"extId": "'+partner+'-api-key",'+\
                    '"name": "'+partner+'-api-key",'+\
                    '"apiKey": "'+partner+'-api-key",'+\
                    '"identifyPartner": true,'+\
                    '"restricted": true'+\
                '},'+\
                '"facadeOperation": {'+\
                    '"SOAPAction": "",'+\
                    '"uri": "'+front_uri+'",'+\
                    '"extId": "operation_facade.'+operation+'",'+\
                    '"name": "facade.'+operation+'"'+\
                '},'+\
                '"timeOut": "12000",'+\
                '"spidCheck": false,'+\
                '"scopes": "",'+\
                '"allowedRequestors": [],'+\
                '"extId": "po_omni_'+partner+'-api-key_facade.'+operation+'",'+\
                '"name": "omni_'+partner+'-api-key_facade.'+operation+'"'+\
            '}'+\
        '],'+\
        '"policyBindings": ['+\
            '{'+\
                '"policyMapping": {'+\
                    '"extId": "policyMapping_AuthenticationByXml",'+\
                    '"name": "AuthenticationByXml"'+\
                '},'+\
                '"uri": "'+front_uri+'",'+\
                '"binding": "service",'+\
                '"transportType": "HTTP",'+\
                '"flow": "in",'+\
                '"operation": {'+\
                    '"extId": "operation_facade.'+operation+'",'+\
                    '"name": "facade.'+operation+'"'+\
                '},'+\
                '"extId": "policyBinding_AuthenticationByXml+/rest/sample/front+facade.'+operation+'",'+\
                '"name": "AuthenticationByXml+'+front_uri+'+facade.'+operation+'"'+\
            '}'+\
        '],'+\
        '"routers": ['+\
            '{'+\
                '"facadeOperation": {'+\
                    '"extId": "operation_facade.'+operation+'",'+\
                    '"name": "facade.'+operation+'"'+\
                '},'+\
                '"targetOperationGroup": {'+\
                    '"extId": "sg_targetgroup.'+operation+'",'+\
                    '"name": "targetgroup.'+operation+'"'+\
                '},'+\
                '"spids": [],'+\
                '"routingKey": "default",'+\
                '"extId": "rt_router.'+operation+'",'+\
                '"name": "router.'+operation+'"'+\
            '}'+\
        ']'+\
    '}]'

    json_string=json_string+json_string2

    #print(json_string)

    api_url=api_host+updateURL

    #print(api_url)

    headers={"Content-Type":"application/json",\
            "Cache-Control": "no-cache",\
            "Authorization":"Basic YWRtaW46YWRtaW4=",\
            "apikey":"internal-config-api-key"}

    print("=====Applying settings to API-X "+api_host+"===========")
    response = requests.put(api_url, data=json_string, headers=headers).json()


    #reload configuration
    print("=====Reloading project .cfg files ===========")
    time.sleep(5)
    reload_resp=requests.get(api_host+reloadURL)

    print("\nconfigurationStatus : "+response['configurationStatusList'][0]['configurationStatus'])
    print("statusDetail : "+response['configurationStatusList'][0]['statusDetail'])


    if env=="PRD":
        time.sleep(10) #pause for 10s before applying config to second APIX in prod
        print("=====Applying settings to second API-X "+api_host_2+"===========")
        response2=requests.put(api_host_2+updateURL, data=json_string, headers=headers).json()
        print("=====Reloading project .cfg files ===========")
        time.sleep(5)
        reload_resp_2=requests.get(api_host_2+reloadURL)
        print("\nconfigurationStatus for host 2: "+response2['configurationStatusList'][0]['configurationStatus'])
        print("statusDetail for host 2: "+response2['configurationStatusList'][0]['statusDetail'])


#SOAPJMS config
def SOAPJMSConfig(env,file_name,dev_apix_host,sit_apix_host,uat_apix_host,prd_apix_host_1,prd_apix_host_2,\
            operation,soapaction,partner,uri,dev_queue,version):
    print("Processing SOAPJMS API config for "+file_name+" for env: "+env)

    print( 'Operation: '+operation+'\n Partner:' +partner+'\n URI: '+uri+\
    '\n soapAction: '+soapAction+'\n queue: '+dev_queue+'\n version:'+version)
    queue=""

    if env=="DEV":
        api_host=dev_apix_host
        queue=dev_queue
    elif env=="SIT":
        api_host=sit_apix_host
        queue=dev_queue.replace("DEV","SIT")
    elif env=="UAT":
        api_host=uat_apix_host
        queue=dev_queue.replace("DEV","UAT")
    elif env=="PRD":
        api_host=prd_apix_host_1
        api_host_2=prd_apix_host_2
        queue=dev_queue.replace("DEV","PRD")
    else:
        print("No environment found.")
        print("usage : ./updateconfig.sh ENV apix_config.property")
        print("Failed. Exiting configuration")
        exit()

    print("Environment="+env+"\nQueue="+queue)

    json_string='[{'+\
        '"clientId":"test",'+\
        '"isReplaceEnabled":false,'+\
        '"enableReload":true,'+\
        '"isActive":true,'+\
        '"version":"'+version+'",'+\
        '"name":"CoopBank_SOA",'+\
        '"facadeOperations":[{'+\
            '"SOAPAction":"\\"'+soapaction+'\\"",'+\
            '"uri":"'+uri+'",'+\
            '"facadeMethod":"",'+\
            '"serviceName":"",'+\
            '"errorStatusCode":"500",'+\
            '"processBodyXSLT":{'+\
                '"filePath":"",'+\
                '"isBinary":false},'+\
            '"faultTransform":{'+\
                '"filePath":"SOA_FaultHandler.xsl",'+\
                '"isBinary":false},'+\
            '"extId":"operation_facade.'+operation+'",'+\
            '"name":"facade.'+operation+'"}],'+\
            '"targetOperations": [{'+\
                        '"jmsSoapAction":"\\"'+soapaction+'\\"",'+\
                        '"jmsPriority":"",'+\
                        '"jmsExpiration":"",'+\
                        '"destinationName":"'+queue+'",'+\
                        '"destinationType":"queue",'+\
                        '"targetService":"",'+\
                        '"contentType":"Text Message",'+\
                        '"isAsync":false,'+\
                        '"timeout":"10000",'+\
                        '"weight":"",'+\
                        '"type":"SOAPJMS",'+\
                        '"extId":"service_target.'+operation+'",'+\
                        '"name":"target.'+operation+'"'+\
            '}],'+\
            '"facadeAccess": [{'+\
                    '"partner": {'+\
                        '"serialNumber":"'+partner+'",'+\
                        '"extId":"partner_'+partner+'",'+\
                        '"name":"'+partner+'"'+\
                    '},'+\
                    '"facadeOperation": {'+\
                        '"SOAPAction":"\\"'+soapaction+'\\"",'+\
                        '"uri":"'+uri+'",'+\
                        '"facadeMethod":"",'+\
                        '"serviceName":"",'+\
                        '"errorStatusCode":"500",'+\
                        '"processBodyXSLT": {'+\
                            '"filePath":"",'+\
                            '"isBinary": false'+\
                        '},'+\
                        '"faultTransform": {'+\
                            '"filePath":"SOA_FaultHandler.xsl",'+\
                            '"isBinary": false'+\
                        '},'+\
                        '"extId":"operation_facade.'+operation+'",'+\
                        '"name":"facade.'+operation+'"'+\
                    '},'+\
                    '"timeOut":"12000",'+\
                    '"spidCheck":"false",'+\
                    '"scopes":"",'+\
                    '"allowedRequestors": [],'+\
                    '"extId":"po_'+partner+'_facade.'+operation+'",'+\
                    '"name":"'+partner+'_facade.'+operation+'"'+\
                '}'+\
            '],'+\
            '"policyBindings": [{'+\
                    '"policyMapping": {'+\
                        '"extId":"policyMapping_AuthenticationByXml",'+\
                        '"name":"AuthenticationByXml"'+\
                    '},'+\
                    '"uri":"'+uri+'",'+\
                    '"binding":"service",'+\
                    '"transportType":"SOAP",'+\
                    '"flow":"in",'+\
                    '"operation": {'+\
                        '"extId":"operation_facade.'+operation+'",'+\
                        '"name":"facade.'+operation+'"'+\
                    '},'+\
                    '"extId":"policyBinding_AuthenticationByXml+'+uri+'facade.'+operation+'",'+\
                    '"name":"AuthenticationByXml+'+uri+'+facade.'+operation+'"'+\
                '}'+\
            '],'+\
            '"routers": [{'+\
                    '"facadeOperation": {'+\
                        '"extId":"operation_facade.'+operation+'",'+\
                        '"name":"facade.'+operation+'"'+\
                    '},'+\
                    '"targetOperationVersion":"",'+\
                    '"targetOperation": {'+\
                        '"type":"NOOP",'+\
                        '"extId":"service_target.'+operation+'",'+\
                        '"name":"target.'+operation+'"'+\
                    '},'+\
                    '"spids": [],'+\
                    '"routingKey":"default",'+\
                    '"extId":"rt_router.'+operation+'",'+\
                    '"name":"router.'+operation+'"'+\
                '}'+\
            ']'+\
    '}]'

    #print(json_string)

    api_url=api_host+updateURL

    #print(api_url)

    headers={"Content-Type":"application/json",\
            "Cache-Control": "no-cache",\
            "Authorization":"Basic YWRtaW46YWRtaW4=",\
            "apikey":"internal-config-api-key"}

    print("=====Applying settings to API-X "+api_host+"===========")
    response = requests.put(api_url, data=json_string, headers=headers).json()

    #reload configuration
    print("=====Reloading project .cfg files ===========")
    time.sleep(5)
    reload_resp=requests.get(api_host+reloadURL)

    print("\nconfigurationStatus : "+response['configurationStatusList'][0]['configurationStatus'])
    print("statusDetail : "+response['configurationStatusList'][0]['statusDetail'])


    if env=="PRD":
        time.sleep(10) #pause for 10s before applying config to second APIX in prod
        print("=====Applying settings to second API-X "+api_host_2+"===========")
        response2=requests.put(api_host_2+updateURL, data=json_string, headers=headers).json()
        print("=====Reloading project .cfg files ===========")
        time.sleep(5)
        reload_resp_2=requests.get(api_host_2+reloadURL)
        print("\nconfigurationStatus for host 2: "+response2['configurationStatusList'][0]['configurationStatus'])
        print("statusDetail for host 2: "+response2['configurationStatusList'][0]['statusDetail'])


#SOAPJMS+REST
def SOAPJMSRestConfig():
    print ("both")


#check apiType
env=sys.argv[1]
#apix hosts
dev_apix_host=sys.argv[3]
sit_apix_host=sys.argv[4]
uat_apix_host=sys.argv[5]
prd_apix_host_1=sys.argv[6]
prd_apix_host_2=sys.argv[7]

#bw hosts
dev_bw_host=sys.argv[8]
sit_bw_host=sys.argv[9]
uat_bw_host=sys.argv[10]
prd_bw_host_1=sys.argv[11]
prd_bw_host_2=sys.argv[12]

#read the file
prop_file=sys.argv[2]
file=open(prop_file,'r')
content=file.readlines()
apiType=operation=content[0].split('=')[1].strip()
op=operation=content[0].split('=')[0].strip()

print("Operation?"+op)

#Rest configurations
if apiType=="REST":
    operation=content[1].split('=')[1].strip()
    partner=content[2].split('=')[1].strip()
    front_uri=content[3].split('=')[1].strip()
    backend_uri=content[4].split('=')[1].strip()
    port=content[5].split('=')[1].strip()
    version="4"

    #for rest, modify operation name incase we have both SOAP and REST
    operation=operation+".REST"

    if (len(operation)==0) or (len(partner)==0) or (len(front_uri)==0) or (len(backend_uri)==0)\
    or (len(port)==0) or (len(version)==0):
        print("Check that operation, partner, front_uri,backend_uri, port and version are all populated")
        print("Failed. Exiting the configurations")
        exit()
        
    RestConfig(env,prop_file,dev_apix_host,sit_apix_host,uat_apix_host,prd_apix_host_1,prd_apix_host_2,\
        dev_bw_host,sit_bw_host,uat_bw_host,prd_bw_host_1,prd_bw_host_2,\
            operation,partner,front_uri,backend_uri,port,version)
    
    #close file
    file.close()
# SOAPJMS configurations. Missing apiType meaning it's the old good SOAPJMS
elif op=="operation":
    operation=content[0].split('=')[1].strip()
    soapAction=content[1].split('=')[1].strip()
    partner=content[2].split('=')[1].strip()
    uri=content[3].split('=')[1].strip()
    dev_queue=content[4].split('=')[1].strip()
    version="4"

    if (len(operation)==0) or (len(partner)==0) or (len(uri)==0) or (len(soapAction)==0)\
    or (len(dev_queue)==0) or (len(version)==0):
        print("Check that operation, partner, uri,soapAction, dev queue and version are all populated")
        print("Failed. Exiting the configurations")
        exit()

    SOAPJMSConfig(env,prop_file,dev_apix_host,sit_apix_host,uat_apix_host,prd_apix_host_1,prd_apix_host_2,\
        operation,soapAction,partner,uri,dev_queue,version)
    
    #close file
    file.close()
# SOAPJMS+REST configurations. For existing services where we need to have REST as an addition
elif apiType=="SOAPJMS+REST":
    operation_1=content[1].split('=')[1].strip()
    #print("Operation="+operation_1)
    soapAction=content[2].split('=')[1].strip()
    partner=content[3].split('=')[1].strip()
    uri=content[4].split('=')[1].strip()
    dev_queue=content[5].split('=')[1].strip()
    front_uri=content[6].split('=')[1].strip()
    backend_uri=content[7].split('=')[1].strip()
    port=content[8].split('=')[1].strip()
    version=operation="4"

    if (len(operation)==0) or (len(partner)==0) or (len(front_uri)==0) or (len(backend_uri)==0)\
    or (len(port)==0) or (len(version)==0):
        print("Check that operation, partner, front_uri,backend_uri, port and version are all populated")
        print("Failed. Exiting the configurations")
        exit()
    
    if (len(operation)==0) or (len(partner)==0) or (len(uri)==0) or (len(soapAction)==0)\
    or (len(dev_queue)==0) or (len(version)==0):
        print("Check that operation, partner, uri,soapAction, dev queue and version are all populated")
        print("Failed. Exiting the configurations")
        exit()
    print("This is a double operation to APIX. It will create a SOAPJMS and REST configuration at a go..")
    SOAPJMSConfig(env,prop_file,dev_apix_host,sit_apix_host,uat_apix_host,prd_apix_host_1,prd_apix_host_2,\
        operation_1,soapAction,partner,uri,dev_queue,version)
    
    operation_1=operation_1+".REST"
    print("Wait as configuration refreshes for REST configuration to Start")
    time.sleep(5)
    RestConfig(env,prop_file,dev_apix_host,sit_apix_host,uat_apix_host,prd_apix_host_1,prd_apix_host_2,\
        dev_bw_host,sit_bw_host,uat_bw_host,prd_bw_host_1,prd_bw_host_2,\
            operation_1,partner,front_uri,backend_uri,port,version)
    
    #close file
    file.close()
else:
    print("Unknown apiType or non-standard properties file")
    print("Failed. Exiting configuration")
    #close file
    file.close()
    exit()