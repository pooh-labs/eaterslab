#!/bin/bash
# This script assumes you have python3, pip and virtualenv installed
# (Sometimes you have to apt-get install python3-venv)
# https://packaging.python.org/guides/installing-using-pip-and-virtual-environments/
echo "Starting installation"
python3 -m venv serverenv
source serverenv/bin/activate
pip install django
pip install django-environ
pip install djangorestframework
echo "Done"

