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
vboxmanage modifyvm "${VM_NAME}" --memory "${RAM_SIZE}" --vram 128 &&
vboxmanage modifyvm "${VM_NAME}" --graphicscontroller "vmsvga" &&
vboxmanage modifyvm "${VM_NAME}" --nic1 nat &&
vboxmanage modifyvm "${VM_NAME}" --usbxhci on && # Enables USB 3.0 for webcams
vboxmanage createhd --filename "${VM_DISK_FILE_PATH}" --size "${DISK_SIZE}" --format VDI && 
vboxmanage storagectl "${VM_NAME}" --name "IDE Controller" --add ide --controller PIIX4 &&
vboxmanage storageattach "${VM_NAME}" --storagectl "IDE Controller" --port 0 --device 0 --type hdd --medium "/media/kantoniak/rpi-files/ivory-beam-camera-disk.vdi" &&
vboxmanage storageattach "${VM_NAME}" --storagectl "IDE Controller" --port 1 --device 0 --type dvddrive --medium "${VM_ISO_PATH}" && 
vboxmanage modifyvm "${VM_NAME}" --boot1 dvd --boot2 disk --boot3 none --boot4 none

if [ "${ENABLE_KVM}" -eq "1" ]; then
    echo "Enabling KVM..."
    vboxmanage modifyvm "${VM_NAME}" --paravirtprovider "kvm"
fi
