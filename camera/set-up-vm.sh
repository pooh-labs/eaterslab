#!/bin/sh

# TODO: Force running from this dir
CURRENT_DIR="./"

PROJECT_NAME=ivory-beam

RASPBIAN_ISO_URL="https://downloads.raspberrypi.org/rpd_x86/images/rpd_x86-2020-02-14/2020-02-12-rpd-x86-buster.iso"
RASPBIAN_BUILD="2020-02-12-rpd-x86-buster"

MACHINE_NAME="${PROJECT_NAME}-camera"
MACHINE_DIR="machine-files"
MACHINE_ISO_PATH="${MACHINE_DIR}/${RASPBIAN_BUILD}.iso"
MACHINE_DISK_FILE_PATH="${MACHINE_DIR}/${MACHINE_NAME}-disk.vdi"

mkdir -p "${MACHINE_DIR}"
echo "Using machine dir: ${MACHINE_DIR}"

echo "Downloading image..."
wget -q --show-progress "${RASPBIAN_ISO_URL}" -O "${MACHINE_ISO_PATH}.iso"

echo "Creating machine..."
vboxmanage createvm --name "${MACHINE_NAME}" --ostype "Linux" --register --basefolder "${CURRENT_DIR}" && # Linux OS type is "Other Linux (32-bit)"
vboxmanage modifyvm "${MACHINE_NAME}" --memory 4096 --vram 128 &&
vboxmanage modifyvm "${MACHINE_NAME}" --nic1 nat &&
vboxmanage createhd --filename "${MACHINE_DISK_FILE_PATH}" --size "8192" --format VDI && 
vboxmanage storagectl "${MACHINE_NAME}" --name "IDE Controller" --add ide --controller PIIX4 &&
vboxmanage storageattach "${MACHINE_NAME}" --storagectl "IDE Controller" --port 0 --device 0 --type hdd --medium "${MACHINE_DISK_FILE_PATH}" &&
vboxmanage storageattach "${MACHINE_NAME}" --storagectl "IDE Controller" --port 1 --device 0 --type dvddrive --medium "${MACHINE_ISO_PATH}" && 
vboxmanage modifyvm "${MACHINE_NAME}" --boot1 dvd --boot2 disk --boot3 none --boot4 none
