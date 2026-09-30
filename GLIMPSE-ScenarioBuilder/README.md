# GLIMPSE ScenarioBuilder

This folder contains the ScenarioBuilder application JAR and launch helpers.

## Java 21 + JavaFX requirement

`GLIMPSE-ScenarioBuilder.jar` is cross-platform, but it must be launched with a matching JavaFX runtime for the target OS.

Current launcher/runtime layout:

- Windows: `libs/javafx-21/win`
- Linux: `libs/javafx-21/linux`
- macOS Intel: `libs/javafx-21/mac`
- macOS Apple Silicon: `libs/javafx-21/mac-aarch64`

The macOS JavaFX jars are checked in under:

```text
GLIMPSE-ScenarioBuilder/libs/javafx-21/mac/
GLIMPSE-ScenarioBuilder/libs/javafx-21/mac-aarch64/
```

Each folder should contain these jars:

- `javafx-base-21.0.4-mac.jar`
- `javafx-controls-21.0.4-mac.jar`
- `javafx-fxml-21.0.4-mac.jar`
- `javafx-graphics-21.0.4-mac.jar`

and the matching `-mac-aarch64.jar` files for Apple Silicon.

## Launching on macOS

Use the macOS launcher from this folder:

```bash
chmod +x run-GLIMPSE-ScenarioBuilder_Mac.sh
./run-GLIMPSE-ScenarioBuilder_Mac.sh
```

The script prefers `JAVA_HOME`, then `/usr/libexec/java_home -v 21`, and finally `java` on `PATH`.

## Eclipse setup

The project `.classpath` files now rely on the `GLIMPSE_JAVAFX_21` user library rather than hard-coded Windows jars.
Configure that user library with the JavaFX jars for your operating system.
