#!/bin/bash

# See https://stackoverflow.com/a/17841619/1775120
function join {
    local IFS="$1";
    shift; 
    echo "$*";
}

SOURCES=(
    "api_connector.py" \
    "data_archiver.py" \
    "data_batcher.py" \
    "events.py" \
    "frame_ingestor.py" \
    "main.py" \
    "people_counter.py" \
    "people_recognizer.py"
)

TESTS=(
    "test_csv_archiver.py" \
    "test_data_batcher.py" \
    "test_frame_ingestor.py"
)

ALL_FILES=$(IFS=$' '; echo "${SOURCES[*]} ${TESTS[*]}")
ALL_TESTS=$(IFS=$' '; echo "${TESTS[*]}")
