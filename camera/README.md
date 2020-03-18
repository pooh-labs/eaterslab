# Cameras

A quick overview of directory structure:
* `pi-files`: project files to put on Raspberry Pi device
* `vm-files`: ignored directory, default for virtual machines files if you didn't provide your own during setup

## Using VM

You will need ~3 GB for Raspbian Desktop image and up to 14 GB for the disk file.

### Setup

1. Run `./vm-setup.sh` (`--help` shows configuration options, you want at least `--enable-kvm`).
2. Start VM from Virtual Box and choose `Install` option.
    1. Keymap to use: `American English`
    2. Partition disks: `Guided partitioning`, `Guided - use entire disk`
    3. Partition disks: choose the only partition, then `All files in one partition`
    4. Partition disks: finish and write to disk
    5. GRUB: install to master boot record (`Yes`), choose `/dev/sda`
3. Raspbian should reboot into full-fledged system. Run the setup wizard.

### Starting VM

Run `./vm-start.sh`. It will start the machine and bind `/dev/video0` webcam for you. Call `v4l2-ctl --list-devices` to see which device it is.
