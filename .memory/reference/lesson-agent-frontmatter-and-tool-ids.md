---
classification: internal
project: proj-lessons
doc_type: lesson
---

# Lesson: Agent definitions must parse and must use the server's exact tool names

## What happened
`planner.md` and `reviewer.md` had their `description: >` text unindented, so the YAML frontmatter did not parse and Claude Code did not list either agent. Separately, reviewer, tester and project-manager granted tools such as `mcp__coursetools__file-read` and `mcp__coursetools__task-tracker`, while the server defines `file_read` and `task_tracker`. A grant that names a tool that does not exist silently grants nothing.

## What we learned
An agent file can look correct and still be inert. The only reliable checks are that Claude Code lists the agent and that the agent can call its granted tools against the live server.

## How to apply it
Indent folded `description: >` text by two spaces. Copy tool names from the server code (`@mcp.tool()` function names) as `mcp__<server>__<function>`, with underscores. After any change, run `claude mcp list` to confirm the server is connected, then invoke the agent with `claude -p --agent <name>` and check that it reports the tools it actually has. Run 031 used the tester's own tool list to confirm the grants matched.
