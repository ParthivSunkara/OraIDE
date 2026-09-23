# OraIDE

**A tablet-first code editor and development environment for Android.**

OraIDE is an open-source Android application designed from the ground up for coding on tablets, with a touch-friendly interface inspired by modern desktop code editors.

The application also supports Android phones.

The goal is simple: **to bring a proper development environment to Android tablets instead of treating them like oversized phones.**

## Current Status

**Version:** 0.3.1  
**Status:** Pre-alpha  
**Platform:** Android Tablets and Phones

OraIDE is currently focused on building the foundation of a complete development environment for Android.

The v0.3.x development cycle focuses on establishing the IDE workspace, editor interaction, project management, file navigation, search, keyboard and mouse support, and the underlying architecture required for future compiler and toolchain integration.

Compilers, terminal functionality, debugging, Git integration, language servers, and other advanced development features are planned for future versions.

## License

**OraIDE's source code is licensed under the MIT License.**

*The OraIDE name, logo, and branding are not covered by the MIT License. Modified or forked versions should not imply that they are the official OraIDE project.*

## Features

### Editor

* Multi-tab code editor
* Line numbers
* Syntax highlighting
* Search within files
* Find and Replace
* Undo / Redo
* Auto-indentation
* Automatic bracket and quote completion
* Current-line highlighting
* Scrollable editor
* Monospace typography
* Unsaved changes indicators
* Save As
* Improved tab management

### File Explorer

* Project-based workspace
* Nested folders
* File creation
* Folder creation
* File and folder deletion
* File and folder renaming
* Lazy directory loading
* Multiple projects
* Project switching
* Recent projects
* File and folder hierarchy visualization
* Depth-based indentation
* File selection
* File-type icons
* Expandable folder hierarchy

### Search

* Dedicated Search workspace
* Find
* Find and Replace
* File search
* Folder search
* Project search
* Match Case
* Whole Word
* Regular Expression search
* Match counting
* Search result distribution
* Search result highlighting

### Interface

* Tablet-first landscape layout
* Responsive UI for different screen sizes and aspect ratios
* Android phone support
* Dark interface
* Customizable theme colours
* Material 3
* Jetpack Compose
* Global App Bar
* Activity Bar
* Status Bar
* Bottom Panel
* IDE-style workspace layout
* UI Scale customization

### Input

* Touch interaction
* Keyboard shortcuts
* Mouse interaction
* Right-click interaction
* Middle-click tab closing
* Double-click interaction
* Centralized command/action system

### Workspaces

* Explorer workspace
* Search workspace
* Git workspace
* Run workspace
* Terminal workspace
* Extensions workspace
* Settings workspace
* Problems panel
* Output panel

> Some workspaces currently serve as foundations or placeholders for functionality planned for future releases.

## Architecture

OraIDE is built using:

* **Kotlin**
* **Jetpack Compose**
* **Material 3**
* **MVVM**
* **Repository pattern**
* **Kotlin Coroutines**

The application uses centralized systems for common IDE actions and search functionality.

Key architectural components include:

* `ActionManager`
* `OraCommand`
* `ShortcutDefinition`
* `SearchEngine`
* `SearchOptions`
* `FileNode`

The editor, search, file-management, and interaction systems are designed to remain modular so that more advanced functionality can be introduced without requiring a complete rewrite of the existing application.

## Planned Features

Future versions are planned to introduce:

* Integrated terminal
* C compilation and execution
* C++ compilation and execution
* C# compilation and execution
* Java compilation and execution
* Python execution
* Cross-language toolchain support
* Git integration
* Tree-sitter-based parsing
* Language Server Protocol support
* Code completion
* Diagnostics
* Debugging
* Plugin/extension support

The v0.4.x development cycle is planned to begin the implementation of compiler and terminal/toolchain functionality.

These features are intentionally not part of the current release.

## Design Philosophy

Most development environments are designed around a desktop workflow:

> Keyboard + mouse + large monitor

OraIDE takes a different approach:

> **Android tablet + touchscreen + optional keyboard and mouse**

The interface is designed to remain usable with touch input while taking advantage of the larger displays and hardware available on modern Android tablets.

At the same time, OraIDE supports traditional keyboard and mouse interaction for users who want a more desktop-like workflow.

## Development

OraIDE is a vibe-coded project.

The initial prototype was built with the help of **Google Antigravity**, using AI-assisted development. The product direction, architecture, feature requirements, UI design, testing, and iteration are directed by the developer.

The project is also an experiment in how far AI-assisted development can take a genuinely usable Android application.

AI tools used during development include **Google Antigravity with Gemini 3.1 Pro (High)**.

### Building

Clone the repository and open the project in Android Studio or another compatible Android development environment.

Build the debug APK with:


./gradlew assembleDebug


On Windows:

(powershell)
.\gradlew assembleDebug


The generated debug APK can be found under:


app/build/outputs/apk/debug/


## Project Structure

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


## Project

**The Oravadi Project**

OraIDE is developed as part of the Oravadi Project.

---

### Disclaimer

*OraIDE is an independent open-source project inspired by the workflow and usability of modern desktop code editors. It is not affiliated with, endorsed by, or sponsored by Google, Google Antigravity, Gemini, Microsoft, or Visual Studio Code.*
