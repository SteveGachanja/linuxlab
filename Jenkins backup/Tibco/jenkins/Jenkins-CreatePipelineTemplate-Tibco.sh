#!/bin/sh
#*************************************************************************************
# APPLICATION VERSION 1.0
# CREATED by NEO TECH dated Wed 10th November 2022
# REFERENCES
#https://myopswork.com/when-shell-scripts-meets-jenkins-61594f576e96
#https://docs.cloudbees.com/docs/cloudbees-ci-kb/latest/client-and-managed-masters/how-to-create-a-job-using-the-rest-api-and-curl
#https://birolemekli.medium.com/how-to-trigger-jenkins-job-with-bash-script-8f3457d11efc

#SET PARAMETERS
#*************************************************************************************

JENKINS_USER="jenkinsadmin"
JENKINS_TOKEN="11c4a4afe921f3717ffe741c1355c73e54"

JENKINS_AUTH="$JENKINS_USER:$JENKINS_TOKEN"
JENKINS_URL="http://172.16.19.221:9191"

BS_TEMPLATE_URL="Maintenance_Pipelines/job/APP.BS.Template"
TS_TEMPLATE_URL="Maintenance_Pipelines/job/APP.TS.Template"

echo "---------------------------------------------------"
echo "Jenkins Application Pipeline Template"
echo "Enter 1 : Create NEW Pipeline" \
&& echo "Enter 2 : Delete Existing Pipeline"
echo "---------------------------------------------------"
echo -n "Enter Option:" && read choice_input
echo "---------------------------------------------------"

case "$choice_input" in

#*************************************************************************************
# CREATE JENKINS APPLICATION
#*************************************************************************************
1)

echo "Create Jenkins Application Pipeline" \
&& echo " 1. Enter APPLICATION NAME" \
&& echo " 2. Enter TIBCO APPSPACE" \
&& echo " 3. Enter JENKINS WORKSPACE"
echo "---------------------------------------------------"
echo -n "Enter APPLICATION_NAME:" && read APPLICATION_NAME
echo -n "Enter TIBCO_APPSPACE:" && read TIBCO_APPSPACE
echo -n "Enter JENKINS_WORKSPACE:" && read JENKINS_WORKSPACE

#Clean up Entry Point
APPLICATION_NAME=$(echo $APPLICATION_NAME | tr -d "[:blank:]")
TIBCO_APPSPACE=$(echo $TIBCO_APPSPACE | tr -d "[:blank:]")
JENKINS_WORKSPACE=$(echo $JENKINS_WORKSPACE | tr -d "[:blank:]")

if [[ $APPLICATION_NAME == *APP.BS.* ]]; then TIBCO_DOMAIN="Business-Domain" ; fi
if [[ $APPLICATION_NAME == *APP.TS.* ]]; then TIBCO_DOMAIN="Technical-Domain" ; fi
echo APPLICATION_DOMAIN $TIBCO_DOMAIN

echo "---------------------------------------------------"
#Confirm ALL conditions

if [[ $APPLICATION_NAME == "" || $TIBCO_APPSPACE == "" || $JENKINS_WORKSPACE == "" ]]; then echo "Enter All the required parameters"  && exit 1; fi

echo "Confirm to continue with given Parameters ..."
echo -n " (YES/NO) :"
read continue_prompt
echo "---------------------------------------------------"

if [[ ${continue_prompt} == YES || ${continue_prompt} == yes || ${continue_prompt} == Y || ${continue_prompt} == y ]];
then
echo Confirmed 
#GET config.xml Template Business or Technical Application
#Configure Template based APPLICATION_NAME

if [ $TIBCO_DOMAIN == "Business-Domain" ];
then
echo "GET Business-Domain Template"
BASE_JOB_URL=$BS_TEMPLATE_URL
curl -X GET --user $JENKINS_AUTH $JENKINS_URL/job/$BASE_JOB_URL/config.xml -o templateconfig.xml
sed -i 's#APP.BS.Card.NewCustomerAgreement.Request.1.0#'"$APPLICATION_NAME"'#g' templateconfig.xml
sed -i 's#Customer-CardEnquiries-AppSpace#'"$TIBCO_APPSPACE"'#g' templateconfig.xml
fi

