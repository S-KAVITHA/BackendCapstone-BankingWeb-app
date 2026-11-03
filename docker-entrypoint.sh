#!/bin/bash
set -eu

AUTH_DIR=/claude-auth
mkdir -p "$AUTH_DIR" /root/.claude

# A persisted credential means this is NOT the first run: restore it and mark
# onboarding complete so Claude skips the first-run flow (theme, login, trust).
# On the first run there is no credential, so onboarding runs once and the
# background saver below persists the resulting credential.
if [ -f "$AUTH_DIR/.credentials.json" ]; then
  cp "$AUTH_DIR/.credentials.json" /root/.claude/.credentials.json
  chmod 600 /root/.claude/.credentials.json
  echo '{ "hasCompletedOnboarding": true, "projects": { "/workspace": { "hasTrustDialogAccepted": true } } }' > /root/.claude.json
fi

# Register this image's MCP servers (merged into /root/.claude.json).
# Slack is optional: the documented `docker run` commands pass no Slack
# variables, so skip it (instead of failing under `set -u`) when they are unset.
if [ -n "${SLACK_BOT_TOKEN:-}" ] && [ -n "${SLACK_TEAM_ID:-}" ]; then
  claude mcp add slack \
    node /usr/local/lib/node_modules/@modelcontextprotocol/server-slack/dist/index.js \
    -e SLACK_BOT_TOKEN="$SLACK_BOT_TOKEN" \
    -e SLACK_TEAM_ID="$SLACK_TEAM_ID"
else
  echo "Slack MCP not configured (SLACK_BOT_TOKEN/SLACK_TEAM_ID unset); skipping."
fi

claude mcp add gmail \
  node /usr/local/lib/node_modules/@gongrzhe/server-gmail-autoauth-mcp/dist/index.js

# Save the credential whenever it changes, so login survives any exit. Save
# when the persisted copy is missing (first login) or older than the live one;
# temp-file + chmod + atomic mv keeps the copy safe on the shared volume.
( set +e
  while true; do
    if [ -f /root/.claude/.credentials.json ] && \
       { [ ! -f "$AUTH_DIR/.credentials.json" ] || \
         [ /root/.claude/.credentials.json -nt "$AUTH_DIR/.credentials.json" ]; }; then
      tmp=$(mktemp "$AUTH_DIR/.credentials.json.tmp.XXXXXX")
      cp /root/.claude/.credentials.json "$tmp"
      chmod 600 "$tmp"
      mv -f "$tmp" "$AUTH_DIR/.credentials.json"
    fi
    sleep 5
  done ) &

# Bootstrap the MySQL-compatible datasource application.properties expects
# (jdbc:mysql://localhost:3306/db_world, credentials from that file) so a fresh container boots
# with a working DB instead of needing a manual install during the session.
# root keeps unix_socket login (used by MariaDB's own debian-start check) and
# gains the password the app's JDBC connection uses. Setting the password alone
# would remove socket login and make that check fail with ERROR 1045.
# Idempotent: if socket login is unavailable, the password fallback is used.
service mariadb start
DB_SETUP="CREATE DATABASE IF NOT EXISTS db_world;
  ALTER USER 'root'@'localhost' IDENTIFIED VIA unix_socket OR mysql_native_password USING PASSWORD('root');
  FLUSH PRIVILEGES;"
mysql -uroot -e "$DB_SETUP" 2>/dev/null || mysql -uroot -proot -e "$DB_SETUP"
echo "Database ready: db_world"

# exec so the command runs as PID 1 (correct signal handling / reaping).
exec "$@"
