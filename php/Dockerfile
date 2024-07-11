FROM php:8.2-fpm

RUN apt update && \
    apt install -y zip libzip-dev libpng-dev libicu-dev libxml2-dev

RUN docker-php-ext-install mysqli pdo pdo_mysql gd zip intl xml

RUN apt clean
