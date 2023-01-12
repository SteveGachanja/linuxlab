# My first REST API
## Pip installations - please read the requiremens file
'''
pip install Flask

python app.py
'''
## Description of Implementation
This project is : 
1. Implemented using Flask a Python package , and is a REST API for a Store.
2. Deployed on Docker

''' 
    - docker commands
    - docker build -t coopdockerhub/python-api:devbuild_by_stevegachanja .  (note the dot at the end)
	- docker run -d -p 8080:5000 coopdockerhub/python-api:devbuild_by_stevegachanja 
	   - note mapping is from host (left) to container (right)
	   - map the application port  app.run(host='0.0.0.0', port=5000, debug=True)
	   - (my local) docker is configured to use the default machine with IP 192.168.99.105
'''

## OTHER USEFUL HACKS
-----------------------------------------------------------------
SAWarning: 

relationship 'StoreModel.items' will copy column stores.id to column items.store_id,
which conflicts with relationship(s): 'ItemModel.store' (copies stores.id to items.store_id). 

If this is not the intention, consider if these relationships should be linked with back_populates, 
or if viewonly=True should be applied to one or more if they are read-only. 

For the less common case that foreign key constraints are partially overlapping, the orm.foreign() annotation can be used to isolate the columns that should be written towards.

To silence this warning, add the parameter 'overlaps="store"' to the 'StoreModel.items' relationship.

-----------------------------------------------------------------
Useful database authorization access hacks
-----------------------------------------------------------------

mysql -u root -p

USE mysql;
CREATE USER 'username_api'@'172.30.26.38' IDENTIFIED BY 'P@ssW0rd';
GRANT ALL ON *.* TO 'user'@'localhost';
FLUSH PRIVILEGES;

-----------------------------------------------------------------
DOCKER
-----------------------------------------------------------------

tail live logs 
docker logs --follow <container ID>
docker logs --follow --details 3fd70d4ac2d6


-----------------------------------------------------------------
URLS
-----------------------------------------------------------------
https://dev.to/sid86dev/a-complete-beginner-friendly-python-flask-tutorial-learn-from-basic-template-rendering-to-deploying-in-web-servers-5ack