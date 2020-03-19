#!/bin/sh

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
sudo apt-get install python3.7 &&

# Set up virtual environment
cd rpi-files &&
python3 -m venv env &&

# Install requirements
source env/bin/activate &&
pip install -r requirements.txt &&
deactivate
