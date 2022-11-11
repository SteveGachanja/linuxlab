#!/bin/bash

echo "RESTART DF .."
echo "============================================================="

export BUILD_ID=dontKillMe

cd /app/mservers/user_projects/domains/OmniDomain/bin/

echo "Step 1. Find and Kill DF processes::"

process_sh=$(ps -ef | grep DF | grep /bin/sh | grep -v kaspersky | awk -F " " {'print $02'} | tr -d "[:blank:]")
process_DF01=$(ps -ef | grep DF | grep /omni/software/jdk1.8.0_202/bin/java | awk {'print $02'} | tr -d "[:blank:]")

echo "$process_sh $process_DF01" | while read process; do echo $process && kill -9 $process 2>/dev/null; done
sleep 3

echo "-------------------------------------------------------------"
echo "Step 2. Starting the ./startManagedWebLogic_DF.sh process ..."

nohup ./startManagedWebLogic_DF.sh DF01 1>nohup.out 2>nohup.out &
sleep 3

echo "-------------------------------------------------------------"
#./read.nohup.out.sh

for (( ; ; ))
do

Initializing=`cat nohup.out | grep "Initializing On demand file" | wc -l`
StartScheduler=`cat nohup.out | grep "StartScheduler........." | wc -l`
sleep 2
RUNNING=`cat nohup.out | grep "RUNNING" | wc -l`
InitializingERROR1=`cat nohup.out | grep "failed to preload" | wc -l`
InitializingERROR2=`cat nohup.out | grep "Error while initializing On demand configuration" | wc -l`
DB_ERROR1=`cat nohup.out | grep "IO Error" | wc -l`
DB_ERROR2=`cat nohup.out | grep "Error while initializing Data source" | wc -l`
sleep 2

if [ $RUNNING -eq 0 ] && [ $StartScheduler -eq 0 ];
then
export RUNNING_mode=0

elif [ $InitializingERROR1 -ge 1 ] || [ $InitializingERROR2 -ge 1 ] || [ $DB_ERROR1 -ne 0 ] || [ $DB_ERROR2 -ne 0 ];
then
export RUNNING_mode=4

elif [ $RUNNING -ge 1 ] && [ $StartScheduler -eq 0 ];
then
export RUNNING_mode=1

elif [ $RUNNING -ge 1 ] && [ $Initializing -ge 1 ] && [ $StartScheduler -ge 1 ];
then
export RUNNING_mode=2

else
export RUNNING_mode=5
fi

case "$RUNNING_mode" in

0)
echo "RUNNING_mode:$RUNNING_mode" > PROPERTY_FILE
echo "Kindly wait for approx. 3 mins while DF Server is Restarting..." >> PROPERTY_FILE
sleep 2
cat nohup.out | grep -B 1 -A 20 "Initializing On demand file" >> PROPERTY_FILE
cat PROPERTY_FILE
exec &>/dev/null
;;


1)
echo "===================================================" > PROPERTY_FILE
echo "RUNNING_mode:$RUNNING_mode" >> PROPERTY_FILE
echo "1) WARNING : DF has Restarted but the DF StartScheduler did not run ..." >> PROPERTY_FILE
sleep 2
cat nohup.out | grep -B 1 -A 20 "Initializing On demand file" >> PROPERTY_FILE
echo "---------------------------------------------------" >> PROPERTY_FILE
echo "Status variables .... " >> PROPERTY_FILE
echo "----------------------" >> PROPERTY_FILE
echo RUNNING $RUNNING >> PROPERTY_FILE
echo Initializing $Initializing >> PROPERTY_FILE
echo StartScheduler $StartScheduler >> PROPERTY_FILE
echo InitializingERROR1 $InitializingERROR1 >> PROPERTY_FILE
echo InitializingERROR2 $InitializingERROR2 >> PROPERTY_FILE
echo DB_ERROR1 $DB_ERROR1 >> PROPERTY_FILE
echo DB_ERROR2 $DB_ERROR2 >> PROPERTY_FILE
echo "----------------------" >> PROPERTY_FILE

echo "SUMMARY OUTPUT" >> PROPERTY_FILE
cat nohup.out | grep "StartScheduler" >> PROPERTY_FILE
cat nohup.out | grep "Error" >> PROPERTY_FILE
cat nohup.out | grep "RUNNING" >> PROPERTY_FILE
echo "===================================================" >> PROPERTY_FILE
sleep 2
cat PROPERTY_FILE
break
;;

2)
echo "===================================================" > PROPERTY_FILE
echo "RUNNING_mode:$RUNNING_mode" >> PROPERTY_FILE
echo "2) DF has Successfully Restarted..." >> PROPERTY_FILE
sleep 2
cat nohup.out | grep -B 1 -A 20 "Initializing On demand file" >> PROPERTY_FILE
echo "---------------------------------------------------" >> PROPERTY_FILE
echo "Status variables .... " >> PROPERTY_FILE
echo "----------------------" >> PROPERTY_FILE
echo RUNNING $RUNNING >> PROPERTY_FILE
echo Initializing $Initializing >> PROPERTY_FILE
echo StartScheduler $StartScheduler >> PROPERTY_FILE
echo InitializingERROR1 $InitializingERROR1 >> PROPERTY_FILE
echo InitializingERROR2 $InitializingERROR2 >> PROPERTY_FILE
echo DB_ERROR1 $DB_ERROR1 >> PROPERTY_FILE
echo DB_ERROR2 $DB_ERROR2 >> PROPERTY_FILE
echo "----------------------" >> PROPERTY_FILE

echo "SUMMARY OUTPUT" >> PROPERTY_FILE
cat nohup.out | grep "StartScheduler" >> PROPERTY_FILE
cat nohup.out | grep "Error" >> PROPERTY_FILE
cat nohup.out | grep "RUNNING" >> PROPERTY_FILE
echo "===================================================" >> PROPERTY_FILE
sleep 2
cat PROPERTY_FILE
break
;;

*)
echo "===================================================" > PROPERTY_FILE
echo "RUNNING_mode:$RUNNING_mode" >> PROPERTY_FILE
echo "*) WARNING : Something is not OK ..." >> PROPERTY_FILE
sleep 2
cat nohup.out | grep -B 20 -A 20 "Error" >> PROPERTY_FILE
echo "---------------------------------------------------" >> PROPERTY_FILE
echo "Status variables .... " >> PROPERTY_FILE
echo "----------------------" >> PROPERTY_FILE
echo RUNNING $RUNNING >> PROPERTY_FILE
echo Initializing $Initializing >> PROPERTY_FILE
echo StartScheduler $StartScheduler >> PROPERTY_FILE
echo InitializingERROR1 $InitializingERROR1 >> PROPERTY_FILE
echo InitializingERROR2 $InitializingERROR2 >> PROPERTY_FILE
echo DB_ERROR1 $DB_ERROR1 >> PROPERTY_FILE
echo DB_ERROR2 $DB_ERROR2 >> PROPERTY_FILE
echo "----------------------" >> PROPERTY_FILE

echo "SUMMARY OUTPUT" >> PROPERTY_FILE
cat nohup.out | grep "StartScheduler" >> PROPERTY_FILE
cat nohup.out | grep "Error" >> PROPERTY_FILE
cat nohup.out | grep "RUNNING" >> PROPERTY_FILE
echo "===================================================" >> PROPERTY_FILE
sleep 2
cat PROPERTY_FILE
break
;;

esac
done

echo "********* End of script ***************************" >> PROPERTY_FILE



