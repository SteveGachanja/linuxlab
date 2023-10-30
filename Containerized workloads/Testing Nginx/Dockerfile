FROM nginx

COPY ./server.cert /ssl/
COPY ./server.key /ssl/
COPY ./nginx.conf /etc/nginx/conf.d/default.conf
COPY ./index.html /usr/share/nginx/html/index.html
COPY ./printhostname.sh /usr/share/nginx/