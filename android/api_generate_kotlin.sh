#!/bin/bash

if [[ $# -ne 1 ]]; then
    echo "Illegal number of parameters. Usage: ./api_generate_kotlin.sh api.yaml"
    exit 1
fi

GENERATOR_VERSION='4.2.3'

wget https://repo1.maven.org/maven2/org/openapitools/openapi-generator-cli/${GENERATOR_VERSION}/openapi-generator-cli-${GENERATOR_VERSION}.jar -O openapi-generator-cli.jar &&

rm -rf ./api &&
mkdir api &&

java -jar openapi-generator-cli.jar generate \
     -g kotlin \
     -i $1 \
     -o api \
     -p groupId='labs.pooh' -p modelMutable='false' -p packageName='labs.pooh.client' -p serializationLibrary='gson' -p dateLibrary='threetenbp' &&

rm openapi-generator-cli.jar &&
rm -rf app/src/main/java/labs/pooh/eaterslab/client &&
cp -r api/src/main/kotlin/labs/pooh/client app/src/main/java/labs/pooh/eaterslab/ &&
rm -rf ./api &&
git add app/src/main/java/labs/pooh/eaterslab/client
