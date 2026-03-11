# spring-boot-file-browser-example — LAN File Sharing

> This project profoundly reveals what it means to reach the ultimate realm of boredom: meticulously crafting a LAN file
> sharing using the enterprise-level framework Spring Boot — something that could be done with just a few lines of
> code —
> yet making it so complete and rigorous that you can't help but admire this extreme pursuit of boredom.

## 📖 Project Introduction

**spring-boot-file-browser-example** is a minimalist LAN file sharing based on Spring Boot. It provides basic features
like file listing, online preview, and downloading, along with a clean built‑in web interface. Although the
functionality is simple, the code embodies the "craftsmanship of boredom" everywhere — from path security checks and
Chinese filename encoding to a hardcoded mapping of dozens of MIME types, and finally to packaging into multiple
executable formats for release. Every detail is handled with care. This is the ultimate realm of boredom: **pursuing
perfection in something utterly meaningless, and eventually creating a useful little tool**.

---

## ✨ Features

- 📁 **File Browsing** – List files and folders in a directory, navigate into subdirectories.
- 👁️ **Online Preview** – View images, PDFs, text files directly (if supported by the browser).
- ⬇️ **File Download** – Download files with one click, supports Chinese filenames.
- 🧭 **Friendly Web Interface** – Built‑in HTML page with breadcrumb navigation, file size formatting,
  double‑click/button operations.
- 🛡️ **Path Security** – Strict protection against path traversal attacks (`..` bypass).
- 📦 **Multiple Runtime Options** – Run from source, run as an exe installer (no Java required), or as a lightweight exe
  launcher.
- 🧩 **Cross‑Platform** – Based on Java, theoretically runs on any system with Java support (exe versions are
  Windows‑only).

---

## 🛠️ Tech Stack

- **Java 17+** – Uses `jakarta.servlet`, based on the latest Jakarta EE.
- **Spring Boot 3.x** – Provides REST APIs and embedded web server.
- **Frontend** – Pure HTML/CSS/JavaScript (zero dependencies, native implementation).
- **Packaging Tools** – jpackage (generates installers with bundled JRE), Launch4j (generates exe launchers).

---

## 🚀 Quick Start

### Method 1: Run the Executable Directly (Simplest)

Download the appropriate exe file from [Releases](https://github.com/xumous/spring-boot-file-browser-example/releases):

| File                            | Description                                                                                                                   |
|---------------------------------|-------------------------------------------------------------------------------------------------------------------------------|
| `*_jpackage.exe`                | Installer packaged with jpackage, **includes JRE** — can be run directly after installation (no pre‑installed Java required). |
| `*_launch4j.exe`                | Exe wrapped with Launch4j, **requires JRE 17+** installed on the system — double‑click to run (a console window will appear). |
| `*_hidden-console_launch4j.exe` | Same as above, but **hides the console window**, suitable for background running.                                             |

After launching, open your browser and go to `http://localhost:8080/files/index` to see the file browser interface.

> ⚠️ Note: The default download directory is `F:/common_share`. If it does not exist, the program will create it
> automatically. Ensure that path exists and is accessible, or modify the directory as described below.

### Method 2: Run from Source

1. **Clone the repository**
   ```bash
   git clone https://github.com/xumous/spring-boot-file-browser-example.git
   cd spring-boot-file-browser-example
   ```

2. **Run with Maven**
   ```bash
   mvn spring-boot:run
   ```
   Or import into an IDE and run the `main` method directly.

3. **Change the download directory (optional)**  
   The default download directory is `F:/common_share`. To change it, you can set it programmatically before starting:
   ```java
   com.demo_249050433.controller.FileController.setDownloadDir("your_absolute_path");
   ```
   Or modify the static variable `DOWNLOAD_DIR` in `FileController` directly.

4. **Access the interface**  
   Open your browser: `http://localhost:8080/files/index`

---

## 📝 Usage Instructions

The homepage lists all files and folders in the current directory:

- **Folders**: Click the folder name or the "Open" button to enter the subdirectory.
- **Files**:
    - Click the file name or the "View" button — the browser will try to preview the file in a new tab (if supported, it
      displays directly; otherwise, it will download).
    - Click the "Download" button to download the file directly.
- **Path Bar at the top**: Shows the current directory hierarchy; click any segment to jump directly to that level.

Files are listed with "folders first, sorted by name". File sizes are automatically formatted as B/KB/MB/GB.

---

## 🔍 API Documentation

| Endpoint             | Method | Description                                                                | Example                       |
|----------------------|--------|----------------------------------------------------------------------------|-------------------------------|
| `/files/index`       | GET    | Returns the built‑in HTML interface                                        | `/files/index`                |
| `/files/list?path=`  | GET    | Retrieves the list of files and folders under the given path (JSON format) | `/files/list?path=subfolder`  |
| `/files/view/**`     | GET    | View a file online (`Content-Disposition: inline`)                         | `/files/view/image.jpg`       |
| `/files/download/**` | GET    | Download a file (`Content-Disposition: attachment`)                        | `/files/download/archive.zip` |

**Note**: `**` is a wildcard that matches multiple subdirectories, e.g., `/files/view/documents/report.pdf`.

---

## 🧠 Where Is the Ultimate Realm of Boredom?

- **Using a sledgehammer to crack a nut**: Spring Boot + full REST API, just to make a simple file browser.
- **Security first**: The code strictly validates paths to prevent `../` bypass, even for local runs.
- **Extreme care for filename encoding**: Supports Chinese filenames using RFC 5987 standard encoding, with an ASCII
  fallback.
- **Hardcoded MIME type mapping**: Dozens of file extensions are manually mapped to Content‑Types, covering common
  formats like images, documents, audio, and video.
- **No frontend dependencies**: Pure native JavaScript implements breadcrumb navigation, file size formatting, and
  double‑click/button operations — clean and fully functional.
- **Packing as exe**: To let even non‑Java users experience this boring project, three exe versions are provided (
  installer with bundled JRE, console launcher, hidden‑console launcher), using both jpackage and Launch4j.

---

## ⚠️ Important Notes

- The default download directory is `F:/common_share`; if it doesn't exist, it will be created automatically. Make sure
  this path does not contain sensitive files (if exposed to the public internet).
- The project only provides file browsing — upload, delete, and other operations are **not** supported.
- If used as a public service, be sure to add access control (e.g., login authentication) to avoid leaking private data.
- For exe versions, the hidden‑console launcher may not show program output. If you encounter issues, try the console
  version first for debugging.

---

## 🔧 Customization & Building

### Change the Download Directory

- **From source**: Call `FileController.setDownloadDir("your_path")` in the startup class.
- **Exe versions**: Currently external configuration is not supported; future extensions via environment variables or
  config files are possible.

### Build the Exe Yourself

If you want to experience the packaging process:

- **jpackage** (requires JDK 14+):
  ```bash
  jpackage --name file-browser --input target --main-jar your-app.jar --type exe --win-console
  ```
- **Launch4j**: Use the GUI tool or a configuration file to wrap the jar into an exe.

---

## 🤝 Contributing

Issues and Pull Requests are welcome! But please keep the boring style: any improvement must increase code complexity or
fun **without** adding practicality.

---

## 📄 License

[MIT](LICENSE)

---

> If you find this project boringly interesting, please give it a ⭐️!  