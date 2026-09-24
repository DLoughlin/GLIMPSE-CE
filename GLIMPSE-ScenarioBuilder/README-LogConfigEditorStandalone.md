# Standalone Log Configuration Editor

This module now includes a standalone launcher:

- `glimpseElement.LogConfigEditorLauncher`

That lets the log configuration editor run independently from the full GLIMPSE Scenario Builder.

## What you need to do

### 1) Build the project so the new launcher compiles

In Eclipse, make sure `GLIMPSE-ScenarioBuilder` builds successfully.

If you see this error when launching from Eclipse:

> JavaFX runtime components are missing, and are required to run this application

then your Eclipse run configuration is missing the JavaFX VM arguments.

Use these settings for a normal test run from Eclipse:

- **Main class:** `glimpseElement.LogConfigEditorLauncher`
- **Project:** `GLIMPSE-ScenarioBuilder`
- **VM arguments:**

```text
--module-path "${workspace_loc:/GLIMPSE-ScenarioBuilder}/libs/javafx-21/win"
--add-modules=javafx.controls,javafx.fxml
```

If you later launch on Linux, change the module-path folder to `libs/javafx-21/linux` if that directory is present in your checkout.

### 2) Export a second runnable JAR

Create a separate runnable JAR for the log editor, using:

- **Main class:** `glimpseElement.LogConfigEditorLauncher`

Keep the existing Scenario Builder JAR unchanged; this new one is a second artifact.

### 3) Package it the same way as the main app

The project already uses the Eclipse JAR-in-JAR style manifest for the main app.
For the standalone editor JAR, use the same packaging approach, but point the runnable entry to:

- `glimpseElement.LogConfigEditorLauncher`

### 4) Decide where `log_conf.xml` comes from

When the editor starts, it looks for a default file in this order:

1. the GCAM executable directory from the GLIMPSE options
2. the current working directory
3. the directory containing the editor JAR

You can also pass a file path on launch, and the editor will open that file directly.

### 5) Make it easy to double-click

Once you have the standalone JAR, rename it clearly, for example:

- `GLIMPSE-LogConfigEditor.jar`

Then users can double-click it like the main app JAR.

## Optional launch shortcut

If you want a shortcut that opens a specific file, you can use a command like:

```powershell
java -jar GLIMPSE-LogConfigEditor.jar C:\path\to\log_conf.xml
```

## Files added for this

- `src/glimpseElement/LogConfigEditorLauncher.java`
- `src/glimpseElement/LogConfigEditorWidget.java` updated to support standalone startup

## Notes

- If the editor is launched without GLIMPSE, dialogs now open without needing a parent window.
- The editor remains usable even if no default `log_conf.xml` is found.
