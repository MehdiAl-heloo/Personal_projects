from pathlib import Path
from time import sleep
from watchdog.observers import Observer
from monitor import FileSorter

if __name__ == '__main__':
    # Define the source (Desktop) and destination directories
    source = Path.home() / 'OneDrive' / 'Desktop'
    target = Path.home() / 'Desktop/Organized_Files'

    # Initialize the file handler and observer
    handler = FileSorter(source_dir=source, target_root=target)
    watcher = Observer()
    watcher.schedule(handler, path=str(source), recursive=True)
    watcher.start()

    print(f"Watching: {source}\nSaving to: {target}\nPress Ctrl+C to quit.")
    try:
        # Keep script running until manually interrupted
        while True:
            sleep(60)
    except KeyboardInterrupt:
        print("\nStopping observer...")
        watcher.stop()
    watcher.join()