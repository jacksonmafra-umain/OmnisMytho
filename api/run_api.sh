#!/usr/bin/env bash
#
# Starts the Omnis Mytho API + ngrok tunnel.
# Writes the public URL to app/gradle.properties so the KMP app always
# picks up the latest endpoint — no hardcoded URLs.
#
# Usage:
#   ./run_api.sh              # default port 8000
#   ./run_api.sh 9000         # custom port
#
# Requirements:
#   - ngrok installed and authenticated (ngrok config add-authtoken ...)
#   - Python venv at api/.venv with deps installed
#
set -euo pipefail

PORT="${1:-8000}"
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
GRADLE_PROPS="$PROJECT_ROOT/app/gradle.properties"

# Colors
GREEN='\033[0;32m'
GOLD='\033[0;33m'
NC='\033[0m'

cleanup() {
    echo ""
    echo -e "${GOLD}Shutting down...${NC}"
    [ -n "${UVICORN_PID:-}" ] && kill "$UVICORN_PID" 2>/dev/null && echo "  Stopped uvicorn (PID $UVICORN_PID)"
    [ -n "${NGROK_PID:-}" ] && kill "$NGROK_PID" 2>/dev/null && echo "  Stopped ngrok (PID $NGROK_PID)"

    # Reset gradle property to localhost fallback
    if [ -f "$GRADLE_PROPS" ]; then
        if grep -q "^api.baseUrl=" "$GRADLE_PROPS"; then
            sed -i '' "s|^api.baseUrl=.*|api.baseUrl=http://10.0.2.2:$PORT/api/v1|" "$GRADLE_PROPS"
        fi
        echo "  Reset api.baseUrl to localhost fallback"
    fi
    exit 0
}
trap cleanup SIGINT SIGTERM

# ── 1. Start uvicorn ──────────────────────────────────────────────────────────
echo -e "${GOLD}Starting API on port $PORT...${NC}"
cd "$SCRIPT_DIR"

if [ -d ".venv" ]; then
    source .venv/bin/activate
fi

uvicorn main:app --host 0.0.0.0 --port "$PORT" --reload &
UVICORN_PID=$!
sleep 2

if ! kill -0 "$UVICORN_PID" 2>/dev/null; then
    echo "Failed to start uvicorn"
    exit 1
fi

echo -e "${GREEN}  API running on http://localhost:$PORT${NC}"
echo "  Swagger: http://localhost:$PORT/docs"

# ── 2. Start ngrok ────────────────────────────────────────────────────────────
if ! command -v ngrok &>/dev/null; then
    echo ""
    echo "ngrok not found. Install: https://ngrok.com/download"
    echo "Using localhost fallback instead."
    NGROK_URL="http://10.0.2.2:$PORT"
else
    echo -e "\n${GOLD}Starting ngrok tunnel...${NC}"
    ngrok http "$PORT" --log=stdout --log-level=warn > /dev/null &
    NGROK_PID=$!
    sleep 3

    # Get the public URL from ngrok API
    NGROK_URL=""
    for i in 1 2 3 4 5; do
        NGROK_URL=$(curl -s http://127.0.0.1:4040/api/tunnels 2>/dev/null | \
            python3 -c "import sys,json; tunnels=json.load(sys.stdin).get('tunnels',[]); print(tunnels[0]['public_url'] if tunnels else '')" 2>/dev/null || true)
        if [ -n "$NGROK_URL" ]; then
            break
        fi
        sleep 1
    done

    if [ -z "$NGROK_URL" ]; then
        echo "  Failed to get ngrok URL, using localhost fallback"
        NGROK_URL="http://10.0.2.2:$PORT"
    else
        echo -e "${GREEN}  ngrok tunnel: $NGROK_URL${NC}"
    fi
fi

# ── 3. Write to gradle.properties ─────────────────────────────────────────────
API_BASE_URL="$NGROK_URL/api/v1"

if [ -f "$GRADLE_PROPS" ]; then
    if grep -q "^api.baseUrl=" "$GRADLE_PROPS"; then
        sed -i '' "s|^api.baseUrl=.*|api.baseUrl=$API_BASE_URL|" "$GRADLE_PROPS"
    else
        echo "" >> "$GRADLE_PROPS"
        echo "#API" >> "$GRADLE_PROPS"
        echo "api.baseUrl=$API_BASE_URL" >> "$GRADLE_PROPS"
    fi
else
    echo "api.baseUrl=$API_BASE_URL" > "$GRADLE_PROPS"
fi

echo ""
echo -e "${GREEN}════════════════════════════════════════════════════════════${NC}"
echo -e "${GREEN}  Omnis Mytho API ready!${NC}"
echo -e "${GREEN}════════════════════════════════════════════════════════════${NC}"
echo ""
echo "  Local:    http://localhost:$PORT"
echo "  Public:   $NGROK_URL"
echo "  API base: $API_BASE_URL"
echo "  Swagger:  $NGROK_URL/docs"
echo ""
echo "  gradle.properties updated:"
echo "    api.baseUrl=$API_BASE_URL"
echo ""
echo -e "${GOLD}  Rebuild the app to pick up the new URL.${NC}"
echo "  Press Ctrl+C to stop."
echo ""

# Wait for uvicorn
wait "$UVICORN_PID"
