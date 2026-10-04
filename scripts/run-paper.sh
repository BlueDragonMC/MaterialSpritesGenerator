#!/usr/bin/env bash
# Runs a Paper server with the built plugin in order to generate the material sprite data.
#
# The plugin is expected to have been built already (plugin/target/MaterialSpritesGenerator.jar).
# It is configured to shut the server down as soon as the data has been written, and the
# generated file is copied into the data module's resources.
#
# Usage: scripts/run-paper.sh <minecraft version>
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
VERSION="${1:-${MINECRAFT_VERSION:-}}"
if [[ -z "$VERSION" ]]; then
	echo "usage: run-paper.sh <minecraft version> (or set MINECRAFT_VERSION)" >&2
	exit 2
fi

PLUGIN_JAR="$ROOT/plugin/target/MaterialSpritesGenerator.jar"
SERVER_DIR="$ROOT/build/paper/$VERSION"
PLUGIN_DATA="$SERVER_DIR/plugins/MaterialSpritesGenerator"
GENERATED="$PLUGIN_DATA/material-sprites.json"
DESTINATION="$ROOT/data/src/main/resources/material-sprites.json"

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

if [[ ! -s "$GENERATED" ]]; then
	echo "Mapping generation failed (server exit code $STATUS). Last log lines:" >&2
	tail -n 40 "$SERVER_DIR/console.log" >&2
	exit 1
fi

mkdir -p "$(dirname "$DESTINATION")"
cp "$GENERATED" "$DESTINATION"
echo
echo "Generated data:  $GENERATED"
echo "Copied into:     $DESTINATION"
