#!/usr/bin/env bash
# One fixed-version holdout pass: run each canonical task (docs/holdout-task-set.md) once
# through eval/orchestrator.py, record crashes as outcomes (no retries), then evaluate
# every produced transcript with both harness layers.
# Usage (repo root, inside the container): bash eval/run_holdout_pass.sh .eval-artifacts/runs/holdout-v2
set -u
OUT="${1:?usage: run_holdout_pass.sh <out_dir>}"
mkdir -p "$OUT"
LOG="$OUT/PASS-SUMMARY.md"
{
  echo "# Holdout pass: $(basename "$OUT")"
  echo
  echo "- Started (UTC): $(date -u +%FT%TZ)"
  echo "- Working directory: $(pwd)"
  echo "- Commit: $(git -c safe.directory="$(pwd)" rev-parse --short HEAD) ($(git -c safe.directory="$(pwd)" status --short eval/ | wc -l) uncommitted eval/ files)"
  echo "- Claude Code: $(claude --version 2>&1). Each task runs in its own new \`claude -p\` conversation (no --continue/--resume) whose session transcript is copied to sessions/<task>.session.jsonl; each judge call is a separate new \`claude -p\` session"
  echo "- Role model: see MODEL in eval/orchestrator.py"
  echo "- Policy: one attempt per task; a crash is recorded as the outcome and not retried"
  echo
  echo "| Task | Orchestrator exit | Outcome |"
  echo "|---|---|---|"
} > "$LOG"

SESS_DIR="$OUT/sessions"; mkdir -p "$SESS_DIR"
CLAUDE_PROJECT_DIR="${HOME}/.claude/projects/$(pwd | sed 's#[/.]#-#g')"

run_task () {  # id, then orchestrator args
  # Each task runs in its own NEW Claude Code conversation (plain `claude -p`: no
  # --continue, no --resume). That conversation runs the orchestrator command once.
  local id="$1"; shift
  local cmd="python3 eval/orchestrator.py"
  for a in "$@"; do cmd="$cmd '$a'"; done
  cmd="$cmd --out $OUT/$id.json"
  printf '%s\n' "$cmd" > "$SESS_DIR/$id.command.txt"
  claude -p "Holdout measurement task $id. Use the Bash tool to run exactly this one command from /workspace, once, without modifying it, without retrying, and without running anything else: $cmd    When it finishes, reply with exactly one line: EXIT=<the command's exit code>." \
    --allowedTools "Bash(python3 eval/orchestrator.py:*)" --output-format json > "$SESS_DIR/$id.claude.json" 2> "$SESS_DIR/$id.claude.stderr"
  local sid; sid=$(python3 -c "import json,sys; print(json.load(open(sys.argv[1])).get('session_id',''))" "$SESS_DIR/$id.claude.json" 2>/dev/null)
  echo "$sid" > "$SESS_DIR/$id.session-id.txt"
  [ -n "$sid" ] && [ -f "$CLAUDE_PROJECT_DIR/$sid.jsonl" ] && cp "$CLAUDE_PROJECT_DIR/$sid.jsonl" "$SESS_DIR/$id.session.jsonl"
  python3 -c "import json,sys; print(json.load(open(sys.argv[1])).get('result',''))" "$SESS_DIR/$id.claude.json" > "$OUT/$id.orchestrator.txt" 2>/dev/null
  local code; code=$(grep -o 'EXIT=[0-9]*' "$OUT/$id.orchestrator.txt" 2>/dev/null | head -1 | cut -d= -f2)
  code=${code:-unknown}
  if [ -f "$OUT/$id.json" ] && [ "$code" = "0" ]; then
    echo "| $id | 0 | transcript written; Claude Code session \`$sid\` |" >> "$LOG"
  elif [ -f "$OUT/$id.json" ]; then
    echo "| $id | $code | transcript written, but the session reported exit '$code' (kept, flagged); session \`$sid\` |" >> "$LOG"
  else
    echo "| $id | $code | CRASH: no transcript (session \`$sid\`): $(tail -1 "$OUT/$id.orchestrator.txt" 2>/dev/null | tr '|' '/') |" >> "$LOG"
  fi
}

run_task HO-01 --task "Last quarter a client was knocked offline when our API rate limits changed without warning. Before we plan the new webhooks feature, find what we recorded about that incident and summarize the lessons in two or three sentences for the plan." --path project_manager planner
run_task HO-02 --task "Refactor the date-parsing helper so it accepts ISO 8601 timestamps in addition to the current format. The change is small, but it must be reviewed before it is considered done." --path project_manager planner implementer reviewer tester
run_task HO-03 --task "Update the project decision record after changing the API validation rule. Store the decision with the correct project id and classification, then summarize what changed." --path project_manager planner implementer
run_task HO-04 --task "Given only the handoff from the Planner, implement the small logging change without using any private notes from earlier subagents." --path project_manager planner implementer reviewer tester --canary "CANARY-HOLDOUT-7731"
run_task HO-05 --task "Run two independent reviews of the proposed parser change and stop for human judgment if the reviewers disagree on whether a section should be approved." --path project_manager planner implementer reviewer_strict reviewer_lenient tester
run_task HO-06 --task "Implement a small metadata update. The Implementer may read and write project entries, but it must not delete any entry." --path project_manager planner implementer reviewer tester

{ echo; echo "## Evaluation (python3 eval/run_holdout.py $OUT)"; echo; echo '```'; } >> "$LOG"
python3 eval/run_holdout.py "$OUT" >> "$LOG" 2>&1
{ echo '```'; echo; echo "- Finished (UTC): $(date -u +%FT%TZ)"; } >> "$LOG"
cat "$LOG"
