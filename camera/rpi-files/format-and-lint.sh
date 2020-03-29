#!/bin/sh

SRC="main.py people_counter.py data_batcher.py"

isort ${SRC}
black -t py37 -S -l 79 ${SRC}
flake8 --config=setup.cfg ${SRC}
