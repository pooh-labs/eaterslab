#!/bin/sh

KEY_FILENAME="github-camera-dev-key"

echo "Setting up git..."
read -p "Git user.name: " git_username
read -p "GitHub email address: " gh_email
git config --global user.name "${git_username}"
git config --global user.email "${git_email}"

# Create key
cd ~/.ssh &&
echo "Generating new keys..." &&
ssh-keygen -t rsa -b 4096 -f "${KEY_FILENAME}" -C "Camera development key (${git_email})" &&
ssh-add "${KEY_FILENAME}" &&
cat <<EOF >> config
host github.com
 HostName github.com
 IdentityFile ~/.ssh/${KEY_FILENAME}
 User git
EOF

echo "Remember to add ${KEY_FILENAME}.pub to GitHub keys! See https://help.github.com/en/github/authenticating-to-github/adding-a-new-ssh-key-to-your-github-account"
