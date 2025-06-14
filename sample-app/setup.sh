#!/usr/bin/env bash
set -euo pipefail

REPO="void-memories/grw"
API_URL="https://api.github.com/repos/$REPO/releases/latest"
WRAPPER="./grw"
DIR=".grw"

echo "🔍 Fetching latest release info..."
TAG=$(curl -sSL "$API_URL" \
     | grep -m1 '"tag_name"' \
     | sed -E 's/.*"([^"]+)".*/\1/')

ZIP_URL="https://github.com/$REPO/releases/download/$TAG/grw-cli.zip"

if [ ! -d "$DIR" ]; then
  echo "⬇️  Downloading grw-cli $TAG..."
  mkdir "$DIR"
  
  # Start download in background
  curl -L "$ZIP_URL" -o "$DIR/grw-cli.zip" --silent &
  CURL_PID=$!
  
  # Monitor progress with spinner and size
  spinner_chars="⠋⠙⠹⠸⠼⠴⠦⠧⠇⠏"
  spinner_index=0
  
  while kill -0 $CURL_PID 2>/dev/null; do
    if [[ -f "$DIR/grw-cli.zip" ]]; then
      size=$(stat -f%z "$DIR/grw-cli.zip" 2>/dev/null || stat -c%s "$DIR/grw-cli.zip" 2>/dev/null || echo "0")
      size_mb=$(echo "scale=1; $size / 1024 / 1024" | bc -l 2>/dev/null || echo "0.0")
      
      spinner_char=${spinner_chars:$spinner_index:1}
      printf "\r🔄 Downloading... %s %.1f MB" "$spinner_char" "$size_mb"
      
      spinner_index=$(( (spinner_index + 1) % ${#spinner_chars} ))
    else
      printf "\r🔄 Downloading... %s" "${spinner_chars:$spinner_index:1}"
      spinner_index=$(( (spinner_index + 1) % ${#spinner_chars} ))
    fi
    
    sleep 0.2
  done
  
  # Wait for curl to finish and show final size
  wait $CURL_PID
  
  if [[ -f "$DIR/grw-cli.zip" ]]; then
    final_size=$(stat -f%z "$DIR/grw-cli.zip" 2>/dev/null || stat -c%s "$DIR/grw-cli.zip" 2>/dev/null || echo "0")
    final_size_mb=$(echo "scale=1; $final_size / 1024 / 1024" | bc -l 2>/dev/null || echo "0.0")
    printf "\r✅ Downloaded %.1f MB                    \n" "$final_size_mb"
  else
    echo "\r❌ Download failed!                      "
    exit 1
  fi
  
  echo "📦 Extracting archive..."
  unzip -q "$DIR/grw-cli.zip" -d "$DIR"
  rm "$DIR/grw-cli.zip"
  echo "✅ Setup completed!"
fi

cat > "$WRAPPER" << 'EOF'
#!/usr/bin/env bash
SELF="$(cd "$(dirname "$0")" && pwd)"
exec java -jar "$SELF/.grw/cli-all.jar" "$@"
EOF

chmod +x "$WRAPPER"
echo "✅ Installed ./grw → now run './grw --help'"
echo "now run './grw --help' to get started"
