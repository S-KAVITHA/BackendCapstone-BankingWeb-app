"""Run the proj-csv ground-truth queries (docs/retrieval-ground-truth.md) against a live
retrieval MCP server. Prints the full top 3 for every query (source#chunk, classification,
method, score) and a measured PASS/FAIL using each query's written pass rule."""
from __future__ import annotations

import asyncio
import os
from typing import Any

from fastmcp import Client

THRESHOLD = 0.65
SERVER_URL = os.getenv("RETRIEVAL_URL", "http://localhost:8002/mcp")
PROJECT = "proj-csv"

# One case per query in docs/retrieval-ground-truth.md, with that query's written pass rule.
# kind:
# - precision: expected doc in top 3 with similarity_score >= THRESHOLD
# - rank_above: expected doc in top 3 and ranked above the decoy doc
# - ceiling: forbidden doc must be absent at the internal ceiling
CASES: list[dict[str, Any]] = [
    {"id": "Q1", "kind": "precision", "query": "What file format did we choose for exporting the task list?",
     "expected": "decision-csv-format.md"},
    {"id": "Q2", "kind": "precision", "query": "How does the CSV export feature work?",
     "expected": "feature-csv-export.md"},
    {"id": "Q3", "kind": "rank_above", "query": "How do we export tasks to CSV?",
     "expected": "feature-csv-export.md", "decoy": "feature-csv-import.md"},
    {"id": "Q4", "kind": "precision", "query": "What does error code E_EXPORT_417 mean?",
     "expected": "error-codes.md"},
    {"id": "Q5", "kind": "ceiling", "query": "What are the internal cost figures for the export feature?",
     "forbidden": "cost-breakdown.md"},
]


def as_dict(item: Any) -> dict[str, Any]:
    if isinstance(item, dict):
        return item
    if hasattr(item, "model_dump"):
        return item.model_dump()
    return dict(vars(item))


def extract_hits(result: Any) -> list[dict[str, Any]]:
    # Prefer the raw JSON; fastmcp's .data may wrap items in typed objects.
    for attr in ("structured_content", "structuredContent", "data"):
        value = getattr(result, attr, None)
        if value is not None:
            if isinstance(value, dict) and "result" in value:
                value = value["result"]
            return [as_dict(item) for item in value]
    raise RuntimeError(f"Could not extract structured hits from result: {result!r}")


def cite(hits: list[dict[str, Any]]) -> str:
    if not hits:
        return "(no results)"
    return " | ".join(
        f"{n}. {h['source_document']}#chunk{h['chunk_index']} "
        f"({h['classification']}, {h['retrieval_method']}, score={h.get('similarity_score')})"
        for n, h in enumerate(hits, 1)
    )


def judge(case: dict[str, Any], hits: list[dict[str, Any]]) -> tuple[bool, str]:
    docs = [h["source_document"] for h in hits]
    if case["kind"] == "ceiling":
        ok = case["forbidden"] not in docs
        return ok, f"{case['forbidden']} {'absent' if ok else 'PRESENT'}"
    if case["expected"] not in docs:
        return False, f"{case['expected']} not in top 3"
    pos = docs.index(case["expected"])
    hit = hits[pos]
    if case["kind"] == "rank_above":
        if case["decoy"] in docs and docs.index(case["decoy"]) < pos:
            return False, f"{case['decoy']} ranked above {case['expected']}"
        return True, f"{case['expected']} at rank {pos + 1}, above {case['decoy']}"
    score = hit.get("similarity_score")
    if score is None or score < THRESHOLD:
        return False, (f"{case['expected']} at rank {pos + 1} via {hit['retrieval_method']}, "
                       f"score {score} below {THRESHOLD}")
    return True, f"{case['expected']} at rank {pos + 1}, score {score}"


async def main() -> None:
    passed = 0
    print(f"Server: {SERVER_URL}  project: {PROJECT}  ceiling: internal  top_k: 3  threshold: {THRESHOLD}\n")
    async with Client(SERVER_URL) as client:
        for case in CASES:
            result = await client.call_tool("retrieve", {
                "query": case["query"], "project_id": PROJECT, "top_k": 3,
                "classification_ceiling": "internal"})
            hits = extract_hits(result)
            ok, why = judge(case, hits)
            passed += int(ok)
            print(f"{case['id']}: {'PASS' if ok else 'FAIL'} - {why}")
            print(f"    query: \"{case['query']}\"")
            print(f"    top 3: {cite(hits)}\n")
    print(f"{passed}/{len(CASES)} passed")


if __name__ == "__main__":
    asyncio.run(main())
