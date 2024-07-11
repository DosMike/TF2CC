# This container is only inteded to update the minified files

FROM alpine:3.19

RUN apk update && apk add bash nodejs npm
RUN npm install uglify-js -g
RUN npm install css-minify -g

WORKDIR /var/www/html
ENTRYPOINT [ "/bin/bash", "-c", "./docker_run.sh" ]
