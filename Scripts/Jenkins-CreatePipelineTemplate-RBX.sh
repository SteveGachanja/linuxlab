#!/bin/sh
# REFERENCES

#https://docs.cloudbees.com/docs/cloudbees-ci-kb/latest/client-and-managed-masters/how-to-create-a-job-using-the-rest-api-and-curl
#https://birolemekli.medium.com/how-to-trigger-jenkins-job-with-bash-script-8f3457d11efc

JENKINS_USER="jenkinsadmin"
JENKINS_TOKEN="11163d880759048622cf1746552720a29d"

JENKINS_AUTH="$JENKINS_USER:$JENKINS_TOKEN"
JENKINS_URL="http://172.16.209.71:9090"

echo "---------------------------------------------------"
echo "Create Jenkins Application Pipeline" \
&& echo " 1. Enter APPLICATION NAME" \
&& echo " 2. Enter TIBCO APPSPACE" \
&& echo " 3. Enter JENKINS WORKSPACE"
echo "---------------------------------------------------"
echo -n "Enter APPLICATION_NAME:" && read APPLICATION_NAME
echo -n "Enter TIBCO_APPSPACE:" && read TIBCO_APPSPACE
echo -n "Enter JENKINS_WORKSPACE:" && read JENKINS_WORKSPACE
sleep 2
if [[ $APPLICATION_NAME == *APP.BS.* ]]; then TIBCO_DOMAIN="Business-Domain" ; fi
if [[ $APPLICATION_NAME == *APP.TS.* ]]; then TIBCO_DOMAIN="Technical-Domain" ; fi
echo APPLICATION_DOMAIN $TIBCO_DOMAIN

echo "---------------------------------------------------"

#Clean up
APPLICATION_NAME=$(echo $APPLICATION_NAME | tr -d "[:blank:]")
TIBCO_APPSPACE=$(echo $TIBCO_APPSPACE | tr -d "[:blank:]")
JENKINS_WORKSPACE=$(echo $JENKINS_WORKSPACE | tr -d "[:blank:]")

#Get config.xml from the BASE_JOB_URL 
#GET Template based on APPLICATION_NAME / SELECT if Business or Technical Application

if [ $TIBCO_DOMAIN == "Business-Domain" ]; then echo "GET Business-Domain Template" ; fi
if [ $TIBCO_DOMAIN == "Technical-Domain" ]; then echo "GET Technical-Domain Template" ; fi

BASE_JOB_URL="OmniBuilds-CoreBankingIntegration/job/Maintenance_Scheduled_Jobs/job/test"
curl -X GET --user $JENKINS_AUTH $JENKINS_URL/job/$BASE_JOB_URL/config.xml -o mylocalconfig.xml

echo "---------------------------------------------------"
#Create Job Item with Condition to skip an existing job

JOB_URL="OmniBuilds-CoreBankingIntegration/job/Maintenance_Scheduled_Jobs"

ERROR=$(curl -X POST --user $JENKINS_AUTH $JENKINS_URL/job/$JOB_URL/createItem?name=$APPLICATION_NAME --data-binary @mylocalconfig.xml -H "Content-Type:text/xml")
RESPONSE_ERROR=$(echo "$ERROR" | grep "<!DOCTYPE html>" || true )

if [ -n "$RESPONSE_ERROR" ]
then
echo "---------------------------------------------------------------"
echo "RESPONSE_ERROR, Check if $APPLICATION_NAME is already existing."
echo "---------------------------------------------------------------"
exit 1
else
  curl -X POST --user $JENKINS_AUTH $JENKINS_URL/job/$JOB_URL/createItem?name=$APPLICATION_NAME --data-binary @mylocalconfig.xml -H "Content-Type:text/xml"
fi


echo "---------------------------------------------------"







