# Portable release packaging for GLIMPSE-CE

These helpers stage a release bundle that matches the launcher layout already used by the ScenarioBuilder scripts.

For the full release runbook (step-by-step process, artifact strategy, and release contents), see `packaging/RELEASE_PROCESS.md`.

## Expected runtime layout

The launchers look for Corretto 21 here:

- Windows: `runtime/corretto-21/windows-x64/bin/java.exe`
- Linux: `runtime/corretto-21/linux-x64/bin/java`

## Windows packaging

Use a local Corretto 21 runtime or JDK and point the script at its root folder:

```powershell
pwsh -File .\packaging\package-portable-release.ps1 -CorrettoHome C:\path\to\amazon-corretto-21\windows-x64
```

Output:

- `dist\GLIMPSE-CE-portable-windows-x64.zip`

## Linux packaging

Set `CORRETTO_HOME` to a Corretto 21 Linux runtime or JDK root folder:

```bash
CORRETTO_HOME=/path/to/amazon-corretto-21-linux-x64 ./packaging/package-portable-release.sh
```

Output:

- `dist/GLIMPSE-CE-portable-linux-x64.tar.gz`

## What gets packaged

The packers stage the same project layout the launchers already expect:

- `GLIMPSE-ScenarioBuilder/`
- `GLIMPSE-ModelInterface/`
- `runtime/corretto-21/...`
- root `run_*.bat` / `run_*.sh` launchers
- GCAM options files

## jpackage installers and app images

The `jpackage` helpers consume the same staged portable bundle and produce either app images or installers.

### Windows

```powershell
pwsh -File .\packaging\package-jpackage-windows.ps1 -PortableRoot .\dist\GLIMPSE-CE-portable -PackageType app-image
```

Supported `-PackageType` values:

- `app-image`
- `msi`
- `exe`

The output is staged under `dist\jpackage\windows\`.

### Linux

```bash
PORTABLE_ROOT=./dist/GLIMPSE-CE-portable ./packaging/package-jpackage-linux.sh
```

Set `PACKAGE_TYPE=app-image` for a portable app image, or use another supported `jpackage` package type available on your Linux host.

The output is staged under `dist/jpackage/linux/`.

## One-command release wrappers

If you want staging + `jpackage` in one step, use the wrapper scripts.

### Windows wrapper

```powershell
pwsh -File .\packaging\build-release-windows.ps1 -CorrettoHome C:\path\to\amazon-corretto-21\windows-x64 -PackageType msi
```

### Linux wrapper

```bash
CORRETTO_HOME=/path/to/amazon-corretto-21-linux-x64 PACKAGE_TYPE=app-image ./packaging/build-release-linux.sh
```

Wrapper defaults:

- Windows `PackageType`: `msi`
- Linux `PACKAGE_TYPE`: `app-image`

## Notes

- These scripts build on the portable bundle workflow so the Corretto 21 runtime and application folders stay aligned.
- `jpackage` still requires platform-specific tooling to be present on the build host, so use the Windows helper on Windows and the Linux helper on Linux.
