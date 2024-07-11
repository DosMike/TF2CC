#!/usr/bin/env bash

set -e
scriptdir="$(dirname "$(realpath "${BASH_SOURCE[0]}")")"
cd "${scriptdir:?}"

echo Minify JS
uglifyjs --compress --mangle -o script/alerty.min.js -- script/alerty.js
uglifyjs --compress --mangle -o script/app.min.js -- script/app.js
uglifyjs --compress --mangle -o script/request.min.js -- script/request.js

echo Minify CSS
css-minify -o css --file css/style.css
