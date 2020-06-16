#!/bin/bash

thumbs_dir="./thumbs"
files=$(find "." -path "${thumbs_dir}" -prune -o -name '*.png' -printf "%f\n")
thumb_width=290
thumb_height=230 # Applies only to non-Android files

mkdir -p $thumbs_dir

declare -a website_files=()
declare -a android_files=()

# Generate files
for filename in $files; do
    echo "Processing $filename..."
    if [[ $filename == android* ]];
    then
        android_files+=("$filename")
        # No cropping, resize only
        mogrify -path "$thumbs_dir" -filter Triangle -define filter:support=2 -resize "${thumb_width}x" -unsharp 0.25x0.08+8.3+0.045 -dither None -posterize 136 -quality 82 -define jpeg:fancy-upsampling=off -define png:compression-filter=5 -define png:compression-level=9 -define png:compression-strategy=1 -define png:exclude-chunk=all -interlace none -colorspace sRGB -strip "$filename"
    else
        website_files+=("$filename")
        # Resize and crop
        mogrify -path "$thumbs_dir" -filter Triangle -define filter:support=2 -resize "${thumb_width}x" -unsharp 0.25x0.08+8.3+0.045 -dither None -posterize 136 -quality 82 -define jpeg:fancy-upsampling=off -define png:compression-filter=5 -define png:compression-level=9 -define png:compression-strategy=1 -define png:exclude-chunk=all -interlace none -colorspace sRGB -strip -crop "${thumb_width}x${thumb_height}+0+0" "$filename"
    fi
done

# Generate Markdown
cat <<EOF > screenshots.md
## Screenshots

### Website
<p align="center">
EOF

for filename in ${website_files[@]}; do
    cat <<EOF >> screenshots.md
    <a href="readme-images/${filename}"><img src="readme-images/thumbs/${filename}" width="${thumb_width}" /></a>
EOF
done

cat <<EOF >> screenshots.md
</p>

### Android
<p align="center">
EOF

for filename in ${android_files[@]}; do
    cat <<EOF >> screenshots.md
    <a href="readme-images/${filename}"><img src="readme-images/thumbs/${filename}" width="${thumb_width}" /></a>
EOF
done

cat <<EOF >> screenshots.md
</p>
EOF
