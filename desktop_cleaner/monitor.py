import shutil
from datetime import date
from pathlib import Path
from watchdog.events import FileSystemEventHandler
from categories import category_paths

def ensure_dated_path(base: Path):
    """
    Create a subfolder inside the given path based on the current year and month.
    """
    today = date.today()
    structured_path = base / str(today.year) / f"{today.month:02d}"
    structured_path.mkdir(parents=True, exist_ok=True)
    return structured_path

def get_unique_filename(original: Path, folder: Path):
    """
    Prevent filename conflicts by appending an index if the file exists.
    """
    proposed = folder / original.name
    counter = 1
    while proposed.exists():
        proposed = folder / f"{original.stem}_{counter}{original.suffix}"
        counter += 1
    return proposed

class FileSorter(FileSystemEventHandler):
    def __init__(self, source_dir: Path, target_root: Path):
        # Set up paths to monitor and organize
        self.source_dir = source_dir.resolve()
        self.target_root = target_root.resolve()

    def on_modified(self, event):
        # Process each file in the monitored directory
        for item in self.source_dir.iterdir():
            if not item.is_file():
                continue
            ext = item.suffix.lower()
            if ext not in category_paths:
                continue

            # Build final destination directory and resolve file name
            dest_subdir = self.target_root / category_paths[ext]
            dated_dir = ensure_dated_path(dest_subdir)
            destination = get_unique_filename(item, dated_dir)

            # Move file and handle any errors
            try:
                shutil.move(str(item), str(destination))
            except Exception as e:
                print(f"Error moving {item.name}: {e}")