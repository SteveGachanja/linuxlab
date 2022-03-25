#!/bin/bash
echo "deploy backend web"
kubectl apply -f ./qotd-python/k8s/quotes-deployment.yaml
kubectl apply -f ./qotd-python/k8s/service.yaml
kubectl apply -f ./qotd-python/k8s/route.yaml

sleep 3
echo "deploy frontend web"
kubectl apply -f ./quotesweb/k8s/quotesweb-deployment.yaml
kubectl apply -f ./quotesweb/k8s/quotesweb-service.yaml
kubectl apply -f ./quotesweb/k8s/quotesweb-route.yaml

sleep 3
echo "Scale the deployment"
kubectl scale deployments/quotesweb --replicas=3

sleep 3
echo "Create a Persistent Volume Claim"
kubectl apply -f ./quotemysql/mysqlvolume.yaml

sleep 20
echo "Create a Secret to be used to create the database."
kubectl apply -f ./quotemysql/mysql-secret.yaml

sleep 3
echo "Create a MariaDB database app"
kubectl delete -f ./quotemysql/mysql-deployment.yaml
kubectl apply -f ./quotemysql/mysql-deployment.yaml
kubectl expose deploy/mysql --name mysql --port 3306 --type NodePort

echo "Get pod MariaDB logs"
podname=`kubectl get pods | grep mysql | awk -F " " '{print $01}' | tr -d [:blank:]`
kubectl get pods
kubectl logs -f ${podname}

sleep 3
echo "Copy the database creation commands into the pod and execute the script.   "
kubectl cp ./quotemysql/create_database_quotesdb.sql ${podname}:/tmp/create_database_quotesdb.sql
#kubectl cp ./quotemysql/create_database.sh ${podname}:/tmp/create_database.sh

echo "Copy the table creating commands into the pod and execute the script."
kubectl cp ./quotemysql/create_table_quotes.sql ${podname}:/tmp/create_table_quotes.sql
#kubectl cp ./quotemysql/create_tables.sh ${podname}:/tmp/create_tables.sh

echo "Populate the table"
kubectl cp ./quotemysql/populate_table_quotes_POWERSHELL.sql ${podname}:/tmp/populate_table_quotes_POWERSHELL.sql
kubectl cp ./quotemysql/quotes.csv ${podname}:/tmp/quotes.csv
#kubectl cp ./quotemysql/populate_tables_BASH.sh ${podname}:/tmp/populate_tables_BASH.sh

echo "Query table to prove our work"
kubectl cp ./quotemysql/query_table_quotes.sql ${podname}:/tmp/query_table_quotes.sql
#kubectl cp ./quotemysql/query_table_quotes.sh ${podname}:/tmp/query_table_quotes.sh

echo "List all the files in tmp"
kubectl exec ${podname} -- ls tmp

echo "Next Setup database scripts"
#kubectl exec $podname -it -- ls -t tmp 
#kubectl exec $podname date

# Get output from running 'date' in mariadb-container from pod podname
#kubectl exec $podname -c mariadb date 

# Switch to raw terminal mode, sends stdin to 'bash' in mariadb-container from pod podname # and sends stdout/stderr from 'bash' back to the client
#kubectl exec $podname -c mariadb -it -- bash -il

echo "Populating the database"

echo "Update backend program (quotes) to Version 2"
kubectl set image deploy quotes quotes=quay.io/donschenck/quotes:v2

echo "TEST MariaDB failover "
kubectl delete pod ${podname}

