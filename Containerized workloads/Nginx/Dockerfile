FROM nginx:mainline-alpine

#COPY ./server.cert /ssl/
#COPY ./server.key /ssl/
#COPY ./nginx.conf /etc/nginx/conf.d/default.conf

RUN rm /etc/nginx/conf.d/*
ADD nginx.conf /etc/nginx/conf.d/
ADD index.html /usr/share/nginx/html/