FROM nginx

COPY ./server.cert /ssl/
COPY ./server.key /ssl/
COPY ./nginx.conf /etc/nginx/conf.d/default.conf
COPY ./index.html /usr/share/nginx/html/index.html
COPY ./printhostname.sh /usr/share/nginx/



##  REGENERATE CERT PROCESS and specify number of days
#sudo openssl req  -nodes -new -x509 -days 365 -keyout server.key -out server.cert