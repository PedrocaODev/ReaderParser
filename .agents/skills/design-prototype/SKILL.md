---
name: design-prototype
description: Inspect, verify, and serve the ReaderParser interactive design prototype for Novel and Manhwa reading experiences and the 1:1 Design System reference. Use when validating UI specifications, testing theme tokens, previewing reader interactions, or verifying design contracts before Android Compose implementation.
---

# design-prototype

The `design-prototype` skill provides tools and workflows to inspect, verify, and serve the interactive HTML/CSS/JS design prototype located at `docs/design/prototype.html`. It simulates ReaderParser's mobile UX, theme tokens, code-agnostic design system primitives, and unified reader experiences across Novel and Manhwa formats.

## Why a Local HTTP Server is Required

On Linux systems, browsers installed via **Snap** or **Flatpak** (such as default Ubuntu Firefox Snap or Chrome Flatpak) run in strict containerized sandbox environments. 

These sandboxes:
- Block access to files under hidden directories (such as `~/.herdr/worktrees/...` or `~/.gemini/...`) when accessed via `file:///` URLs.
- Can fail silently or display blank pages due to restricted local filesystem permissions.

The bundled Python server (`serve_prototype.py`) serves the prototype over `http://127.0.0.1:<port>`, cleanly bypassing sandbox restrictions. It also injects `Cache-Control: no-cache, no-store, must-revalidate` headers to ensure UI modifications reload immediately.

---

## Quick Start Commands

All commands run from the repository root:

### 1. Verify Prototype Integrity (Zero-Server Check)
Run deterministic static checks on `docs/design/prototype.html` to confirm theme tokens and reader contract compliance:

```bash
./.agents/skills/design-prototype/scripts/serve_prototype.py --check
```

### 2. Launch Local Server
Serve the prototype on an available local port (defaults to 8765 with automatic fallback up to 8775):

```bash
./.agents/skills/design-prototype/scripts/serve_prototype.py
```

### 3. Launch with Specific Port or Open in Browser
```bash
# Launch on port 8080 and attempt to open default browser
./.agents/skills/design-prototype/scripts/serve_prototype.py --port 8080 --open

# Require exact port (fail if unavailable instead of falling back)
./.agents/skills/design-prototype/scripts/serve_prototype.py --port 8765 --strict-port
```

---

## CLI Options Reference

The `serve_prototype.py` script has zero third-party dependencies (Python 3 standard library only).

| Option | Type | Default | Description |
|---|---|---|---|
| `--port` | integer | `8765` | Initial TCP port to bind. |
| `--strict-port` | flag | `false` | Disable port fallback; fails immediately if `--port` is occupied. |
| `--host` | string | `127.0.0.1` | Network interface to bind. |
| `--dir` | path | Auto (repo root) | Custom directory containing `docs/design/prototype.html`. |
| `--open` | flag | `false` | Attempt to open default web browser on server start. |
| `--check` | flag | `false` | Run automated contract verification suite and exit without serving. |

---

## Design Contract Reference

| Component / Layer | Contract Identifier | Specification & Behaviors |
|---|---|---|
| **Theme System** | `WARM_DARK` | Background `#181614`, Surface `#211E1B`, Container `#2A2521`, Text `#E6DFD5` / `#A89F93`, Accent `#D98A4E` / `#7A9A8B`. |
| | `SEPIA` | Background `#F4ECD8`, Surface `#EFE4C8`, Container `#E4D5B7`, Text `#2C221E` / `#5C4B3B`, Accent `#8C5327` / `#4F6D5A`. |
| | `LIGHT` | Background `#FAF8F5`, Surface `#FFFFFF`, Container `#F0ECE1`, Text `#1C1B1A` / `#605C56`, Accent `#B86528`. |
| | `DARK` | Background `#111318`, Surface `#191C22`, Container `#22262F`, Text `#E2E2E9` / `#939AA6`, Accent `#A2C8FF` / `#BBC7DB`. |
| **Navigation Shell** | 7 Screens & Shell Controls | 7 screens (`Library`, `Browse`, `Downloads`, `Series Detail`, `Novel Reader`, `Manhwa Reader`, `Settings`), top app bar with context actions (Sort, Refresh, More menu, Settings gear), external state simulator bar (Populated, Loading, Empty, Error, Chrome Hidden, Page Error, Content Mismatch), and 4-card Design System Catalog. |
| **Series Detail** | Reading & Series Hub | Responsive 2:3 cover hero card, title, author/source metadata, status chip, inline library bookmark toggle, expandable synopsis, reading progress card, and chapter list with state indicators (Unread, Reading, Completed, Downloaded), Asc/Desc filters, and chapter action sheet. |
| **Novel Reader** | Continuous Vertical Scroll | Continuous vertical prose canvas with user typography controls (Font Family, Font Size, Line Height, Paragraph Spacing, Margins), immersive top/bottom bars, floating progress pill, scrub bar, and in-reader chapter list sheet. |
| **Manhwa Reader** | Multi-Layout Visual Reader | Multi-layout visual canvas (`Webtoon`, `Paged LTR`, `Paged RTL`, `Fit Width`, `Fit Height`, `Original Scale`), 150px minimum measurable image placeholders, error recovery cards, format mismatch handling, immersive controls, floating progress pill, and in-reader chapter list sheet. |
