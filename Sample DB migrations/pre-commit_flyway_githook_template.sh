#!/bin/sh

echo 'Running pre-commit hook'

#scanning function
prog() {
    local w=80 p=$1;  shift
    # create a string of spaces, then change them to dots
    printf -v dots "%*s" "$(( $p*$w/100 ))" ""; dots=${dots// /.};
    # print those dots on a fixed-width space plus the percentage etc.
    printf "\r\e[K|%-*s| %3d %% %s" "$w" "$dots" "$p" "$*";
}


JAVA_PATH=`git config java.path`

printf "\n"
printf "\n"

#git add test-hook2
#************* Configuration **************

#Set the MIGRATION_PATH to the directory where you store all the *{up,down}.sql files\

#SQL MIGRATION SCRIPTS - PACKAGES


PACKAGES_MIGRATION_COOP_ODS_TO_PATH="db/migrations/Packages/COOP_ODS"
#PACKAGES_MIGRATION_COOP_STG_TO_PATH="db/migrations/Packages/COOP_STG"
#PACKAGES_MIGRATION_COOP_EWD_TO_PATH="db/migrations/Packages/COOP_EWD"

#SQL MIGRATION SCRIPTS
TABLE_MIGRATION_TO_PATH="db/sql/migrations/original-sql"


#Functions
FUNCTIONS_MIGRATION_TO_PATH="db/migrations/Functions"

#Procedures
PROCEDURES_MIGRATION_TO_PATH="db/migrations/Procedures"

#Triggers
TRIGGERS_MIGRATION_TO_PATH="db/migrations/Triggers"

#VIEWS
VIEWS_MIGRATION_TO_PATH="db/migrations/Views"


#TEMP
TEMP_MIGRATION_TO_PATH="db/migrations/temp"


#SQL
SQL_MIGRATIONS_TO_PATH="db/migrations/"

DEBUG=0

FLYWAYPREFIX='X'

JDBC_URL='jdbc:oracle:thin:@//copkdnas-c1-scan:1528/COPKBI'   

JDBC_PASSWORD='EdithJepchirchir@2030'

JDBC_USER='HMUTAI'




GIT_BRANCH=`git branch | grep \* | cut -d ' ' -f2`
#echo $GIT_BRANCH
#check which branch you are on
if [[  $GIT_BRANCH = 'master' ]];
then
#if branch is master do not run migrations
exit 0
fi

#********** End of configuration **********
#shopt -s nocasematch

printf "\n"
printf "\n"

for x in {1..100} ; do
    prog "$x" Fetching your target database configuration...
    sleep .1   # do some work here
done ; echo



#check whether JDBC has been set

if [ -z ${JDBC_URL=r+x} ];
 then
	echo "...........................................................................................................";
        echo ".                                                                                                   .";
        echo ". You have not set your database connection string..................!!!!                            .";
        echo ".                                                                                                   .";
        echo ". Set your database conection string by executing the following command:                            .";
        echo ".                                                                                                   .";
        echo ".         git config datasource.url jdbc:oracle:thin:@//IP:PORT/SID                                 .";
        echo ".                                                                                                   .";
        echo ".....................................................................................................";
#exit request
for x in {1..100} ; do
    prog "$x" exiting request...
    sleep .1   # do some work here
done ; echo
printf "\n"
       	exit -1
fi

#check whether db password has been set

if [ -z ${JDBC_PASSWORD=r+x} ];
 then
        echo ".....................................................................................................";
        echo ".                                                                                                   .";
        echo ". You have not set your database password for user ..................!!!!                     .";
        echo ".                                                                                                   .";
        echo ". Set your database password string by executing the following command:                             .";
        echo ".                                                                                                   .";
        echo ".         git config datasource.password your_password                                              .";
       	echo ".                                                                                                   .";
       	echo "You can re-run the migration by running this command git commit from your project workspace         .";
       	echo ".                                                                                                   .";
        echo ".....................................................................................................";

#exit request
for x in {1..100} ; do
    prog "$x" exiting request...
    sleep .1   # do some work here
done ; echo
printf "\n"

        exit -1
fi


#Get the short JDBC URL
JDBC_SHORT=`echo "$JDBC_URL" | cut -c 21-`
#print jdbc string

printf "\n"
        echo "......................................................................................................";
        echo ".                                                                                                    .";
        echo ". Your database is set to: $JDBC_SHORT                                                               .";
        echo ".                                                                                                    .";
        echo "......................................................................................................";
	
printf "\n"


printf "\n"
printf "\n"

for x in {1..100} ; do
    prog "$x" Preparing your new database changes for versioning...
    sleep .1   # do some work here
done ; echo
printf "\n"	


#
#test if migration to path (Where you are copying the files to) is valid.
#

echo 'Test if PACKAGES_MIGRATION_COOP_ODS_TO_PATH exists'
if [ ! -d "$PACKAGES_MIGRATION_COOP_ODS_TO_PATH" ]; then
  echo '1*** GIT-MIGRATION-HOOK: directory to copy migrations to does not exist.'
  echo '***                     The path is configurable and is currently gitRoot/'$PACKAGES_MIGRATION_COOP_ODS_TO_PATH
  exit -1
fi



# Return the list of files that have changed

mkdir $TEMP_MIGRATION_TO_PATH
#mkdir $REPORTS_MIGRATION_TO_PATH

#copy SQL staged changes to a temp folder
for ref in `git diff --name-only --staged $SQL_MIGRATIONS_TO_PATH`; 
   do
	echo 'File to copy '$ref 
   #
   # Extract the name of the File to be copied to Migration Folder. 
   #
	A=$(awk -F/ '{print $5}' <<< $ref)
	echo 'New file is' $TEMP_MIGRATION_TO_PATH/$A
   # Copy file to Destination folder.
   cp $ref $TEMP_MIGRATION_TO_PATH/$A
   
done


#copy SQL non-staged changes to a temp folder
for ref in `git diff --name-only $SQL_MIGRATIONS_TO_PATH`; 
   do
	echo 'File to copy '$ref 
   #
   # Extract the name of the File to be copied to Migration Folder. 
   #
	A=$(awk -F/ '{print $5}' <<< $ref)
	echo 'New file is' $TEMP_MIGRATION_TO_PATH/$A
   # Copy file to Destination folder.
   cp $ref $TEMP_MIGRATION_TO_PATH/$A
done

#Validate changes
printf "\n"
printf "\n"



printf "\n"
printf "\n"



rm -r $TEMP_MIGRATION_TO_PATH


printf "\n"

printf "\n"

echo 'Preparing for Database Migrations application'

# Return the list of files that have changed
# Grep only those in the Packages Migrations Migration From Path -----------------------------------------------> Package Bodies

#for ref2 in `git diff --staged --name-only | grep ^$PACKAGES_MIGRATION_TO_PATH | grep -E '*.(pkb)$'`; 
#git diff --staged --name-only | grep "^$PACKAGES_MIGRATION_COOP_ODS_TO_PATH" | grep -E '.*\.pkb$'

for ref2 in `git diff --staged --name-only | grep "^$PACKAGES_MIGRATION_COOP_ODS_TO_PATH" | grep -E '*.(pkb)$'`; 
   do
   # Generate timestamp to append to migrate path - 
   # Inspired by http://www.jeremyjarrell.com/using-flyway-db-with-distributed-version-control/
   # Also review use of idpotent. 
   #
    #TIMESTAMP=$(date +%Y%m%d%H%M%S%N)
	PACKAGESUFFIX="R"
	echo '*** COPYING FILE TO DESTINATION *** ' 
	echo '**** File to copy ****' $ref2 
	echo '***** Destination of the file' $PACKAGES_MIGRATION_COOP_ODS_TO_PATH/
   #
   # Extract the name of the File to be copied to Migration Folder. 
   #
	A2=$(awk -F/ '{print $5}' <<< $ref2)
	echo 'File Name to copy is' $A2
    echo 'TIME STAMP FOR NOW IS' $PACKAGESUFFIX
	echo 'FileName of New file is' $PACKAGES_MIGRATION_COOP_ODS_TO_PATH/$PACKAGESUFFIX'_'$A2
   # Copy file to Destination folder.  
   mv $ref2 $PACKAGES_MIGRATION_COOP_ODS_TO_PATH/$PACKAGESUFFIX'__'$A2'.sql'
   # Git Reset Head for the File just added... 
   # Version 2 git reset HEAD $ref
   
   # Add copied file to Git Repo 
  git add -A :/
   #Version 2 git reset HEAD $ref
done



# Return the list of files that have changed
# Grep only those in the Packages Migrations Migration From Path----------------------------------->Package Spec 

for ref3 in `git diff --staged --name-only | grep ^$PACKAGES_MIGRATION_COOP_ODS_TO_PATH | grep -E '*.(pks)$'`; 
   do
   # Generate timestamp to append to migrate path - 
   # Inspired by http://www.jeremyjarrell.com/using-flyway-db-with-distributed-version-control/
   # Also review use of idpotent. 
   #
    #TIMESTAMP=$(date +%Y%m%d%H%M%S%N)
	PACKAGESUFFIX2="R"
	echo '*** COPYING FILE TO DESTINATION *** ' 
	echo '**** File to copy ****' $ref3 
	echo '***** Destination of the file' $PACKAGES_MIGRATION_COOP_ODS_TO_PATH/
   #
   # Extract the name of the File to be copied to Migration Folder. 
   #
	A3=$(awk -F/ '{print $5}' <<< $ref3)
	echo 'File Name to copy is' $A3
    echo 'TIME STAMP FOR NOW IS' $PACKAGESUFFIX2
	echo 'FileName of New file is' $PACKAGES_MIGRATION_COOP_ODS_TO_PATH/$PACKAGESUFFIX2'_'$A3
   # Copy file to Destination folder.  
   mv $ref3 $PACKAGES_MIGRATION_COOP_ODS_TO_PATH/$PACKAGESUFFIX2'__'$A3'.sql'
   # Git Reset Head for the File just added... 
   # Version 2 git reset HEAD $ref
   
   # Add copied file to Git Repo 
   git add -A :/
   #Version 2 git reset HEAD $ref
done


#check when the DEV_CHANGES is equal to zero


#check whether all DLL changes have been saved to migration folders
#DB_CHANGES=`java -jar db/tools/scriptschecker.jar $JDBC_SHORT COOP_ODS $JDBC_PASSWORD`


#if dev changes is equal to zero
#if [ ${DB_CHANGES} -eq 0 ] ;
#then
#printf "\n"
#echo '> Fetching your database version...'


#check for missing migrations
migration_status_count=`db/tools/flyway/flyway -url=$JDBC_URL -user=$JDBC_USER -password=$JDBC_PASSWORD -baselineOnMigrate=true -repeatableSqlMigrationPrefix=R  -sqlMigrationPrefix=X -schemas=COOP_ODS -locations=filesystem:db/migrations/Packages/COOP_ODS   -sqlMigrationSeparator=__  -outOfOrder=true -validateOnMigrate=false info | grep Pending |wc -l`


#check whether there are missing migrations

migration_pending=`db/tools/flyway/flyway -url=$JDBC_URL -user=$JDBC_USER -password=$JDBC_PASSWORD -baselineOnMigrate=true -repeatableSqlMigrationPrefix=R  -sqlMigrationPrefix=X  -schemas=COOP_ODS -locations=filesystem:db/migrations/Packages/COOP_ODS   -sqlMigrationSeparator=__  -outOfOrder=true -validateOnMigrate=false info | grep Pending`


#check for failed migrations
migration_status_countf=`db/tools/flyway/flyway -url=$JDBC_URL -user=$JDBC_USER -password=$JDBC_PASSWORD -baselineOnMigrate=true -repeatableSqlMigrationPrefix=R -sqlMigrationPrefix=X  -schemas=COOP_ODS -locations=filesystem:db/migrations/Packages/COOP_ODS   -sqlMigrationSeparator=__  -outOfOrder=true -validateOnMigrate=false info | grep Failed |wc -l`

#check whether there are failed migrations

migration_failed=`db/tools/flyway/flyway -url=$JDBC_URL -user=$JDBC_USER -password=$JDBC_PASSWORD -baselineOnMigrate=true -repeatableSqlMigrationPrefix=R -sqlMigrationPrefix=X  -schemas=COOP_ODS -locations=filesystem:db/migrations/Packages/COOP_ODS   -sqlMigrationSeparator=__  -outOfOrder=true -validateOnMigrate=false info | grep Failed`

#check failed migrations

if [  $migration_status_countf -ne 0 ];
 then
        echo ".............................................................................................................................................";
        echo ".                                                                                                                                           .";
        echo ".We noticed that your previous database migration failed  please contact your local administrator to assist in resolving                    .";
        echo ".                                                                                                                                           .";
        echo ".............................................................................................................................................";
fi


#Display the Failed Migrations

if [[  $migration_status_countf -ne 0 ]];
 then
        echo ".............................................................................................................................................";
        echo ".                                                                                                                                           .";
        echo ".                         You have $migration_status_countf failed Migrations  from your previous migration                                 .";
        echo ".                                                                                                                                           .";
        echo ".                      The following migrations are marked as failed from your previous migration run:                                      .";
        echo ".                                                                                                                                           .";
        echo ". $migration_failed                                                                                                                         .";
        printf "\n"
        printf "\n"
        echo "Please contact your local administrator to assist in resolving                                                                               .";
        printf "\n"
        echo "You can re-run the migration by running this command git commit from your project workspace";
        printf "\n"
        exit -1
fi

#check whether there are pending migrations
if [  $migration_status_count -eq 0 ];
then
        echo ".............................................................................................................................................";
        echo ".                                                                                                                                           .";
        echo ".              No missing Migrations have been found                                                                                        .";
        echo ".                                                                                                                                           .";
        echo ".     You database $JDBC_SHORT is uptodate!!!                                                                                               .";
        echo ".                                                                                                                                           .";
        echo ".............................................................................................................................................";
exit 0
fi


#Display the pending Migrations

if [[  $migration_status_count -ne 0 ]];
 then
        echo ".............................................................................................................................................";
        echo ".                                                                                                                                           .";
        echo ".                         You have $migration_status_count missing Migrations                                                               .";
        echo ".                                                                                                                                           .";
        echo ".                      The following migrations are missing in your database:                                                               .";
        echo ".                                                                                                                                           .";
        echo ". $migration_pending                                                                                                                        .";
        printf "\n"
fi

if [[  $migration_status_count -ne 0 ]];
then
while true;     
do
        read -n1 -p "Do you want Flyway to automatically run the missing database migrations against $JDBC_SHORT (Y/N)" reply < /dev/tty;
          choice_nocase=$(echo $reply | tr '[:upper:]' '[:lower:]');
          choice=$(echo $choice_nocase|sed 's/(.*)/L1/');
if [ "$choice" = 'y' ] ;
then
        printf "\n"
        echo "Flyway will now migrate the missing migrations in the target database $JDBC_SHORT";

db/tools/flyway/flyway migrate -url=$JDBC_URL -user=$JDBC_USER -password=$JDBC_PASSWORD -baselineOnMigrate=true -repeatableSqlMigrationPrefix=R -sqlMigrationSuffixes=.sql -sqlMigrationPrefix=X -schemas=HMUTAI -locations=filesystem:db/migrations/Packages/COOP_ODS   -sqlMigrationSeparator=__ -ignoreMissingMigrations=true -outOfOrder=true -validateOnMigrate=false



printf "\n"

printf "\n"
echo '>Cleaning up.....................................'
printf "\n"


  

#check for pending migrations
final_status_count_p=`db/tools/flyway/flyway -url=$JDBC_URL -user=$JDBC_USER -password=$JDBC_PASSWORD -baselineOnMigrate=true -repeatableSqlMigrationPrefix=R -sqlMigrationSuffixes=.sql -sqlMigrationPrefix=X  -schemas=HMUTAI -locations=filesystem:db/migrations/Packages/COOP_ODS   -sqlMigrationSeparator=__ -ignoreMissingMigrations=true -outOfOrder=true -validateOnMigrate=false info | grep Pending |wc -l`

#check for failed migrations
final_status_count_f=`db/tools/flyway/flyway -url=$JDBC_URL -user=$JDBC_USER -password=$JDBC_PASSWORD -baselineOnMigrate=true -repeatableSqlMigrationPrefix=R -sqlMigrationSuffixes=.sql -sqlMigrationPrefix=X  -schemas=HMUTAI -locations=filesystem:db/migrations/Packages/COOP_ODS  -sqlMigrationSeparator=__ -ignoreMissingMigrations=true -outOfOrder=true -validateOnMigrate=false info | grep Failed |wc -l`


#List Migrations that are still Pending
final_migration_pending=`db/tools/flyway/flyway -url=$JDBC_URL -user=$JDBC_USER -password=$JDBC_PASSWORD -baselineOnMigrate=true -repeatableSqlMigrationPrefix=R -sqlMigrationSuffixes=.sql -sqlMigrationPrefix=X  -schemas=HMUTAI -locations=filesystem:db/migrations/Packages/COOP_ODS -sqlMigrationSeparator=__ -ignoreMissingMigrations=true -outOfOrder=true -validateOnMigrate=false info | grep Pending`


#List Migrations that have Failed
final_migration_failed=`db/tools/flyway/flyway -url=$JDBC_URL -user=$JDBC_USER -password=$JDBC_PASSWORD -baselineOnMigrate=true -repeatableSqlMigrationPrefix=R -sqlMigrationSuffixes=.sql -sqlMigrationPrefix=X  -schemas=HMUTAI -locations=filesystem:db/migrations/Packages/COOP_ODS  -sqlMigrationSeparator=__ -ignoreMissingMigrations=true -outOfOrder=true -validateOnMigrate=false info | grep Failed`


if [[  $final_status_count_f -ne 0 ]];
 then
        echo "...............................................................................................................................";
        echo ".                                                                                                                             .";
        echo ".We have noticed that $final_status_count_f migrations have failed to migrate due to above errors!!!                          .";
        echo ".                                                                                                                             .";
        echo ".The following migrations have failed due to the above errors:                                                                .";
        echo ".                                                                                                                             .";
        echo ". $final_migration_failed                                                                                                     .";
        printf "\n"
        echo ".We have noticed that $final_status_count_f migrations have failed to migrate due to above errors!!!                          .";
        printf "\n"
        echo "Please Contact you local administrator to help in resolving your pending database migrations..."
        printf "\n"
        printf "\n"; echo "The process will now exit!!!."; printf "\n"; echo "You can re-run the migration by running this command git commit from your project workspace";
        printf "\n";
exit -1
fi

if [  $final_status_count_p -eq 0 ];
 then
        echo "..................................................................................................................";
        echo ".                                                                                                                .";
        echo ".         You database $JDBC_SHORT is now uptodate!!!                                                            .";
  echo ".                                                                                                                .";
        echo "..................................................................................................................";
exit 0
fi


if [[  $final_status_count_p -ne 0 ]];
 then
        echo "...................................................................................................................";
        echo ".                                                                                                                 .";
        echo ".We have noticed that $final_status_count_p migrations are still pending!!!                                       .";
        echo ".                                                                                                                 .";
        echo ".The following  migrations are still pending:                                                                     .";
        echo ".                                                                                                                 .";
        echo ". $final_migration_pending                                                                                        .";
        printf "\n"
        echo ".We have noticed that $final_status_count_p migrations are still Pending!!!                                       .";
        printf "\n"
       echo "Please Contact you local administrator to help in resolving your pending database migrations!!!"
        printf "\n"
        printf "\n";
        echo "The process will now exit!!!."; printf "\n"; echo "You can re-run the migration by running this command git commit from your project workspace";
        exit -1
fi


elif [ "$choice" = 'n'  ]
then
printf "\n"
printf "\n"; echo "The process will now exit!!!."; printf "\n"; echo "You can re-run the migration by running this command git commit from your project workspace";
printf "\n"
  sleep 3
  exit -1 
else
printf "\n"
        echo "invalid answer, please type y or n";
printf "\n"
fi
done
fi

#close of first if loop
printf "\n"
fi
#check when the db_changes is greater than zero

if [ ${DB_CHANGES} -ne 0 ] ;
then

        echo ".....................................................................................................";
        echo ".                                                                                                   ."
        echo ".    We noticed that you have not marked $DB_CHANGES as saved in the table DB_CHANGES               .";
        echo ".                                                                                                   ."
	      echo ".     You risk losing your unsaved changes if you continue                                          .";
        echo ".                                                                                                   ."
	      echo ".....................................................................................................";
printf "\n"
fi

printf "\n"
printf "\n"
if [ ${DB_CHANGES} -gt 0 ] ;
then
while true;     
do
        read -p "Do you wish to continue? (Y/N)" reply1 < /dev/tty;
          choice1_nocase=$(echo $reply1 | tr '[:upper:]' '[:lower:]');
          choice1=$(echo $choice1_nocase|sed 's/(.*)/L1/');

printf "\n"

if [ "$choice1" = 'y' ] ;
then

printf "\n"

for x in {1..100} ; do
    prog "$x" Fetching your database version...
    sleep .1   # do some work here

done ; echo
printf "\n"


#count the number of missing migrations

migration_status_count=`db/tools/flyway/flyway -url=$JDBC_URL -user=$JDBC_USER -password=$JDBC_PASSWORD -baselineOnMigrate=true -repeatableSqlMigrationPrefix=R  -locations=filesystem:db/migrations/Packages/COOP_ODS   -sqlMigrationSeparator=__ -outOfOrder=true -validateOnMigrate=false info | grep Pending |wc -l`

#check whether there are missing migrations

migration_pending=`db/tools/flyway/flyway -url=$JDBC_URL -user=$JDBC_USER -password=$JDBC_PASSWORD -baselineOnMigrate=true -repeatableSqlMigrationPrefix=R  -locations=filesystem:db/migrations/Packages/COOP_ODS   -sqlMigrationSeparator=__ -outOfOrder=true -validateOnMigrate=false info | grep Pending`




#check for failed migrations
migration_status_countf=`db/tools/flyway/flyway -url=$JDBC_URL -user=$JDBC_USER -password=$JDBC_PASSWORD -baselineOnMigrate=true -repeatableSqlMigrationPrefix=R  -locations=filesystem:db/migrations/Packages/COOP_ODS  -sqlMigrationSeparator=__ -outOfOrder=true -validateOnMigrate=false info | grep Failed |wc -l`

#check whether there are failed migrations

migration_failed=`db/tools/flyway/flyway -url=$JDBC_URL -user=$JDBC_USER -password=$JDBC_PASSWORD -baselineOnMigrate=true -repeatableSqlMigrationPrefix=R  -locations=filesystem:db/migrations/Packages/COOP_ODS -sqlMigrationSeparator=__ -outOfOrder=true -validateOnMigrate=false info | grep Failed`

#check failed migrations

if [  $migration_status_countf -ne 0 ];
 then
        echo ".............................................................................................................................................";
        echo ".                                                                                                                                           .";
        echo ".We noticed that your previous database migration failed  please contact your local administrator to assist in resolving                    .";
        echo ".                                                                                                                                           .";
        echo ".............................................................................................................................................";
fi

#Display the Failed Migrations

if [[  $migration_status_countf -ne 0 ]];
 then
        echo ".............................................................................................................................................";
        echo ".                                                                                                                                           .";
        echo ".                         You have $migration_status_countf failed Migrations  from your previous migration                                 .";
        echo ".                                                                                                                                           .";
        echo ".                      The following migrations are marked as failed from your previous migration run:                                      .";
        echo ".                                                                                                                                           .";
        echo ". $migration_failed                                                                                                                         .";
        printf "\n"
        printf "\n"
        echo "Please contact your local administrator to assist in resolving                                                                               .";
        printf "\n"
        echo "You can re-run the migration by running this command git commit from your project workspace";
        printf "\n"
        exit -1
fi

#check whether there are pending migrations
if [  $migration_status_count -eq 0 ];
then
        echo ".............................................................................................................................................";
        echo ".                                                                                                                                           .";
        echo ".              No missing Migrations have been found                                                                                        .";
        echo ".                                                                                                                                           .";
        echo ".     You database $JDBC_SHORT is uptodate!!!                                                                                               .";
        echo ".                                                                                                                                           .";
        echo ".............................................................................................................................................";
exit 0
fi


#Display the pending Migrations

if [[  $migration_status_count -ne 0 ]];
 then
        echo ".............................................................................................................................................";
        echo ".                                                                                                                                           .";
        echo ".                         You have $migration_status_count missing Migrations                                                               .";
        echo ".                                                                                                                                           .";
        echo ".                      The following migrations are missing in your database:                                                               .";
        echo ".                                                                                                                                           .";
        echo ". $migration_pending                                                                                                                        .";
        printf "\n"
fi


if [[  $migration_status_count -ne 0 ]];
then
while true;     
do
        read -n1 -p "Do you want Flyway to automatically run the missing database migrations against $JDBC_SHORT (Y/N)" reply < /dev/tty;
        choice_nocase=$(echo $reply | tr '[:upper:]' '[:lower:]');
        choice=$(echo $choice_nocase|sed 's/(.*)/L1/');
if [ "$choice" = 'y' ] ;
then
        printf "\n"
        echo "Flyway will now migrate the missing migrations in the target database $JDBC_SHORT";

db/tools/flyway/flyway migrate -url=$JDBC_URL -user=$JDBC_USER -password=$JDBC_PASSWORD -baselineOnMigrate=true -repeatableSqlMigrationPrefix=R -sqlMigrationSuffix=.sql -sqlMigrationPrefix=X  -locations=filesystem:db/migrations/Packages   -sqlMigrationSeparator=__ -ignoreMissingMigrations=true -outOfOrder=true -validateOnMigrate=false

printf "\n"

for x in {1..100} ; do
    prog "$x" Cleaning up.....
    sleep .1   # do some work here
done ; echo
printf "\n"

#check for pending migrations
final_status_count_p=`db/tools/flyway/flyway -url=$JDBC_URL -user=$JDBC_USER -password=$JDBC_PASSWORD -baselineOnMigrate=true -repeatableSqlMigrationPrefix=R -sqlMigrationSuffixes=.sql -sqlMigrationPrefix=X  -locations=filesystem:db/migrations/Packages/COOP_ODS   -sqlMigrationSeparator=__ -ignoreMissingMigrations=true -outOfOrder=true -validateOnMigrate=false info | grep Pending |wc -l`

#check for failed migrations
final_status_count_f=`db/tools/flyway/flyway -url=$JDBC_URL -user=$JDBC_USER -password=$JDBC_PASSWORD -baselineOnMigrate=true -repeatableSqlMigrationPrefix=R -sqlMigrationSuffixes=.sql -sqlMigrationPrefix=X  -locations=filesystem:db/migrations/Packages/COOP_ODS   -sqlMigrationSeparator=__ -ignoreMissingMigrations=true -outOfOrder=true -validateOnMigrate=false info | grep Failed |wc -l`


#List Migrations that are still Pending
final_migration_pending=`db/tools/flyway/flyway -url=$JDBC_URL -user=$JDBC_USER -password=$JDBC_PASSWORD -baselineOnMigrate=true -repeatableSqlMigrationPrefix=R -sqlMigrationSuffixes=.sql -sqlMigrationPrefix=X  -locations=filesystem:db/migrations/Packages/COOP_ODS   -sqlMigrationSeparator=__ -ignoreMissingMigrations=true -outOfOrder=true -validateOnMigrate=false info | grep Pending`


#List Migrations that have Failed
final_migration_failed=`db/tools/flyway/flyway -url=$JDBC_URL -user=$JDBC_USER -password=$JDBC_PASSWORD -baselineOnMigrate=true -repeatableSqlMigrationPrefix=R -sqlMigrationSuffixes=.sql -sqlMigrationPrefix=X  -locations=filesystem:db/migrations/Packages/COOP_ODS  -sqlMigrationSeparator=__ -ignoreMissingMigrations=true -outOfOrder=true -validateOnMigrate=false info | grep Failed`


if [[  $final_status_count_f -ne 0 ]];
 then
        echo "...............................................................................................................................";
        echo ".                                                                                                                             .";
        echo ".We have noticed that $final_status_count_f migrations have failed to migrate due to above errors!!!                          .";
        echo ".                                                                                                                             .";
        echo ".The following migrations have failed due to the above errors:                                                                .";
        echo ".                                                                                                                             .";
        echo ". $final_migration_failed                                                                                                     .";
        printf "\n"
        echo ".We have noticed that $final_status_count_f migrations have failed to migrate due to above errors!!!                          .";
        printf "\n"
        echo "Please Contact you local administrator to help in resolving your pending database migrations..."
        printf "\n"
        printf "\n"; echo "The process will now exit!!!."; printf "\n"; echo "You can re-run the migration by running this command git commit from your project workspace";
        printf "\n";
exit -1
fi

if [  $final_status_count_p -eq 0 ];
 then
        echo "..................................................................................................................";
        echo ".                                                                                                                .";
        echo ".         You database $JDBC_SHORT is now uptodate!!!                                                            .";
	echo ".                                                                                                                .";
        echo "..................................................................................................................";
exit 0
fi


if [[  $final_status_count_p -ne 0 ]];
 then
        echo "...................................................................................................................";
        echo ".                                                                                                                 .";
        echo ".We have noticed that $final_status_count_p migrations are still pending!!!                                       .";
        echo ".                                                                                                                 .";
        echo ".The following  migrations are still pending:                                                                     .";
        echo ".                                                                                                                 .";
        echo ". $final_migration_pending                                                                                        .";
        printf "\n"
        echo ".We have noticed that $final_status_count_p migrations are still Pending!!!                                       .";
        printf "\n"
       echo "Please Contact you local administrator to help in resolving your pending database migrations!!!"
        printf "\n"
        printf "\n";
        echo "The process will now exit!!!."; printf "\n"; echo "You can re-run the migration by running this command git commit from your project workspace";
        exit -1
fi


elif [ "$choice" = 'n'  ]
then
printf "\n"
printf "\n"; echo "The process will now exit!!!."; printf "\n"; echo "You can re-run the migration by running this command git commit from your project workspace";
printf "\n"
  sleep 3
  exit -1 
else
printf "\n"
        echo "invalid answer, please type y or n";
printf "\n"
fi
done
fi


elif [ "$choice1" = 'n'  ]
then
printf "\n"

printf "\n"; echo "The process will now exit!!!."; printf "\n"; echo "You can re-run the migration by running this command git commit from your project workspace";

printf "\n"

sleep 3
  exit -1
else
printf "\n"
        echo "invalid answer, please type y or n";
printf "\n"
fi
done
fi


exit -1

# Turn on debugging if need be. 
 
if [ $DEBUG -eq 1 ]; then
	echo '*** DEBUGGING IS ON *** ' 
	exit -1
fi


