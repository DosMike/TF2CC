FROM httpd:2.4-alpine

RUN apk update; apk upgrade;

# Copy Apache virtual host file to proxy .php requests to PHP container
COPY apache-vhost.conf /usr/local/apache2/conf/apache-vhost.conf
RUN echo "Include /usr/local/apache2/conf/apache-vhost.conf" \
    >> /usr/local/apache2/conf/httpd.conf
