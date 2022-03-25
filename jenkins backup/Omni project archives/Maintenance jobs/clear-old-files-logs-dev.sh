#!/bin/bash
echo "--------------------------------------------------------------"
echo "list and remove old ear files ... "
pathtofile=/app/appadmin/jenkins/workspace/OmniBuilds/Deploy-Dev/OUTPUT/
cd $pathtofile && ls -rt DEV_DIGITAL_APPLICATION_SPRINT* | head -n -5 | while read files; do echo $files && rm -rf $files; done

pathtofile=/app/appadmin/Oracle/Middleware/OmniDev/user_projects/domains/RBX_HOME/RBXStaging/Rbx-Ear/
cd $pathtofile && ls -rt DEV_DIGITAL_APPLICATION_SPRINT* | head -n -5 | while read files; do echo $files && rm -rf $files; done

pathtofile=/app/appadmin/jenkins/workspace/OmniBuilds/Deploy-Dev/MH_HOME/EAR/
cd $pathtofile && ls -rt IntellectMH-DF.* | head -n -5 | while read files; do echo $files && rm -rf $files; done

pathtofile=/app/appadmin/Oracle/Middleware/OmniDev/user_projects/domains/MH_COOP/MH_HOME/EAR/
cd $pathtofile && ls -rt IntellectMH-DF.* | head -n -5 | while read files; do echo $files && rm -rf $files; done

echo "--------------------------------------------------------------"
echo "removing old log files ... "

for logs in `ls /app/appadmin/Oracle/Middleware/OmniDev/user_projects/domains/RBX_HOME/RBXStaging/applogs/`
do
pathtofile=/app/appadmin/Oracle/Middleware/OmniDev/user_projects/domains/RBX_HOME/RBXStaging/applogs
cd $pathtofile
ls | grep serverLogs.log- | while read files ; do rm -rf $files ; done
ls | grep scheduler.log- | while read files ; do rm -rf $files ; done
ls | grep entitlement.log- | while read files ; do rm -rf $files ; done
done

for logs in `ls /log/IGTB_HOME/CBX_HOME/CBXStaging/applogs/`
do
pathtofile=/log/IGTB_HOME/CBX_HOME/CBXStaging/applogs/
cd $pathtofile
ls | grep entitlement.log- | while read files ; do rm -rf $files ; done
ls | grep dbLogs.log- | while read files ; do rm -rf $files ; done
ls | grep serverLogs.log- | while read files ; do rm -rf $files ; done
ls | grep performance.log- | while read files ; do rm -rf $files ; done
ls | grep SecureAccessTracker.log- | while read files ; do rm -rf $files ; done
ls | grep alertLogs.log- | while read files ; do rm -rf $files ; done
done

pathtofile=/log/IGTB_HOME/CBX_HOME_DM/CBXStaging/applogs
find $pathtofile -type f -not -name '*log' -print0 | xargs -0 -I {} rm -v {} 2>&1 >/dev/null

pathtofile=/log/IGTB_HOME/MH_HOME/Logs
find $pathtofile -type f -not -name '*MH17.4.1.2.2.log*' -not -name '*IFLog1.0.log' -not -name '*NVPMsgExecutor.log*' -print0 | xargs -0 -I {} rm -v {} 2>&1 >/dev/null

pathtofile=/app/appadmin/Oracle/Middleware/OmniDev/user_projects/domains/RBX_HOME/RBXStaging/Canvas/Temp/CTStudioLogs
find $pathtofile -type f -not -name 'ctdbLog.log' -not -name 'ctserverLog.log' -not -name 'ctalertLog.log' -not -name 'ctsecurityLog.log' -not -name 'ctconfigLog.log' -not -name 'ctentitlementLog.log' -not -name 'ctschedulerLog.log' -not -name 'ctperformanceLog.log' -print0 | xargs -0 -I {} rm -v {} 2>&1 >/dev/null

pathtofile=/app/IGTB_HOME/MOBILITY_HOME/CBX_INTELLECT_15_1/CBXStaging/serverclasses/Canvas/Temp/CBXLogs/databaselogs/
find $pathtofile -type f -not -name '*cbxdbLog.log*' -print0 | xargs -0 -I {} rm -v {} 2>&1 >/dev/null

pathtofile=/app/IGTB_HOME/MOBILITY_HOME/CBX_INTELLECT_15_1/CBXStaging/serverclasses/Canvas/Temp/CBXLogs/serverlogs
find $pathtofile -type f -not -name '*cbxserverLog.log*' -not -name '*mobloginflow.log*' -print0 | xargs -0 -I {} rm -v {} 2>&1 >/dev/null

echo "--------------------------------------------------------------"
echo "Cron timestamp" && date
echo "--------------------------------------------------------------"

