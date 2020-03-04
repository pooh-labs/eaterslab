#!/bin/sh

# TODO: Force running from this dir
CURRENT_DIR="./"

PROJECT_NAME=ivory-beam

RASPBIAN_ISO_URL="https://downloads.raspberrypi.org/rpd_x86/images/rpd_x86-2020-02-14/2020-02-12-rpd-x86-buster.iso"
RASPBIAN_BUILD="2020-02-12-rpd-x86-buster"

VM_NAME="${PROJECT_NAME}-camera"
VM_DIR="vm-files"
VM_ISO_PATH="${VM_DIR}/${RASPBIAN_BUILD}.iso"
VM_DISK_FILE_PATH="${VM_DIR}/${VM_NAME}-disk.vdi"

mkdir -p "${VM_DIR}"
echo "Using vm dir: ${VM_DIR}"

echo "Downloading image..."
#wget -q --show-progress "${RASPBIAN_ISO_URL}" -O "${VM_ISO_PATH}.iso"

echo "Creating vm..."
vboxmanage createvm --name "${VM_NAME}" --ostype "Linux" --register --basefolder "${CURRENT_DIR}" && # Linux OS type is "Other Linux (32-bit)"
vboxmanage modifyvm "${VM_NAME}" --memory 6144 --vram 128 &&
vboxmanage modifyvm "${VM_NAME}" --nic1 nat &&
vboxmanage createhd --filename "${VM_DISK_FILE_PATH}" --size "8192" --format VDI && 
vboxmanage storagectl "${VM_NAME}" --name "IDE Controller" --add ide --controller PIIX4 &&
vboxmanage storageattach "${VM_NAME}" --storagectl "IDE Controller" --port 0 --device 0 --type hdd --medium "${VM_DISK_FILE_PATH}" &&
vboxmanage storageattach "${VM_NAME}" --storagectl "IDE Controller" --port 1 --device 0 --type dvddrive --medium "${VM_ISO_PATH}" && 
vboxmanage modifyvm "${VM_NAME}" --boot1 dvd --boot2 disk --boot3 none --boot4 none
