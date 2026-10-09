---
id: evidence-agent-maintenance-lessons
title: "Agent maintenance lessons"
type: Evidence
status: active
valid_from: 2026-10-08
scope:
  system: readerparser
relations:
  - type: relates_to
    target: ../architecture.md
---

# Agent maintenance lessons

## Retrieval and bounded output

Headroom generated the source lessons on 2026-07-08. Migration synthesis dates from 2026-10-08.
Keep the generated source block unchanged until its source passes retirement verification.
Read the exact file/range once with enough output budget. Use targeted rg/sed for large files instead of repeated generic recovery reads.
Batch related file edits into coherent apply_patch hunks. Avoid long sequences of tiny patch/edit/write calls for one feature area.
Start from the referenced file/test/command and nearest local rules. Load architecture for contracts/core data flow and subject nodes for navigation.
Avoid repository-wide globs when a known area is sufficient. Stop repeated project keyword probes when the relevant package lives in an installed environment.

## State and verification discipline

Capture bounded git status/diff/stat/name/log once per phase. Repeat only when state changes.
Use git log --oneline with a count. After merged PR delete-branch, fetch/prune and verify instead of deleting the remote twice.
Keep one stable task list. Update only changed status or material plan changes.
Run Gradle with plain output and SDK variables pointing at an actual SDK. Capture a failure/report before a fix and justified rerun.
Do not loop broad assemble/compile/test commands or tail-only failures without diagnosis.
Target LibraryViewModel/SeriesRepository test loops only after inspecting their complete first failure.

## Runtime and tooling facts

Use python3, not python. Prefer unittest for small scratch tests when pytest is absent.
Validate YAML with Python/PyYAML when available. Recorded ruby/rtk ruby paths were unavailable.
Inspect installed package internals with that package's actual environment interpreter rather than assume system imports.
The historical Headroom venv was `/home/pedro/.local/share/pipx/venvs/headroom-ai/bin/python`.
Headroom learn should use the Codex backend by default. OpenCode requires explicit HEADROOM_LEARN_CLI=opencode.
Inspect headroom.learn.analyzer once instead of repeated help/probes or unrelated repository searches.
Do not rerun rtk init/status/log/adb outputs without changed config/history/device state.

## Android platform and API diagnosis

Android Log/Uri/ContentResolver/ContentValues can break JVM tests without platform support.
Isolate platform calls behind fakes or use instrumentation rather than repeatedly rerun the same unsupported test.
Inspect the installed WorkManager testing AAR/JAR once before guessing TestListenableWorkerBuilder signatures.
When SearchIndexSyncer fails on android.util.Log, choose fake logging/platform isolation or instrumentation before repeating tests.

## Retired OpenSpec command lessons

The recorded correct commands were openspec list then status/instructions for an existing valid named change.
openspec changes was invalid. instructions proposal required --change. openspec update did not take --change.
House-style smoke repos required valid schema metadata/template paths and a created change before status/instructions.
These details diagnose historical workflow failures. They are not instructions to reintroduce OpenSpec.

## Release and dashboard work

Inspect release.yml and bounded gh release/PR data once for release research. Validate workflow YAML with Python/PyYAML.
For dashboard launch snippets, use python3 and export LOG_FILE/PID_FILE in the same command that reads them.
Check an existing dashboard PID once before relaunching.
If Vite attempts a Windows browser path, preserve the printed URL instead of looping dashboard launches.
Historical generated token-savings estimates are telemetry, not technical requirements, and are intentionally not retained as project guarantees.
