#!/bin/sh

OPENAPI_GENERATOR_VERSION="4.2.3"

# Force running from the same directory
if [ "$PWD" != "${SCRIPT_PATH}" ]; then
    echo >&2 "Error: Script called from a different directory" 
    exit 1
fi

# Enable and start SSH
sudo systemctl enable ssh &&
sudo systemctl start ssh &&

# Install Python
sudo apt-get update &&
sudo apt-get install python3.7 openjdk-11-jdk &&

# Set up virtual environment
python3 -m venv env &&

# Install requirements
source env/bin/activate &&
pip install -r requirements.txt &&
deactivate

# Download API generator
wget "https://repo1.maven.org/maven2/org/openapitools/openapi-generator-cli/${OPENAPI_GENERATOR_VERSION}/openapi-generator-cli-${OPENAPI_GENERATOR_VERSION}.jar" -O "openapi-generator-cli.jar"
./generate-api-client.sh
