#!/bin/bash
echo "-------------------------------------------------------------------------------------"
echo "Disk space savings :: Clear workspace caused by the current build ${APPLICATION_NAME}"
echo ${APPFOLDER}
rm -rf ${APPFOLDER}
sleep 2

##Check Jenkins workspace workspaces to save on disk space
#files_to_clear=$(echo -n $filename && echo `ls -lrt $filename | grep -v @tmp | awk -F " " '{print $NF}'` | sed 's#0 #/#g')
echo "Disk space savings :: Clear workspaces caused by older build projects"

for filename in `du -sch /var/lib/jenkins/workspace/* -t 1G | grep -v Deploy | grep -v Stage | awk -F " " '{print $02}' | awk -F "total" '{print $01}' | grep -v '^[[:space:]]*$'`
do

for workspace in `ls $filename | grep -v @tmp`
do

files_to_clear=$(echo -n $filename && echo -n '/' && echo $workspace)
filecount=$(ls $files_to_clear | wc -l)
xml_count=$(find $files_to_clear -name $workspace-PROD.xml 2>/dev/null | wc -l)
minutes=$((($(date +%s) - $(date +%s -r "$files_to_clear")) / 60))

if [ $filecount -ge 1 ] ; then score1=1; else score1=0; fi
if [ $xml_count -ge 1 ] ; then score2=1; else score2=0; fi
if [ $minutes -ge 10 ] ; then score3=1; else score3=0; fi
if [ $(($score1+$score2+$score3)) -ge 2 ] ; then echo ${files_to_clear} && rm -rf $files_to_clear; else echo "${files_to_clear} -- skip , workspace still running."; fi

sleep 2
done
done

echo "----------------------------------------------------------------------------------"
echo -n "timestamp :: " && date
echo "----------------------------------------------------------------------------------"

echo "Keep only latest Jenkins backup"
ls -1tr /var/lib/jenkins/backup/ | head -n -1 | grep -v DIFF | while read files; do rm -rf "/var/lib/jenkins/backup/$files"; done
ls -lrt /var/lib/jenkins/backup/

echo "----------------------------------------------------------------------------------"
echo "Verify disk space savings on Jenkins workspace"
df -h | grep Filesystem && df -h | grep /dev/mapper/rhel-root

