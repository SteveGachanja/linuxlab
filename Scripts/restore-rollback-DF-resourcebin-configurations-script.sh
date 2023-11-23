#!/bin/bash
ENV=$ENV
deploy_path=/app/mservers/user_projects/domains/RBX_HOME/RBXStaging/serverclasses
echo ENVIRONMENT $ENV
echo JENKINS_PATH $WORKSPACE

cd $WORKSPACE
git_commit_date=$(git log -1 --format=%cd --date=short)

for restore_dir in $($ENV ls -rt $deploy_path 2>/dev/null | grep Resources.backup)
do
backup=$deploy_path/$restore_dir
timestamp=$($ENV date -r $backup "+%Y-%m-%d" 2>/dev/null)
echo $backup - $timestamp

if [ $timestamp = $git_commit_date ];
then
echo ""
echo "[ BINGO! This is the Restore Dir we are looking for ]"
echo "Git_commit_date" $git_commit_date
echo "Directory Timestamp" $timestamp
echo ""
echo "[ Deploy Restore ]"
echo "Restore_path" $backup
$ENV rm -rf $deploy_path/Resources 2>/dev/null
$ENV cp -r $backup $deploy_path/Resources 2>/dev/null
echo ""
fi

done
