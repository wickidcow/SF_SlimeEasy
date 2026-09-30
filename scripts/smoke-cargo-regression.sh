#!/usr/bin/env bash
set -euo pipefail

PAPER_VERSION="${1:?Pass the Paper version to test}"
OUTPUT_NAME="${OUTPUT_NAME:?Set the canonical SlimeEasy JAR name}"
ROOT="$(pwd)"
SMOKE="$(mktemp -d "${RUNNER_TEMP:-/tmp}/slimeeasy-${PAPER_VERSION}.XXXXXX")"
mkdir -p "$SMOKE/plugins"
cp "build/libs/$OUTPUT_NAME" "$SMOKE/plugins/$OUTPUT_NAME"
cp build/regression/SlimeEasyCargoRegression.jar "$SMOKE/plugins/"
CORE_JAR="$ROOT/build/regression/Slimefun-Legacy4.1.61.jar"
if [[ ! -s "$CORE_JAR" ]]; then
  curl -fsSL --retry 3 -o "$CORE_JAR" \
    https://github.com/wickidcow/Slimefun-Legacy/releases/download/v4.1.61/Slimefun-Legacy4.1.61.jar
fi
cp "$CORE_JAR" "$SMOKE/plugins/"
PAPER_URL="$(curl -fsSL --retry 3 -H 'User-Agent: SF_SlimeEasy-CI/1.0.5' \
  "https://fill.papermc.io/v3/projects/paper/versions/$PAPER_VERSION/builds" \
  | jq -r '(first(.[] | select(.channel == "STABLE")) // first(.[])) | .downloads."server:default".url // empty')"
test -n "$PAPER_URL"
curl -fsSL --retry 3 -H 'User-Agent: SF_SlimeEasy-CI/1.0.5' "$PAPER_URL" -o "$SMOKE/paper.jar"
printf 'eula=true\n' > "$SMOKE/eula.txt"
cat > "$SMOKE/server.properties" <<'EOF'
online-mode=false
server-ip=127.0.0.1
server-port=0
view-distance=2
simulation-distance=2
spawn-protection=0
EOF
cd "$SMOKE"
EXIT_CODE=0
timeout --kill-after=15s 180s java -Xms512M -Xmx1024M -Dslimeeasy.cargoRegression=true \
  -jar paper.jar --nogui > server.log 2>&1 || EXIT_CODE=$?
if [[ "$EXIT_CODE" != 0 ]] \
  || ! grep -Fq 'Successfully running SlimeEasy!' server.log \
  || ! grep -Fq 'CARGO_WRAPPER_REGRESSION_PASS' server.log \
  || grep -Eq 'CARGO_WRAPPER_REGRESSION_FAIL|InvalidPluginException|NoClassDefFoundError|NoSuchMethodError|AbstractMethodError|Error occurred while enabling SlimeEasy' server.log; then
  cat server.log
  exit 1
fi
grep -E 'This server is running|Successfully running SlimeEasy|CARGO_WRAPPER_REGRESSION_PASS' server.log
echo "Paper $PAPER_VERSION Cargo regression passed with Slimefun Legacy 4.1.61."
