#!/bin/sh

# Load defines
SCRIPT_PATH="$(dirname "$(realpath "$0")")"
. "$SCRIPT_PATH/vm-defines.sh"

print_usage() {
    echo >&2 "Usage: $0 [options]"
    echo >&2 "  --help\tShow help and quit"
    echo >&2 "  --disk=<y>\tSet <y> MB of disk space (${DISK_SIZE} MB by default)"
    echo >&2 "  --ram=<x>\tSet <x> MB of RAM (${RAM_SIZE} MB by default)"
    echo >&2 "  --vm-dir=<d>\tSet VM files directory to <dir> (${VM_DIR} by default)"
    echo >&2 "  --enable-kvm\tEnable fast KVM virtualization"
    echo >&2 "  --sshp=<p>\tSet forwarded SSH port to <p> (${SSH_PORT} by default)"
}

print_usage_and_die() {
    print_usage
    exit 1
}

# Force running from the same directory
if [ "$PWD" != "${SCRIPT_PATH}" ]; then
    echo >&2 "Error: Script called from a different directory" 
    print_usage_and_die
fi

mkdir -p "${VM_DIR}" &&
echo "Using vm files dir: ${VM_DIR}"

# Install extension pack for webcam support
VBOX_VERSION=$(vbox-img --version | cut -d "r" -f 1)
EXTPACK_PATH="Oracle_VM_VirtualBox_Extension_Pack-${VBOX_VERSION}.vbox-extpack"

# Install extension pack 
if [ ! -f "${VM_ISO_PATH}" ]; then
    echo "Downloading extension pack..."
    wget "https://download.virtualbox.org/virtualbox/${VBOX_VERSION}/${EXTPACK_PATH}" -O "${EXTPACK_PATH}"

    echo "Installing extension pack..."
    sudo VBoxManage extpack install --replace "${EXTPACK_PATH}"
fi

# Setup and download VM
RASPBIAN_ISO_URL="https://downloads.raspberrypi.org/rpd_x86/images/rpd_x86-2020-02-14/2020-02-12-rpd-x86-buster.iso"
RASPBIAN_BUILD="2020-02-12-rpd-x86-buster"

mkdir -p "${VM_DIR}"
echo "Using vm dir: ${VM_DIR}"
VM_ISO_PATH="${VM_DIR}/${RASPBIAN_BUILD}.iso"
VM_DISK_FILE_PATH="${VM_DIR}/${VM_NAME}-disk.vdi"

# Download image if not on disk
if [ ! -f "${VM_ISO_PATH}" ]; then
    echo "Downloading image..."
    wget -q --show-progress "${RASPBIAN_ISO_URL}" -O "${VM_ISO_PATH}"
fi

echo "Creating vm..."
vboxmanage createvm --name "${VM_NAME}" --ostype "Linux" --register --basefolder "${CURRENT_DIR}" && # Linux OS type is "Other Linux (32-bit)"
vboxmanage modifyvm "${VM_NAME}" --memory "${RAM_SIZE}" --vram 128 && # Set RAM and VRAM in MB
vboxmanage modifyvm "${VM_NAME}" --graphicscontroller "vmsvga" &&
vboxmanage modifyvm "${VM_NAME}" --nic1 nat --natpf1 "SSH forwarding,tcp,127.0.0.1,${SSH_PORT},10.0.2.15,22" && # NAT and SSH forwarding
vboxmanage modifyvm "${VM_NAME}" --usbxhci on && # Enable USB 3.0 for webcams to attach
vboxmanage createhd --filename "${VM_DISK_FILE_PATH}" --size "${DISK_SIZE}" --format VDI && # Create new empty VDI disk
vboxmanage storagectl "${VM_NAME}" --name "IDE Controller" --add ide --controller PIIX4 && # Enable IDE controller for disk and drive
vboxmanage storageattach "${VM_NAME}" --storagectl "IDE Controller" --port 0 --device 0 --type hdd --medium "${VM_DISK_FILE_PATH}" && # Attach empty virtual disk
vboxmanage storageattach "${VM_NAME}" --storagectl "IDE Controller" --port 1 --device 0 --type dvddrive --medium "${VM_ISO_PATH}" && # Attach ISO with Raspbian image
vboxmanage modifyvm "${VM_NAME}" --boot1 dvd --boot2 disk --boot3 none --boot4 none # Set boot order to DVD, then disk

if [ "${ENABLE_KVM}" -eq "1" ]; then
    echo "Enabling KVM..."
    vboxmanage modifyvm "${VM_NAME}" --paravirtprovider "kvm" # Enable KVM acceleration
fi
