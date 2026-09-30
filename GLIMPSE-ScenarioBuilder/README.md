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

The checked-in `.classpath` files reference the JavaFX jars under `libs/javafx-21/win/` so Eclipse can resolve `javafx.*` imports without a manually configured user library.
If you are working on Linux or macOS and want your local Eclipse classpath to match that OS instead, update the jar paths in `.classpath` and `src/.classpath` to the corresponding `linux`, `mac`, or `mac-aarch64` folder.
