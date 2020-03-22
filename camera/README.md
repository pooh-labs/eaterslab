# Cameras

A quick overview of directory structure:
* `rpi-files`: project files used on Raspberry Pi device
* `vm-files`: ignored directory, default for virtual machines files if you didn't provide your own during setup

## Device setup

This section assumes you have a fresh install of Raspbian on either VM or physical device. You will need keyboard device to run setup commands. Enter terminal and run:

```
# Clone repository to home directory
cd /home/pi
git clone https://github.com/pooh-labs/eaterslab.git

# Run setup script
cd eaterslab/camera/rpi-files
sudo ./setup.sh
```

## Using VM

You will need ~3 GB for Raspbian Desktop image and up to 14 GB for the disk file.

### Setup

1. Run `./vm-setup.sh` (`--help` shows configuration options, you want at least `--enable-kvm`).
2. Start VM from VirtualBox and choose `Install` option.
    1. Keymap to use: `American English`
    2. Partition disks: `Guided partitioning`, `Guided - use entire disk`
    3. Partition disks: choose the only partition, then `All files in one partition`
    4. Partition disks: finish and write to disk
    5. GRUB: install to master boot record (`Yes`), choose `/dev/sda`
3. Raspbian should reboot into full-fledged system. Run the setup wizard.
4. Start VM from VirtualBox and run device setup (as described in this doc).

### Starting VM

Run `./vm-start.sh`. It will:

1. Start the machine.
2. Bind `/dev/video0` webcam for you. Call `v4l2-ctl --list-devices` to see which device it is.
3. Enter SSH connection. SSH does not work until you perform device setup.
