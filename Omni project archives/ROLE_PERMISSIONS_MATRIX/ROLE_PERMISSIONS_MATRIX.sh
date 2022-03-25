#!/bin/bash
path=/var/lib/jenkins/workspace/ROLE_PERMISSIONS_MATRIX

if [[ $BUILD_USER_ID = "" ]];
then
echo "WARNING** BUILD_USER_ID is empty"

else
CAPABILITIES=`cat $path/APPROVED_BUILDERS.property | grep ${BUILD_USER_ID} | awk -F "::" '{print $01}'`

case "$CAPABILITIES" in

*DEV_*)
echo "BUILD_USER ${BUILD_USER_ID} is a ${CAPABILITIES}" > $path/CAPABILITIES
echo "================================================" >> $path/CAPABILITIES
cat $path/APPROVED_BUILDERS.property | grep DEV_CAPABILITIES >> $path/CAPABILITIES
echo "================================================" >> $path/CAPABILITIES
;;

*SYSTEM_*)
echo "BUILD_USER ${BUILD_USER_ID} is a ${CAPABILITIES}" > $path/CAPABILITIES
echo "================================================" >> $path/CAPABILITIES
cat $path/APPROVED_BUILDERS.property | grep SYSTEM_CAPABILITIES >> $path/CAPABILITIES
echo "================================================" >> $path/CAPABILITIES
;;

*EMPOWERED_*)
echo "BUILD_USER ${BUILD_USER_ID} is an ${CAPABILITIES}" > $path/CAPABILITIES
echo "================================================" >> $path/CAPABILITIES
cat $path/APPROVED_BUILDERS.property | grep EMPOWERED_CAPABILITIES >> $path/CAPABILITIES
echo "================================================" >> $path/CAPABILITIES
;;

*SUPER_*)
echo "BUILD_USER ${BUILD_USER_ID} is a ${CAPABILITIES}" > $path/CAPABILITIES
echo "================================================" >> $path/CAPABILITIES
cat $path/APPROVED_BUILDERS.property | grep SUPER_CAPABILITIES >> $path/CAPABILITIES
echo "================================================" >> $path/CAPABILITIES
;;

*)
echo "WARNING** Detected an Outlier BUILD_USER.." > $path/CAPABILITIES
;;

esac

if [[ -d "$path/$TARGET" ]];
then
cat $path/CAPABILITIES | tr '[' '\n' | tr ';' '\n' | tr ']' '\n' > $path/$TARGET/${BUILD_USER_ID}.CAPABILITIES
else
echo "Creating TARGET directory.." && mkdir -p $path/$TARGET/
cat $path/CAPABILITIES | tr '[' '\n' | tr ';' '\n' | tr ']' '\n' > $path/$TARGET/${BUILD_USER_ID}.CAPABILITIES
fi

rm -rf $path/CAPABILITIES
fi






