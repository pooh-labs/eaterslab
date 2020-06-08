#!/bin/sh

echo "Konfiguracja kamery"
read -p 'Camera number [e.g. 1]: ' camera_pk
read -p 'Camera name [e.g. cam-1]: ' device_id
read -p 'Camera API key [e.g. XXX]: ' api_key
read -p 'Timezone [e.g. Europe/Warsaw]: ' timezone

sudo timedatectl set-timezone "${timezone}"

FILE="test.env"
echo "# API connection" > "${FILE}"
echo "API_HOST=https://eaterslab.herokuapp.com/api/beta" >> "${FILE}"
echo "CAMERA_PK=${camera_pk}" >> "${FILE}"
echo "DEVICE_ID=${device_id}" >> "${FILE}"
echo "API_KEY=${timezone}" >> "${FILE}"

cat <<EOF >>test.env

# Archives
ARCHIVES_DIR=archives

# Input frames
FRAME_SOURCE=1
WEBCAM_STREAM_NUM=0

# Recognition parameters
SKIP_FRAMES=30
NET_PROTOTXT_PATH="net/MobileNetSSD_deploy.prototxt"
NET_MODEL_PATH="net/MobileNetSSD_deploy.caffemodel"
NET_MIN_CONFIDENCE=0.4
MAX_FRAME_WIDTH=500
EOF
