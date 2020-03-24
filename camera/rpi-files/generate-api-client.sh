#!/bin/sh

java -jar openapi-generator-cli.jar generate \
   -i ../../specs/api.yaml \
   -g python \
   -o api-client/

cd api-client &&
python3 setup.py install &&
cd -
