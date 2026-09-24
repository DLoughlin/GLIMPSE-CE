# GLIMPSE-ScenarioBuilder

## Persisted window preferences

`GLIMPSE-ScenarioBuilder.properties` stores window and UI preferences so they persist between launches.

- Main window:
  - `window.width`
  - `window.height`
  - `window.x`
  - `window.y`
  - `font.size`
- New Scenario Component Creator:
  - `component.creator.window.width`
  - `component.creator.window.height`
  - `component.creator.window.x`
  - `component.creator.window.y`
- GLIMPSE Console:
  - `console.window.width`
  - `console.window.height`
  - `console.window.x`
  - `console.window.y`

The app resolves the properties file location at runtime (typically near the ScenarioBuilder install/JAR directory).

## Build and run (Gradle, cross-platform)

This project uses Gradle as the source of truth for dependencies, and the Gradle wrapper is committed in `gradlew`, `gradlew.bat`, and `gradle/wrapper/`. In Eclipse, import it with Buildship so JavaFX and the rest of the classpath are resolved from `build.gradle` instead of a hand-maintained `.classpath`.

- Java: 21
- Main class: `gui.Client`
- JavaFX modules are resolved with the OpenJFX Gradle plugin (no OS-specific `.classpath` edits needed).

### 1) Build

```powershell
cd C:\Users\danlo\git\GLIMPSE-CE\GLIMPSE-ScenarioBuilder
.\gradlew.bat clean build
```

### 2) Run

```powershell
cd C:\Users\danlo\git\GLIMPSE-CE\GLIMPSE-ScenarioBuilder
.\gradlew.bat run
```

### Eclipse import (recommended)

Use Buildship and import as a Gradle project:

1. `File -> Import -> Gradle -> Existing Gradle Project`
2. Select `GLIMPSE-ScenarioBuilder`
3. Let Buildship refresh the project and manage the classpath/module path

The Gradle build also pulls in `../GLIMPSE-ModelInterface/GLIMPSE-ModelInterface.jar`, so both Eclipse and command-line builds resolve the sibling project dependency without manual classpath edits.

### Updating the wrapper

If you need to bump the Gradle version later, run the wrapper task from a machine that already has Gradle installed and commit the regenerated wrapper files:

```powershell
cd C:\Users\danlo\git\GLIMPSE-CE\GLIMPSE-ScenarioBuilder
gradle wrapper --gradle-version 8.10.2
```
