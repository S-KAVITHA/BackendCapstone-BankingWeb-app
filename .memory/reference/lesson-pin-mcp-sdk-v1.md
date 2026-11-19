---
classification: internal
project: proj-lessons
doc_type: lesson
---

# Lesson: Pin the Python MCP SDK below 2.0 and test imports outside the repo

## What happened
In Run 031 the `coursetools` server failed to connect with CONNECTION_CLOSED. Two causes stacked up. First, `python3 -c "import mcp"` appeared to succeed from `/workspace`, but only because the repository's own `mcp/` folder shadowed the missing package; the server script itself failed with "No module named 'mcp'". Second, a plain `pip3 install mcp` installed mcp 2.3.0, where `FastMCP` was renamed, so `from mcp.server.fastmcp import FastMCP` raised an error.

## What we learned
A folder named like a package can hide a missing dependency when you test from the repo root. An unpinned dependency can break working code on the next install.

## How to apply it
Install with `pip3 install "mcp<2"` (1.30.0 worked) and check the import from another directory, for example `cd /tmp && python3 -c "from mcp.server.fastmcp import FastMCP"`. Start the server once with `timeout 3 python3 mcp/coursetools_server.py < /dev/null` and confirm no traceback before registering it with `claude mcp add`. The container uses `--rm`, so repeat the install in every new container until it is added to the Dockerfile.
