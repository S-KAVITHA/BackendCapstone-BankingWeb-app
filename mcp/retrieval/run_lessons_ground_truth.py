"""Run the proj-lessons ground-truth queries (docs/lessons-ground-truth.md) against the
live retrieval MCP server and print one line per query with source/chunk citations."""
from __future__ import annotations

import asyncio
from typing import Any

from fastmcp import Client

THRESHOLD = 0.65
SERVER_URL = "http://localhost:8002/mcp"
PROJECT = "proj-lessons"
CONFIDENTIAL_DOC = "lesson-datasource-credentials.md"
CREDENTIALS_QUERY = "Where are the database username and password configured?"

# kind:
# - top3: expected doc in the top 3 (vector score >= THRESHOLD, or keyword fallback)
# - excluded: forbidden doc must be absent at the internal ceiling
# - rejected: the server must refuse a ceiling above its maximum
CASES: list[dict[str, Any]] = [
    {"id": "Q1", "kind": "top3", "query": "What should I check before moving money between two accounts?",
     "ceiling": "internal", "expected": "lesson-transfer-validate-target-first.md"},
    {"id": "Q2", "kind": "top3", "query": "How should new backend code write log messages?",
     "ceiling": "internal", "expected": "lesson-slf4j-not-println.md"},
    {"id": "Q3", "kind": "top3", "query": "Why can't chmod make the knowledge folder read-only for the agent?",
     "ceiling": "internal", "expected": "lesson-knowledge-readonly-mount.md"},
    {"id": "Q4", "kind": "top3",
     "query": "The coursetools MCP server fails with connection closed. Which Python package version do I need?",
     "ceiling": "internal", "expected": "lesson-pin-mcp-sdk-v1.md"},
    {"id": "Q5", "kind": "top3", "query": "How do I make sure an agent's granted tool names actually exist on the server?",
     "ceiling": "internal", "expected": "lesson-agent-frontmatter-and-tool-ids.md"},
    {"id": "Q6", "kind": "top3", "query": "What should the agent do when a memory entry has no review date?",
     "ceiling": "internal", "expected": "lesson-stale-memory-review-dates.md"},
    {"id": "Q7", "kind": "excluded", "query": CREDENTIALS_QUERY,
     "ceiling": "internal", "forbidden": CONFIDENTIAL_DOC},
    {"id": "Q8", "kind": "rejected", "query": CREDENTIALS_QUERY,
     "ceiling": "confidential", "forbidden": CONFIDENTIAL_DOC},
]


def extract_hits(result: Any) -> list[dict[str, Any]]:
    for attr in ("data", "structured_content", "structuredContent"):
        value = getattr(result, attr, None)
        if value is not None:
            if isinstance(value, dict) and "result" in value:
                return value["result"]
            return value
    if isinstance(result, list):
        return result
    raise RuntimeError(f"Could not extract structured hits from result: {result!r}")


def cite(hits: list[dict[str, Any]]) -> str:
    if not hits:
        return "(no results)"
    return ", ".join(
        f"{h['source_document']}#chunk{h['chunk_index']}"
        f"({h['classification']},{h['retrieval_method']},{h.get('similarity_score')})"
        for h in hits
    )


def judge(case: dict[str, Any], hits: list[dict[str, Any]]) -> bool:
    docs = [h["source_document"] for h in hits]
    if case["kind"] == "excluded":
        return case["forbidden"] not in docs
    for h in hits:
        if h["source_document"] == case["expected"]:
            return h["retrieval_method"] == "keyword" or (h.get("similarity_score") or 0) >= THRESHOLD
    return False


async def main() -> None:
    passed = 0
    async with Client(SERVER_URL) as client:
        for case in CASES:
            args = {"query": case["query"], "project_id": PROJECT, "top_k": 3,
                    "classification_ceiling": case["ceiling"]}
            if case["kind"] == "rejected":
                try:
                    result = await client.call_tool("retrieve", args)
                    hits = extract_hits(result)
                    print(f"{case['id']}: FAIL (request with ceiling={case['ceiling']} was accepted) [{cite(hits)}]")
                except Exception as exc:  # the server must refuse the request
                    passed += 1
                    print(f"{case['id']}: PASS (rejected) {type(exc).__name__}: {exc}")
                continue
            hits = extract_hits(await client.call_tool("retrieve", args))
            ok = judge(case, hits)
            passed += int(ok)
            print(f"{case['id']}: {'PASS' if ok else 'FAIL'} \"{case['query']}\" [{cite(hits)}]")
    print(f"\n{passed}/{len(CASES)} passed")


if __name__ == "__main__":
    asyncio.run(main())
