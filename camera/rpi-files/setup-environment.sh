#!/bin/sh

OPENAPI_GENERATOR_VERSION="4.2.3"

# Force running from the same directory
SCRIPT_PATH="$(dirname "$(realpath "$0")")"
if [ "$PWD" != "${SCRIPT_PATH}" ]; then
    echo >&2 "Error: Script called from a different directory" 
    exit 1
fi

# Install Python, OpenJDK and wget
sudo apt-get update &&
sudo apt-get install python3.7 openjdk-11-jdk wget &&

# Set up virtual environment
pip install pipenv &&
pipenv install &&

# Download API generator
wget "https://repo1.maven.org/maven2/org/openapitools/openapi-generator-cli/${OPENAPI_GENERATOR_VERSION}/openapi-generator-cli-${OPENAPI_GENERATOR_VERSION}.jar" -O "openapi-generator-cli.jar" &&
./generate-api-client.sh
