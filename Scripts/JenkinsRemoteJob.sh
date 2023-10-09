#!/bin/sh
# REFERENCES
#https://myopswork.com/when-shell-scripts-meets-jenkins-61594f576e96
#https://docs.cloudbees.com/docs/cloudbees-ci-kb/latest/client-and-managed-masters/how-to-create-a-job-using-the-rest-api-and-curl
#https://birolemekli.medium.com/how-to-trigger-jenkins-job-with-bash-script-8f3457d11efc

JENKINS_USER="sngachanja"
JENKINS_TOKEN="1109dd5770ade8db4a55e735433f7f95e3"
JENKINS_AUTH="$JENKINS_USER:$JENKINS_TOKEN"
JENKINS_URL="http://172.16.209.71:9090"

#Get template to create Job Item
BASE_JOB_URL="OmniBuilds-CoreBankingIntegration/job/Maintenance_Scheduled_Jobs/job/post"
curl -X GET --user $JENKINS_AUTH $JENKINS_URL/job/$BASE_JOB_URL/config.xml -o mylocalconfig.xml

#Create Job Item
JOB_URL="OmniBuilds-CoreBankingIntegration/job/Maintenance_Scheduled_Jobs/"
curl -X POST --user $JENKINS_AUTH $JENKINS_URL/job/$JOB_URL/createItem?name=TESTJobName1 --data-binary @mylocalconfig.xml -H "Content-Type:text/xml" 1>/dev/null
sleep 2

#Build Job Item
JOB_URL="OmniBuilds-CoreBankingIntegration/job/Maintenance_Scheduled_Jobs/job/TESTJobName1"
curl -X POST --user $JENKINS_AUTH $JENKINS_URL/job/$JOB_URL/build
#curl -X POST --user $JENKINS_AUTH $JENKINS_URL/job/$JOB_URL/buildWithParameters?myparam=myparam_value
sleep 10

#Delete Job Item
curl -X POST --user $JENKINS_AUTH $JENKINS_URL/job/$JOB_URL/doDelete
