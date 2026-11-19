---
classification: public
project: proj-lessons
doc_type: lesson
---

# Lesson: Non-interactive claude -p runs need explicit MCP tool grants, and prompts should be piped in

## What happened
In Run 031 the planner and reviewer were invoked with `claude -p --agent <name>`. Their first runs stopped with "Claude requested permissions to use mcp__coursetools__file_read, but you haven't granted it yet", because print mode has no one to approve a permission prompt. Adding `--allowedTools mcp__coursetools__file_read` then failed with "Input must be provided either through stdin or as a prompt argument", because the option accepts several values and consumed the prompt text as a second tool name.

## What we learned
Print mode is the right way to capture repeatable evidence, but grants must be given up front, and variadic options can swallow positional arguments.

## How to apply it
Pipe the prompt and grant only the tools the agent's frontmatter already lists, for example: `echo "<prompt>" | claude -p --agent reviewer --allowedTools mcp__coursetools__file_read`. Capture the output with `tee` into `docs/transcripts/`. Both agents then completed their reads and reported `BankingProject_Backend` and `1.8`.
