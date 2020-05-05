#!/bin/sh

# Force running from the same directory
SCRIPT_PATH="$(dirname "$(realpath "$0")")"
if [ "$PWD" != "${SCRIPT_PATH}" ]; then
    echo >&2 "Error: Script called from a different directory" 
    exit 1
fi

# Enable and start SSH
sudo systemctl enable ssh &&
sudo systemctl start ssh &&

# Setup environment
./setup-environment.sh
