#!/usr/bin/env python3
"""
ReaderParser Design Prototype Local Server & Verification Utility.

Serves docs/design/prototype.html over a local HTTP server with disabled
caching, resolving sandboxed browser access limits (e.g. Snap/Flatpak Firefox)
when working in worktrees located in hidden directories (e.g. ~/.herdr).
"""

import argparse
import http.server
import socket
import socketserver
import sys
import webbrowser
from pathlib import Path


def find_repo_root() -> Path:
    """Resolve the repository root directory."""
    script_path = Path(__file__).resolve()
    for parent in script_path.parents:
        if (parent / "docs" / "design" / "prototype.html").is_file():
            return parent
        if (parent / ".git").exists():
            return parent
    cwd = Path.cwd()
    if (cwd / "docs" / "design" / "prototype.html").is_file():
        return cwd
    return script_path.parents[3] if len(script_path.parents) > 3 else cwd


def is_port_in_use(host: str, port: int) -> bool:
    """Check if a specific host:port is currently bound."""
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.settimeout(0.5)
        return s.connect_ex((host, port)) == 0


def find_available_port(host: str, start_port: int, max_attempts: int = 50) -> int:
    """Find the next available port starting from start_port."""
    port = start_port
    while is_port_in_use(host, port) and max_attempts > 0:
        port += 1
        max_attempts -= 1
    return port


class NoCacheHTTPRequestHandler(http.server.SimpleHTTPRequestHandler):
    """Simple HTTP handler that disables client/proxy caching for live edits."""

    def end_headers(self):
        self.send_header("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0")
        self.send_header("Pragma", "no-cache")
        self.send_header("Expires", "0")
        super().end_headers()

    def log_message(self, fmt, *args):
        sys.stderr.write(f"[{self.log_date_time_string()}] {fmt % args}\n")


def verify_prototype(prototype_path: Path) -> int:
    """Run deterministic validation on prototype.html against design specifications."""
    print("======================================================================")
    print("ReaderParser Prototype Verification Suite (--check)")
    print(f"Target file: {prototype_path}")
    print("======================================================================")

    if not prototype_path.is_file():
        print(f"[FAIL] prototype.html not found at: {prototype_path}")
        return 1

    size = prototype_path.stat().st_size
    if size == 0:
        print("[FAIL] prototype.html is empty (0 bytes)")
        return 1
    print(f"[PASS] File exists and non-empty ({size:,} bytes)")

    try:
        content = prototype_path.read_text(encoding="utf-8")
    except Exception as e:
        print(f"[FAIL] Could not read prototype.html: {e}")
        return 1

    content_lower = content.lower()

    # 1. Verify Theme Tokens
    required_palettes = {
        "WARM_DARK": ["#181614", "#211E1B", "#2A2521", "#E6DFD5", "#A89F93", "#D98A4E", "#7A9A8B"],
        "SEPIA": ["#F4ECD8", "#EFE4C8", "#E4D5B7", "#2C221E", "#5C4B3B", "#8C5327", "#4F6D5A"],
        "LIGHT": ["#FAF8F5", "#FFFFFF", "#F0ECE1", "#1C1B1A", "#605C56", "#B86528"],
        "DARK": ["#111318", "#191C22", "#22262F", "#E2E2E9", "#939AA6", "#A2C8FF", "#BBC7DB"],
    }

    all_tokens_passed = True
    print("\n--- Palette Token Validation ---")
    for theme, tokens in required_palettes.items():
        missing = [t for t in tokens if t.lower() not in content_lower]
        if missing:
            print(f"[FAIL] Theme {theme} missing tokens: {missing}")
            all_tokens_passed = False
        else:
            print(f"[PASS] Theme {theme} ({len(tokens)} required tokens verified)")

    # 2. Verify Unified Reader Engines & Design System Concepts
    required_engine_terms = [
        ("Foundational Primitives", "Foundational Primitives"),
        ("Domain Components", "Domain Components"),
        ("Typography, Shape & Spacing Tokens", "Typography, Shape & Spacing Tokens"),
        ("Component Inventory", "Component Inventory"),
        ("Novel Reader", "Novel Reader"),
        ("Manhwa Reader", "Manhwa Reader"),
        ("Webtoon", "Webtoon"),
        ("Paged LTR", "Paged LTR"),
        ("Paged RTL", "Paged RTL"),
    ]

    all_engines_passed = True
    print("\n--- Unified Reader Engines Validation ---")
    for label, term in required_engine_terms:
        if term.lower() not in content_lower:
            print(f"[FAIL] Missing reader component/contract term: '{term}'")
            all_engines_passed = False
        else:
            print(f"[PASS] Verified presence of '{label}'")

    print("\n======================================================================")
    if all_tokens_passed and all_engines_passed:
        print("[OVERALL RESULT: PASSED] All prototype verification checks succeeded.")
        print("======================================================================")
        return 0
    else:
        print("[OVERALL RESULT: FAILED] One or more verification checks failed.")
        print("======================================================================")
        return 1


