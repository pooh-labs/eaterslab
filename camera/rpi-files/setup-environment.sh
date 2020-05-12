#!/bin/sh

OPENAPI_GENERATOR_VERSION="4.2.3"
OPENAPI_GENERATOR_DOWNLOAD_PATH="https://repo1.maven.org/maven2/org/openapitools/openapi-generator-cli/${OPENAPI_GENERATOR_VERSION}/openapi-generator-cli-${OPENAPI_GENERATOR_VERSION}.jar"
API_PATH="../../specs/api.yaml"
API_GENERATION_PATH="api-client"

# Force running from the same directory
SCRIPT_PATH="$(dirname "$(realpath "$0")")"
if [ "$PWD" != "${SCRIPT_PATH}" ]; then
    echo >&2 "Error: Script called from a different directory" 
    exit 1
fi

# Install Python, OpenJDK and wget
sudo apt-get install python3.7 python3-pip python3-setuptools openjdk-11-jre-headless wget &&
sudo pip3 install pipenv &&

# Download API generator
wget --quiet "${OPENAPI_GENERATOR_DOWNLOAD_PATH}" -O "openapi-generator-cli.jar" &&

# Generate api-client for the first time
rm -rf "${API_GENERATION_PATH}" &&
java -jar openapi-generator-cli.jar generate -i "${API_PATH}" -g python -o "${API_GENERATION_PATH}" &&

# Set up virtual environment
pipenv install

# Set up paths
mkdir -p archives
