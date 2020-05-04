#!/bin/sh

rm -rf ./api-client/

java -jar openapi-generator-cli.jar generate \
   -i ../../specs/api.yaml \
   -g python \
   -o api-client/

pipenv install -e api-client
