#!/usr/bin/env bash
# Runs a Paper server with the built plugin in order to generate the MaterialSprites mappings.
#
# The plugin is expected to have been built already (target/MaterialSpritesGenerator.jar).
# It is configured to shut the server down as soon as the mappings are written.
#
# Usage: scripts/run-paper.sh <minecraft version>
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
VERSION="${1:?usage: run-paper.sh <minecraft version>}"

PLUGIN_JAR="$ROOT/target/MaterialSpritesGenerator.jar"
SERVER_DIR="$ROOT/build/paper/$VERSION"
PLUGIN_DATA="$SERVER_DIR/plugins/MaterialSpritesGenerator"
OUTPUT="$PLUGIN_DATA/MaterialSprites.java"

if [[ ! -f "$PLUGIN_JAR" ]]; then
	echo "Plugin jar not found at $PLUGIN_JAR; run 'mvn package' first." >&2
	exit 1
fi

mkdir -p "$SERVER_DIR/plugins"
if [[ ! -f "$SERVER_DIR/paper.jar" ]]; then
	echo "Resolving the latest Paper build for Minecraft $VERSION..."
	URL="$(curl -fsSL "https://fill.papermc.io/v3/projects/paper/versions/$VERSION/builds" \
		| jq -r '.[0].downloads["server:default"].url')"
	if [[ -z "$URL" || "$URL" == "null" ]]; then
		echo "Could not find a Paper build for Minecraft $VERSION." >&2
		exit 1
	fi
	echo "Downloading $URL"
	curl -fSL -o "$SERVER_DIR/paper.jar" "$URL"
fi

# Accept the Minecraft EULA and install a fresh copy of the plugin.
echo "eula=true" > "$SERVER_DIR/eula.txt"
rm -rf "$PLUGIN_DATA"
cp "$PLUGIN_JAR" "$SERVER_DIR/plugins/MaterialSpritesGenerator.jar"

echo "Starting Paper $VERSION (this can take a moment on the first run)..."
cd "$SERVER_DIR"
set +e
timeout 300 java -Xmx1G -jar paper.jar --nogui > "$SERVER_DIR/console.log" 2>&1
STATUS=$?
set -e

grep -E "\[MaterialSpritesGenerator\]" "$SERVER_DIR/console.log" || true

if [[ ! -s "$OUTPUT" ]]; then
	echo "Mapping generation failed (server exit code $STATUS). Last log lines:" >&2
	tail -n 40 "$SERVER_DIR/console.log" >&2
	exit 1
fi

echo
echo "Generated mappings: $OUTPUT"
