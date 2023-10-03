#!/bin/sh
#*************************************************************************************
# APPLICATION VERSION 1.0
# CREATED by NEO TECH dated Wed 9th November 2022
# REFERENCES
#https://myopswork.com/when-shell-scripts-meets-jenkins-61594f576e96
#https://docs.cloudbees.com/docs/cloudbees-ci-kb/latest/client-and-managed-masters/how-to-create-a-job-using-the-rest-api-and-curl
#https://birolemekli.medium.com/how-to-trigger-jenkins-job-with-bash-script-8f3457d11efc

#SET PARAMETERS
#*************************************************************************************

JENKINS_USER="jenkinsoperator"
JENKINS_TOKEN="116dad145846fab5303ea3815047f0e311"

JENKINS_AUTH="$JENKINS_USER:$JENKINS_TOKEN"
JENKINS_URL="http://172.16.19.221:9191"

BS_TEMPLATE_URL="Maintenance_Pipelines/job/APP.BS.Template"
TS_TEMPLATE_URL="Maintenance_Pipelines/job/APP.TS.Template"

echo "---------------------------------------------------"
echo "Jenkins Application Pipeline Template"
echo "Enter 1 : Create NEW Pipeline" \
&& echo "Enter 2 : Delete and Re-Create Existing Pipeline" \
&& echo "Enter 3 : Delete Existing Pipeline"
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

#Confirm ALL conditions
echo "---------------------------------------------------"
if [[ $APPLICATION_NAME == "" || $TIBCO_APPSPACE == "" || $JENKINS_WORKSPACE == "" ]]; then echo "Enter All the required parameters"  && exit 1; fi
echo CONFIRM TIBCO_APPSPACE $TIBCO_APPSPACE is within the $TIBCO_DOMAIN
echo "---------------------------------------------------"

echo "Confirm to continue with given Parameters ..."
echo -n " (YES/NO) :"
read continue_prompt
echo "---------------------------------------------------"

if [[ ${continue_prompt} == YES || ${continue_prompt} == yes || ${continue_prompt} == Y || ${continue_prompt} == y ]];
then
#GET config.xml Template Business or Technical Application
#Configure Template based APPLICATION_NAME

if [ $TIBCO_DOMAIN == "Business-Domain" ];
then
echo "GET Business-Domain Template"
BASE_JOB_URL=$BS_TEMPLATE_URL
curl -X GET --user $JENKINS_AUTH $JENKINS_URL/job/$BASE_JOB_URL/config.xml -o jenkinsconfigtemplate.xml
sed -i 's#APP.BS.Card.NewCustomerAgreement.Request.1.0#'"$APPLICATION_NAME"'#g' jenkinsconfigtemplate.xml
sed -i 's#Customer-CardEnquiries-AppSpace#'"$TIBCO_APPSPACE"'#g' jenkinsconfigtemplate.xml
fi

if [ $TIBCO_DOMAIN == "Technical-Domain" ];
then
echo "GET Technical-Domain Template"
BASE_JOB_URL=$TS_TEMPLATE_URL
curl -X GET --user $JENKINS_AUTH $JENKINS_URL/job/$BASE_JOB_URL/config.xml -o jenkinsconfigtemplate.xml
sed -i 's#APP.TS.CMS.Card.NewCustomerAgreement.Request.1.0#'"$APPLICATION_NAME"'#g' jenkinsconfigtemplate.xml
sed -i 's#Card-Enquiries-AppSpace#'"$TIBCO_APPSPACE"'#g' jenkinsconfigtemplate.xml
fi

echo "---------------------------------------------------"
#Create Job Item with Error checking Conditions
echo "Create Job Item $APPLICATION_NAME"

curl -X POST --user $JENKINS_AUTH $JENKINS_URL/job/$JENKINS_WORKSPACE/createItem?name=$APPLICATION_NAME --data-binary @jenkinsconfigtemplate.xml -H "Content-Type:text/xml" 1> jenkinslogs
RESPONSE_ERROR1=$(cat jenkinslogs | grep "Error" || true )
RESPONSE_ERROR2=$(cat jenkinslogs | grep "ERROR" || true )

if [[ -n "$RESPONSE_ERROR1"  || -n "$RESPONSE_ERROR2" ]];
then
echo "---------------------------------------------------------------"
echo "ERROR OCCURRED! DID NOT CREATE $APPLICATION_NAME"
echo "---------------------------------------------------------------"
echo -n "Check log errors here >> " && ls -lrt jenkinslogs
echo $RESPONSE_ERROR1 $RESPONSE_ERROR2
cat jenkinslogs | grep --color -o "ERROR"
cat jenkinslogs | grep --color -o "A job already exists with the name"
echo "---------------------------------------------------------------"
echo "Recommendations:"
echo " Confirm if a job name does not already exist with the same name '$APPLICATION_NAME'."
echo " Confirm if the JENKINS_WORKSPACE '$JENKINS_WORKSPACE' is Correct."
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
# RE-CREATE JENKINS APPLICATION
#*************************************************************************************
2)

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

#Confirm ALL conditions
echo "---------------------------------------------------"
if [[ $APPLICATION_NAME == "" || $TIBCO_APPSPACE == "" || $JENKINS_WORKSPACE == "" ]]; then echo "Enter All the required parameters"  && exit 1; fi
echo CONFIRM THAT TIBCO_APPSPACE $TIBCO_APPSPACE is within the $TIBCO_DOMAIN
echo "---------------------------------------------------"

