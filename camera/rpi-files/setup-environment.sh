#!/bin/sh

OPENAPI_GENERATOR_VERSION="4.2.3"
API_PATH="../../specs/api.yaml"

PIPENV="~/.local/bin/pipenv"

# Force running from the same directory
SCRIPT_PATH="$(dirname "$(realpath "$0")")"
if [ "$PWD" != "${SCRIPT_PATH}" ]; then
    echo >&2 "Error: Script called from a different directory" 
    exit 1
fi

# Install Python, OpenJDK and wget
sudo apt-get update &&
sudo apt-get install python3.7 python3-pip python3-setuptools python3-wheel openjdk-11-jre wget &&
pip3 install --user pipenv &&
alias pipenv="${PIPENV_PATH}" &&

# Download API generator
wget "https://repo1.maven.org/maven2/org/openapitools/openapi-generator-cli/${OPENAPI_GENERATOR_VERSION}/openapi-generator-cli-${OPENAPI_GENERATOR_VERSION}.jar" -O "openapi-generator-cli.jar" &&

# Generate api-client for the first time
rm -rf ./api-client/ &&
java -jar openapi-generator-cli.jar generate -i "${API_PATH}" -g python -o api-client/ &&

# Set up virtual environment
${PIPENV} install
