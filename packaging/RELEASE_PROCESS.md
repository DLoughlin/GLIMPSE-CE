# GLIMPSE-CE release process

This runbook describes the release process for building GLIMPSE-CE with a bundled Amazon Corretto 21 runtime.

## Scope

This process covers:

- creating portable release bundles,
- creating `jpackage` app images/installers,
- validating artifacts before publishing,
- and listing exactly what is included in each release.

## Prerequisites

- A clean working tree for release tagging (recommended).
- Corretto 21 available locally:
  - Windows: folder containing `bin/java.exe`
  - Linux: folder containing `bin/java`
- `jpackage` available on the build host `PATH` if creating installers/app images.
- Build host matches artifact platform:
  - Windows host for `msi`/`exe`
  - Linux host for Linux `jpackage` artifacts

## Release artifact strategy (recommended)

- Windows primary: `msi`
- Windows fallback: portable zip and/or `app-image` zip
- Linux primary: `app-image` tar.gz
- Linux optional: distro-native package type (`deb`/`rpm`) when needed

## Step-by-step release workflow

### 1) Choose release version metadata

Decide the release label (for example `8.2-v2026.08.24`) and ensure:

- `AppVersion` argument for the Windows packager is set as desired,
- `APP_VERSION` environment variable for Linux packager is set as desired,
- release notes/changelog are updated.

### 2) Build Windows release artifacts

Run from repo root:

```powershell
pwsh -File .\packaging\build-release-windows.ps1 `
  -CorrettoHome C:\path\to\amazon-corretto-21\windows-x64 `
  -PackageType msi `
  -AppVersion 8.2
```

Optional Windows app image:

```powershell
pwsh -File .\packaging\build-release-windows.ps1 `
  -CorrettoHome C:\path\to\amazon-corretto-21\windows-x64 `
  -PackageType app-image `
  -AppVersion 8.2
```

### 3) Build Linux release artifacts

Run from repo root:

```bash
CORRETTO_HOME=/path/to/amazon-corretto-21-linux-x64 \
PACKAGE_TYPE=app-image \
APP_VERSION=8.2 \
./packaging/build-release-linux.sh
```

Optional Linux distro package (host/tooling dependent):

```bash
CORRETTO_HOME=/path/to/amazon-corretto-21-linux-x64 \
PACKAGE_TYPE=deb \
APP_VERSION=8.2 \
./packaging/build-release-linux.sh
```

### 4) Validate artifacts (smoke test)

For each produced artifact:

- install/unpack on a clean machine (or VM),
- launch ScenarioBuilder,
- open a known options file,
- confirm app starts without requiring a system-installed Java,
- verify no immediate startup errors in terminal/log output.

### 5) Publish

Attach the generated artifacts to the release tag and include:

- platform,
- artifact type (`msi`, `app-image`, portable),
- included Java runtime baseline (Corretto 21),
- known limitations (if any).

## What each release includes

### Portable bundle contents

The portable bundle staged at `dist/GLIMPSE-CE-portable` contains:

- `GLIMPSE-ScenarioBuilder/`
- `GLIMPSE-ModelInterface/`
- `runtime/corretto-21/windows-x64/` (Windows build)
- `runtime/corretto-21/linux-x64/` (Linux build)
- root launchers:
  - `run_GLIMPSE_GCAM-USA-8.2-windows.bat`
  - `run_GLIMPSE_GCAM-global-8.2-windows.bat`
  - `run_GLIMPSE_GCAM-USA-8.2-linux.sh`
  - `run_GLIMPSE_GCAM-global-8.2-linux.sh`
- root options files:
  - `options_GCAM-USA-8.2-windows.txt`
  - `options_GCAM-global-8.2-windows.txt`
  - `options_GCAM-USA-8.2-linux.txt`
  - `options_GCAM-global-8.2-linux.txt`

Portable archive outputs:

- Windows: `dist/GLIMPSE-CE-portable-windows-x64.zip`
- Linux: `dist/GLIMPSE-CE-portable-linux-x64.tar.gz`

### jpackage outputs

From staged portable input, `jpackage` creates:

- Windows under `dist/jpackage/windows/staging/package/`:
  - `msi` or `exe` installer (when selected),
  - or app image folder plus archive `*-windows-x64-app-image.zip`
- Linux under `dist/jpackage/linux/staging/package/`:
  - app image folder plus archive `*-linux-x64-app-image.tar.gz`,
  - or distro-specific package file when selected and supported.

## Script map

- Portable bundle creators:
  - `packaging/package-portable-release.ps1`
  - `packaging/package-portable-release.sh`
- jpackage helpers:
  - `packaging/package-jpackage-windows.ps1`
  - `packaging/package-jpackage-linux.sh`
- One-command wrappers:
  - `packaging/build-release-windows.ps1`
  - `packaging/build-release-linux.sh`

## Troubleshooting

- If the Windows release fails before packaging, verify `CorrettoHome` points to a folder that contains `bin/java.exe`.
- If the Linux release fails before packaging, verify `CORRETTO_HOME` points to a folder that contains `bin/java`.
- If `jpackage` is missing, install/use a JDK 21 build that provides `jpackage` and ensure it is on `PATH`.
- If installer creation fails on Linux for a given package type, retry with `PACKAGE_TYPE=app-image`.
