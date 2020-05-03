#!/bin/bash

# See https://stackoverflow.com/a/17841619/1775120
function join {
    local IFS="$1";
    shift; 
    echo "$*";
}

SOURCES=(
    "main.py" \
    "people_counter.py" \
    "data_batcher.py" \
)

TESTS=(
    "test_data_batcher.py"
)

ALL_FILES=$(IFS=$' '; echo "${SOURCES[*]} ${TESTS[*]}")

isort ${ALL_FILES} &&
black -t py37 -S -l 79 ${ALL_FILES} &&
flake8 --config=setup.cfg ${ALL_FILES} &&
pytest ${TESTS}
