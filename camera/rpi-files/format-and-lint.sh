#!/bin/sh

SRC="main.py"

isort ${SRC}
black -t py37 -S -l 79 ${SRC}
flake8 --config=setup.cfg ${SRC}
