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
  curl -sSL "$ZIP_URL" -o "$DIR/grw-cli.zip"
  unzip -q "$DIR/grw-cli.zip" -d "$DIR"
  rm "$DIR/grw-cli.zip"
fi

cat > "$WRAPPER" << 'EOF'
#!/usr/bin/env bash
SELF="$(cd "$(dirname "$0")" && pwd)"
exec java -jar "$SELF/.grw/lib/grw-cli-all.jar" "$@"
EOF

chmod +x "$WRAPPER"
echo "✅ Installed ./grw → now run './grw <command>'"
