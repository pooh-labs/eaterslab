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

# Process flags
for arg in "$@"
do
    case $arg in
        --help)
        print_usage
        exit 0
        ;;
        --disk=*)
        DISK_SIZE="${arg#*=}"
        echo "Selected disk size: ${DISK_SIZE} MB"
        shift
        ;;
        --ram=*)
        RAM_SIZE="${arg#*=}"
        echo "Selected RAM size: ${RAM_SIZE} MB"
        shift
        ;;
        --sshp=*)
        SSH_PORT="${arg#*=}"
        echo "Selected SSH port: ${SSH_PORT}"
        shift
        ;;
        --vm-dir=*)
        VM_DIR="${arg#*=}"
        shift
        ;;
        --enable-kvm)
        ENABLE_KVM=1
        shift
        ;;
    esac
done