echo "Confirm to continue with given Parameters ..."
echo -n " (YES/NO) :"
read continue_prompt
echo "---------------------------------------------------"

if [[ ${continue_prompt} == YES || ${continue_prompt} == yes || ${continue_prompt} == Y || ${continue_prompt} == y ]];
then
#Delete Job Item with Error checking Conditions
echo "Delete Job Item $APPLICATION_NAME"

curl -X POST --user $JENKINS_AUTH $JENKINS_URL/job/$JENKINS_WORKSPACE/job/$APPLICATION_NAME/doDelete 1> jenkinslogs
RESPONSE_ERROR1=$(cat jenkinslogs | grep "Error" || true )
RESPONSE_ERROR2=$(cat jenkinslogs | grep "ERROR" || true )

if [[ -n "$RESPONSE_ERROR1"  || -n "$RESPONSE_ERROR2" ]];
then
echo "---------------------------------------------------------------"
echo "ERROR OCCURRED! DID NOT DELETE $APPLICATION_NAME"
echo "---------------------------------------------------------------"
echo -n "Check log errors here >> " && ls -lrt jenkinslogs
echo $RESPONSE_ERROR1 $RESPONSE_ERROR2
cat jenkinslogs | grep --color -o "ERROR"
echo "---------------------------------------------------------------"
exit 1
else
echo "---------------------------------------------------------------"
echo "$APPLICATION_NAME PIPELINE SUCCESSFULLY DELETED"
echo "---------------------------------------------------------------"
fi

#GET config.xml Template Business or Technical Application
#Configure Template based APPLICATION_NAME
sleep 2

if [ $TIBCO_DOMAIN == "Business-Domain" ];
then
echo "GET Business-Domain Template"
BASE_JOB_URL=$BS_TEMPLATE_URL
curl -X GET --user $JENKINS_AUTH $JENKINS_URL/job/$BASE_JOB_URL/config.xml -o jenkinsconfigtemplate.xml
sed -i 's#APP.BS.Card.NewCustomerAgreement.Request.1.0#'"$APPLICATION_NAME"'#g' jenkinsconfigtemplate.xml
sed -i 's#Customer-CardEnquiries-AppSpace#'"$TIBCO_APPSPACE"'#g' jenkinsconfigtemplate.xml
fi

if [ $TIBCO_DOMAIN == "Technical-Domain" ];
then
echo "GET Technical-Domain Template"
BASE_JOB_URL=$TS_TEMPLATE_URL
curl -X GET --user $JENKINS_AUTH $JENKINS_URL/job/$BASE_JOB_URL/config.xml -o jenkinsconfigtemplate.xml
sed -i 's#APP.TS.CMS.Card.NewCustomerAgreement.Request.1.0#'"$APPLICATION_NAME"'#g' jenkinsconfigtemplate.xml
sed -i 's#Card-Enquiries-AppSpace#'"$TIBCO_APPSPACE"'#g' jenkinsconfigtemplate.xml
fi

#Create Job Item with Error checking Conditions
echo "Create Job Item $APPLICATION_NAME"
curl -X POST --user $JENKINS_AUTH $JENKINS_URL/job/$JENKINS_WORKSPACE/createItem?name=$APPLICATION_NAME --data-binary @jenkinsconfigtemplate.xml -H "Content-Type:text/xml" 1> jenkinslogs
RESPONSE_ERROR1=$(cat jenkinslogs | grep "Error" || true )
RESPONSE_ERROR2=$(cat jenkinslogs | grep "ERROR" || true )

if [[ -n "$RESPONSE_ERROR1"  || -n "$RESPONSE_ERROR2" ]];
then
echo "---------------------------------------------------------------"
echo "ERROR OCCURRED! DID NOT CREATE $APPLICATION_NAME"
echo "---------------------------------------------------------------"
echo -n "Check log errors here >> " && ls -lrt jenkinslogs
echo $RESPONSE_ERROR1 $RESPONSE_ERROR2
cat jenkinslogs | grep --color -o "ERROR"
cat jenkinslogs | grep --color -o "A job already exists with the name"
echo "---------------------------------------------------------------"
echo "Recommendations:"
echo " Confirm if a job name does not already exist with the same name '$APPLICATION_NAME'."
echo " Confirm if the JENKINS_WORKSPACE '$JENKINS_WORKSPACE' is Correct."
echo "---------------------------------------------------------------"
exit 1
else
echo "---------------------------------------------------------------"
echo "$APPLICATION_NAME PIPELINE SUCCESSFULLY CREATED"
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
3)

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
echo "Delete Job Item $APPLICATION_NAME"
curl -X POST --user $JENKINS_AUTH $JENKINS_URL/job/$JENKINS_WORKSPACE/job/$APPLICATION_NAME/doDelete 1> jenkinslogs
RESPONSE_ERROR1=$(cat jenkinslogs | grep "Error" || true )
RESPONSE_ERROR2=$(cat jenkinslogs | grep "ERROR" || true )

if [[ -n "$RESPONSE_ERROR1"  || -n "$RESPONSE_ERROR2" ]];
then
echo "---------------------------------------------------------------"
echo "ERROR OCCURRED! DID NOT DELETE $APPLICATION_NAME"
echo "---------------------------------------------------------------"
echo -n "Check log errors here >> " && ls -lrt jenkinslogs
echo $RESPONSE_ERROR1 $RESPONSE_ERROR2
cat jenkinslogs | grep --color -o "ERROR"
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