def main() -> int:
    repo_root = find_repo_root()
    default_dir = repo_root / "docs" / "design"

    parser = argparse.ArgumentParser(
        description="Serve and verify the ReaderParser interactive design prototype."
    )
    parser.add_argument(
        "--host",
        default="127.0.0.1",
        help="Host address to bind the HTTP server to (default: 127.0.0.1)",
    )
    parser.add_argument(
        "--port",
        type=int,
        default=8765,
        help="Port to serve on (default: 8765; automatically finds next free port if occupied)",
    )
    parser.add_argument(
        "--strict-port",
        action="store_true",
        help="Fail if the requested port is in use instead of searching for the next free port",
    )
    parser.add_argument(
        "--dir",
        type=Path,
        default=default_dir,
        help=f"Directory containing the prototype (default: {default_dir})",
    )
    parser.add_argument(
        "--open",
        action="store_true",
        help="Automatically open the served URL in the default browser",
    )
    parser.add_argument(
        "--check",
        action="store_true",
        help="Perform deterministic validation checks on prototype.html and exit immediately",
    )

    args = parser.parse_args()

    target_file = args.dir / "prototype.html"

    if args.check:
        return verify_prototype(target_file)

    if not target_file.is_file():
        sys.stderr.write(f"Error: Target prototype file not found at {target_file}\n")
        return 1

    serve_port = args.port
    if is_port_in_use(args.host, serve_port):
        if args.strict_port:
            sys.stderr.write(f"Error: Port {serve_port} is already in use.\n")
            return 1
        available_port = find_available_port(args.host, serve_port)
        print(f"Port {serve_port} is in use; incrementing to available port {available_port}.")
        serve_port = available_port

    # Change working directory to serve directory
    handler = NoCacheHTTPRequestHandler

    class ThreadingHTTPServer(socketserver.ThreadingMixIn, http.server.HTTPServer):
        daemon_threads = True

    try:
        with ThreadingHTTPServer((args.host, serve_port), lambda *a, **kw: handler(*a, directory=str(args.dir), **kw)) as httpd:
            url = f"http://{args.host}:{serve_port}/prototype.html"
            print("======================================================================")
            print("ReaderParser Interactive Design Prototype Server")
            print("======================================================================")
            print(f"Serving directory : {args.dir}")
            print(f"Prototype URL     : {url}")
            print("Cache-Control     : disabled (no-store, live refresh on edit)")
            print("Press Ctrl+C to terminate the server.")
            print("======================================================================")

            if args.open:
                print(f"Opening browser at: {url}")
                webbrowser.open(url)

            httpd.serve_forever()
    except KeyboardInterrupt:
        print("\nServer terminated by user. Exiting cleanly.")
        return 0
    except Exception as e:
        sys.stderr.write(f"Server error: {e}\n")
        return 1


if __name__ == "__main__":
    sys.exit(main())
