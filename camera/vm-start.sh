#!/bin/sh

# Load defines
SCRIPT_PATH="$(dirname "$(realpath "$0")")"
. "$SCRIPT_PATH/vm-defines.sh"

# Start machine and attach webcam
vboxmanage startvm "${VM_NAME}" &&
vboxmanage controlvm "${VM_NAME}" webcam attach .0 &&

# Enter SSH
echo "Waiting for SSH agent to start. Connecting..."
ssh -p "${SSH_PORT}" pi@127.0.0.1
