#!/bin/bash

source "defines.sh"

isort ${ALL_FILES} &&
black -t py37 -S -l 79 ${ALL_FILES} &&
flake8 --config=setup.cfg ${ALL_FILES} &&
pytest ${ALL_TESTS}
