# Changelog

All notable changes to OraIDE are documented here.

## [UNRELEASED]

- Compilers coming soon!
- Compilers for C, C++, C#, Java, and Python are planned for v0.4.x
- v0.3.2 will focus on codebase cleanup, refactoring, and optimization without changing existing functionality.

## [0.3.1] - 2026-09-23

### Added

- Proper file and folder hierarchy visualization in the Explorer
- Depth-based indentation for nested files and folders
- File selection states in the Explorer
- File-type icons for common source and project files
- Improved folder expansion and hierarchy navigation
- Mouse and pointer interaction support
- Middle-click tab closing
- Right-click interaction for Explorer and tab elements
- Double-click interaction support
- Centralized command and keyboard shortcut system
- Keyboard shortcuts for common editor and tab actions
- Keyboard Shortcuts section in Settings
- Unified SearchEngine architecture
- Match Case search option
- Whole Word search option
- Regular Expression search option
- Match counting and result distribution
- Independent search highlighting
- Search functionality across file, folder, and project scopes
- Internal change tracking through `CHANGES.md`

### Changed

- Improved Explorer architecture and file-tree handling
- Improved editor and tab interaction
- Improved keyboard input handling
- Improved mouse and touch input handling
- Centralized common OraIDE actions through `ActionManager`
- Search and matching logic separated from editor UI components
- Search highlighting separated from the base syntax highlighting system
- Improved Search workspace behavior
- Improved Settings integration for keyboard shortcuts
- Improved interaction consistency across the IDE

### Internal Development

- Introduced `ActionManager` and `OraCommand` for centralized IDE actions
- Introduced `ShortcutDefinition` for centralized shortcut definitions
- Introduced `SearchEngine` and `SearchOptions`
- Added internal file-tree depth tracking through `FileNode`
- Added independent editor search highlighting
- Continued architectural cleanup of the Explorer, Search, Editor, and input systems
- Added `CHANGES.md` to maintain a detailed record of implementation changes
- v0.3.1 was developed as the machinery and interaction milestone following the UI-focused v0.3.0 release

### Known Issues

- Some file explorer interactions may still have small clickable regions
- Some project management and workspace edge cases remain under development
- Additional UI refinement and stability improvements are planned for future v0.3.x releases

### Notes

- Pre-alpha development release
- v0.3.0 established the major IDE UI and workspace structure
- v0.3.1 focuses on the underlying interaction, search, Explorer, and command machinery
- No compiler or language toolchain functionality is included in v0.3.1
- Compiler and terminal/toolchain development is planned for v0.4.x.
- I dare the reader to touch some grass.

## [0.3.0] - 2026-09-21

### Added

- Global App Bar
- Activity Bar with dedicated workspaces for Explorer, Search, Git, Run, Terminal, Extensions, and Settings
- Bottom Panel with Terminal, Problems, and Output tabs
- Status Bar
- Dedicated Search workspace
- Find and Replace functionality
- File, Folder, and Project search scopes
- Save As functionality
- Extensions workspace with Installed and Available sections
- UI Scale customization
- Improved editor tab management
- Unsaved changes indicators
- Unsaved changes warning when closing files
- Close All Tabs functionality
- Improved file and folder hierarchy visualization
- Improved project management and workspace handling

### Changed

- Major UI and UX overhaul
- Redesigned tablet-first layout
- Introduced a desktop-class IDE-style workspace layout
- Replaced the previous top bar with the new Global App Bar
- Improved Activity Bar and Explorer organization
- Improved editor tab appearance and behavior
- Improved file explorer hierarchy and indentation
- Improved project switching, creation, deletion, and rename workflows
- Improved theme and UI customization
- Improved responsiveness for tablets and phones
- Improved editor state preservation and tab handling

### Internal Development

- Continued development of the project management architecture
- Improvements developed during the internal v0.2.2.1 build were incorporated into v0.3.0
- v0.2.2.1 was an internal development build and was never publicly released

### Known Issues

- Some project state and workspace references may become inconsistent after certain project rename or switching operations
- Some file explorer and project management edge cases remain under development
- Additional UI refinement and stability work remains for future releases

### Notes

- Major pre-alpha development milestone
- v0.3.0 represents the tentative v0.3 feature set
- v0.3.0 focuses on the IDE workspace, navigation, project management, search, and overall UI/UX foundation
- Future v0.3.x builds will focus further on the IDE UI/UX, adding more features, setting it up for the implementation of compilers in v0.4.x.
- Why are you reading my notes?

## [0.2.2] - 2026-08-17

### Added

- Separate icons for creating files and folders
- Ability to create files and folders inside existing folders
- Multiple project support
- Project switching
- Recent project handling
- Project deletion
- App Info screen
- Dynamic application version display
- Oravadi Project information
- Creator information
- MIT license information and disclaimer
- Links to GitHub, LinkedIn, and Instagram

### Changed

- Improved file explorer navigation
- Improved project management
- Improved project switching and recent project handling
- Improved project creation and deletion workflows

### Notes

- Public release
- Early development / prototype
- There may or may not be something hidden here.


## [0.2.1] - 2026-08-15

### Added
- Initial public prototype
- Tablet-first code editor
- Multi-tab editing
- Syntax highlighting
- File explorer
- File and folder management
- Undo/Redo
- Auto-indentation
- Automatic bracket and quote completion
- Search within files
- Customizable theme

### Notes
- Early development / prototype
- Tested on Android phones and tablets