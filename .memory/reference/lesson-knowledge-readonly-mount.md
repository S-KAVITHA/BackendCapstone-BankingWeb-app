---
classification: internal
project: proj-lessons
doc_type: lesson
---

# Lesson: chmod cannot protect Knowledge files in the agent container; use a read-only mount

## What happened
The plan was to keep `.memory/knowledge/` read-only for agents through a CLAUDE.md instruction backed by file permissions. In the course container the agent runs as root, and the repository is bind-mounted from a Windows drive (drvfs), where every file is `rwxrwxrwx`. A probe started without protection created `probe.txt` and appended text to `coding-standards.md`. In Run 029, with the directory mounted `:ro`, both writes failed with "Read-only file system".

## What we learned
On drvfs, `chmod` is ignored, and root bypasses file modes anyway, so permission bits give no protection here. Claude Code deny rules must use `Edit(path)`; `Write(path)` rules are not matched and only produce a warning.

## How to apply it
Always start agent containers with `-v "${PWD}/.memory/knowledge:/workspace/.memory/knowledge:ro"` (it is in every README `docker run` command). Keep `Edit(.memory/knowledge/**)` in `.claude/settings.json`. Make Knowledge edits from the host. If an agent hits a denial, it must stop, report it, and ask a human, as the CLAUDE.md "Permission denials" section says.