if [ $TIBCO_DOMAIN == "Technical-Domain" ];
then
echo "GET Technical-Domain Template"
BASE_JOB_URL=$TS_TEMPLATE_URL
curl -X GET --user $JENKINS_AUTH $JENKINS_URL/job/$BASE_JOB_URL/config.xml -o templateconfig.xml
sed -i 's#APP.TS.CMS.Card.NewCustomerAgreement.Request.1.0#'"$APPLICATION_NAME"'#g' templateconfig.xml
sed -i 's#Card-Enquiries-AppSpace#'"$TIBCO_APPSPACE"'#g' templateconfig.xml
fi

echo "---------------------------------------------------"
#Create Job Item with Error checking Conditions

curl -X POST --user $JENKINS_AUTH $JENKINS_URL/job/$JENKINS_WORKSPACE/createItem?name=$APPLICATION_NAME --data-binary @templateconfig.xml -H "Content-Type:text/xml" 1>jenkinslogs
RESPONSE_ERROR1=$(cat jenkinslogs | grep "<!DOCTYPE html>" || true )
RESPONSE_ERROR2=$(cat jenkinslogs | grep "ERROR 404" || true )

if [[ -n "$RESPONSE_ERROR1"  || -n "$RESPONSE_ERROR2" ]];
then
echo "---------------------------------------------------------------"
echo "ERROR OCCURRED! Kindly Debug"
echo " Check If All requirements are OK e.g. :"
echo " 1. JENKINS_WORKSPACE $JENKINS_WORKSPACE is correct."
echo " 2. APPLICATION_NAME $APPLICATION_NAME does not already exist."
echo "---------------------------------------------------------------"
exit 1
else
echo "---------------------------------------------------------------"
echo "$APPLICATION_NAME PIPELINE SUCCESSFULLY PROVISIONED"
echo "---------------------------------------------------------------"
fi

else
exit 1
fi

break
;;

#*************************************************************************************
# DELETE JENKINS APPLICATION
#*************************************************************************************
2)

echo "Delete Jenkins Application Pipeline" \
&& echo " 1. Enter APPLICATION NAME" \
&& echo " 2. Enter JENKINS WORKSPACE"

echo "---------------------------------------------------------------"
echo -n "Enter APPLICATION_NAME:" && read APPLICATION_NAME
echo -n "Enter JENKINS_WORKSPACE:" && read JENKINS_WORKSPACE

#Clean up Entry Point
APPLICATION_NAME=$(echo $APPLICATION_NAME | tr -d "[:blank:]")
JENKINS_WORKSPACE=$(echo $JENKINS_WORKSPACE | tr -d "[:blank:]")

echo "---------------------------------------------------------------"
#Confirm ALL conditions

if [[ $APPLICATION_NAME == "" || $JENKINS_WORKSPACE == "" ]]; then echo "Enter All the required parameters"  && exit 1; fi

echo "Confirm to continue with given Parameters ..."
echo -n " (YES/NO) :"
read continue_prompt
echo "---------------------------------------------------------------"

if [[ ${continue_prompt} == YES || ${continue_prompt} == yes || ${continue_prompt} == Y || ${continue_prompt} == y ]];
then
echo Confirmed 
curl -X POST --user $JENKINS_AUTH $JENKINS_URL/job/$JENKINS_WORKSPACE/job/$APPLICATION_NAME/doDelete 1>jenkinslogs
RESPONSE_ERROR1=$(cat jenkinslogs | grep "ERROR 404" || true )

if [[ -n "$RESPONSE_ERROR1"  || -n "$RESPONSE_ERROR2" ]];
then
echo "---------------------------------------------------------------"
echo "ERROR OCCURRED! Kindly Debug"
echo $RESPONSE_ERROR1
echo "---------------------------------------------------------------"
exit 1
else
echo "---------------------------------------------------------------"
echo "$APPLICATION_NAME PIPELINE SUCCESSFULLY DELETED"
echo "---------------------------------------------------------------"
fi
fi

break
;;
esac

#*************************************************************************************
# END OF SCRIPT 
#*************************************************************************************


