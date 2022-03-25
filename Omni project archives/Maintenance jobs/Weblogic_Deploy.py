from java.io import File
from java.lang import Exception
from java.io import FileInputStream
from java.util import Properties
import re
import os
import shutil
import time

propInputStream = FileInputStream("./deploy.properties")
configProps = Properties()
configProps.load(propInputStream)

debug()

ADMIN_SERV_IP=configProps.get("ADMIN_SERVER_IP")
admin_url=configProps.get("ADMIN_SERVER_URL")
uname=configProps.get("ADMIN_SERVER_USER")

BINARY_PATH=configProps.get("BINARY_PATH")
EAR_NAME=configProps.get("EAR_NAME_DEPLOY")

pwd=configProps.get("ADMIN_SERVER_PASSWORD")
adminServerName=configProps.get("SERVER_NAME")
mBeanServer=configProps.get("MBEAN_SERVER")

#EAR_NAME=configProps.get("EAR_NAME_UNDEPLOY")
connect(uname, pwd, admin_url)

edit()

startEdit()
deployedAppList=ls('/AppDeployments')

appList = re.findall('DEV_DIGITAL_APPLICATION_SPRINT', deployedAppList)
if len(appList) >= 1:
        #print 'found EAR'
        #print deployedAppList
        OLD_EAR_NAME= deployedAppList[deployedAppList.index("DEV_DIGITAL_APPLICATION_SPRINT"):].split(" ")[0]
        print '****** Stopping and undeploying digitalFaceApplication: '+OLD_EAR_NAME+' *****'
        stopApplication(OLD_EAR_NAME)
        undeploy(OLD_EAR_NAME)
        print '***** Undeployed digitalFaceApplication *****'
		print ("****** Deploying 'digitalFaceApplication' *****")
        deploy(EAR_NAME,BINARY_PATH,adminServerName,timeout=600000000)
        print ("***** Deployed 'digitalFaceApplication' Application successfully *****")
else:
        print ' *****Application digitalFaceApplication not found'
        print ("****** Deploying 'digitalFaceApplication' *****")
        deploy(EAR_NAME,BINARY_PATH,adminServerName,timeout=600000000)
        print ("***** Deployed 'digitalFaceApplication' Application successfully *****")
save()
activate()
disconnect()
print 'Deployment completed successfully. End of the script ... '
exit()
		
