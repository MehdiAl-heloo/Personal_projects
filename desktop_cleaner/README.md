# Desktop File Organizer

A Python tool that automatically organizes your Desktop files by file type and date.

---

## Features

- Monitors your Desktop folder in real-time
- Moves files into categorized folders (e.g., `docs/pdf`, `media/images`)
- Organizes files by year and month (e.g., `2025/03`)
- Renames duplicate files to avoid overwriting
- Lightweight, modular, and easy to customize

---

## How It Works

1. Watches your Desktop using the `watchdog` library
2. Detects any new or modified files
3. Moves them to `Organized_Files`, sorted by extension and date

---
