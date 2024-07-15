#!/usr/bin/env sh

# Brought to me by CHATGPT
# Define the directory containing the images
INPUT_DIR="src-img"
OUTPUT_DIR="resources/build/img"

# Find all image files (you can adjust the file extensions as needed)
#
find "$INPUT_DIR" -type f \( -iname '*.jpg' -o -iname '*.jpeg' -o -iname '*.png' -o -iname '*.bmp' -o -iname '*.tiff' \) | while read file; do
  # Get the relative path of the file from the input directory
  relative_path="${file#$INPUT_DIR/}"

  # Get the directory of the file relative to the input directory
  relative_dir=$(dirname "$relative_path")

  # Create the corresponding directory in the output directory
  mkdir -p "$OUTPUT_DIR/$relative_dir"

  # Get the base filename without extension
  filename=$(basename "$file")
  base="${filename%.*}"

  echo "Converting $relative_dir/$filename"
  # Convert the image to WebP format and save it in the output directory
  cwebp -q 85 "$file" -o "$OUTPUT_DIR/$relative_dir/$base.webp"
done

$echo du
