#!/bin/bash
# This script assumes you have python3, pip and pipenv installed
# you can install pipenv using pip3 install pipenv
echo "Starting installation of virtual environment"
cd server
pipenv install
pipenv shell
echo "Done"

