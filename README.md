# OraIDE

**A tablet-first code editor and development environment for Android.**

OraIDE is an open-source Android application designed from the ground up for coding on tablets, with a touch-friendly interface inspired by modern desktop code editors.
This application also supports Android phones.

The goal is simple, **to bring a proper development environment to Android tablets instead of treating them like oversized phones.**

## Current Status

**Version:** 0.2.x
**Status:** Early development / prototype
**Platform:** Android Tablets and Phones

OraIDE is currently focused on building a polished code-editing experience. Advanced IDE functionality such as compilation, debugging, Git integration, and language servers is planned for future versions.

## License

**OraIDE's source code is licensed under the MIT License.**

*The OraIDE name, logo, and branding are not covered by the MIT License. Modified or forked versions should not imply that they are the official OraIDE project.*

## Features

### Editor

* Multi-tab code editor
* Line numbers
* Syntax highlighting
* Search within files
* Undo / Redo
* Auto-indentation
* Automatic bracket and quote completion
* Current-line highlighting
* Scrollable editor
* Monospace typography

### File Explorer

* Project-based workspace
* Nested folders
* File creation
* Folder creation
* File and folder deletion
* File and folder renaming
* Lazy directory loading
* Multiple projects

### Interface

* Tablet-first landscape layout
* Responsive UI for different screen sizes and aspect ratios
* Android phone support
* Dark interface
* Customizable theme colours
* Material 3
* Jetpack Compose

## Architecture

OraIDE is built using:

* **Kotlin**
* **Jetpack Compose**
* **Material 3**
* **MVVM**
* **Repository pattern**
* **Kotlin Coroutines**

The editor and syntax-highlighting systems are designed to remain modular so that more advanced parsing technologies can be introduced later without requiring a complete rewrite of the editor.

## Planned Features

Future versions may introduce:

* Python execution
* C/C++ compilation
* Java compilation
* Integrated terminal
* Git integration
* Tree-sitter-based parsing
* Language Server Protocol support
* Code completion
* Diagnostics
* Debugging
* Plugin/extension support

These features are intentionally not part of the current release.

## Design Philosophy

Most development environments are designed around a desktop workflow:

> Keyboard + mouse + large monitor

OraIDE takes the opposite approach:

> **Android tablet + touchscreen + optional keyboard**

The interface is designed to remain usable with touch input while taking advantage of the larger displays and hardware available on modern Android tablets, however it also is intended to work well with a mouse and keyboard.

## Development

OraIDE is a vibe-coded project.

The initial prototype was built with the help of **Google Antigravity**, using AI-assisted development. The product direction, architecture, feature requirements, UI design, testing, and iteration are directed by the developer.

The project is also an experiment in how far AI-assisted development can take a genuinely usable Android application.
AI tools used during development include **Google Antigravity with Gemini 3.1 Pro (High)**.

### Building

Clone the repository and open the project in Android Studio or another compatible Android development environment.

Build the debug APK with:

```bash
./gradlew assembleDebug
```

On Windows:

```powershell
.\gradlew assembleDebug
```

The generated debug APK can be found under:

```text
app/build/outputs/apk/debug/
```

## Project Structure

```text
OraIDE/
├── app/
│   └── src/
│       ├── main/
│       ├── test/
│       └── androidTest/
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradlew
└── gradlew.bat
```


## Project

**The Oravadi Project**

OraIDE is developed as part of the Oravadi Project.

---

### Disclaimer

*OraIDE is an independent open-source project inspired by the workflow and usability of modern desktop code editors. It is not affiliated with, endorsed by, or sponsored by Google, Google Antigravity, Gemini, Microsoft, or Visual Studio Code.*