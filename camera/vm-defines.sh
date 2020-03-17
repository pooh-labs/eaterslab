#!/bin/sh

# Constants
PROJECT_NAME=ivory-beam
VM_NAME="${PROJECT_NAME}-camera"

# Default values for VMs
DISK_SIZE=14336
RAM_SIZE=512
VM_DIR="./vm-files"
ENABLE_KVM=0
SSH_PORT=10050