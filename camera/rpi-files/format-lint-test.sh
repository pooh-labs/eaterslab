#!/bin/bash

# See https://stackoverflow.com/a/17841619/1775120
function join {
    local IFS="$1";
    shift; 
    echo "$*";
}

SOURCES=(
    "main.py" \
    "data_archiver.py" \
    "data_batcher.py" \
    "frame_ingestor.py" \
    "people_counter.py"
)

TESTS=(
    "test_csv_archiver.py" \
    "test_data_batcher.py" \
    "test_frame_ingestor.py"
)

ALL_FILES=$(IFS=$' '; echo "${SOURCES[*]} ${TESTS[*]}")
ALL_TESTS=$(IFS=$' '; echo "${TESTS[*]}")

isort ${ALL_FILES} &&
black -t py37 -S -l 79 ${ALL_FILES} &&
flake8 --config=setup.cfg ${ALL_FILES}
pytest ${ALL_TESTS}
