[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [string]$PortableRoot,

    [string]$OutputDir = (Join-Path $PSScriptRoot '..\dist\jpackage\windows'),

    [string]$AppName = 'GLIMPSE-CE-ScenarioBuilder',

    [string]$AppVersion = '8.2',

    [ValidateSet('app-image', 'msi', 'exe')]
    [string]$PackageType = 'app-image'
)

$ErrorActionPreference = 'Stop'

$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$PortableRoot = (Resolve-Path $PortableRoot).Path
$jpackage = Get-Command jpackage -ErrorAction Stop

$runtimeImage = Join-Path $PortableRoot 'runtime\corretto-21\windows-x64'
if (-not (Test-Path (Join-Path $runtimeImage 'bin\java.exe'))) {
    throw "PortableRoot must contain the bundled Corretto 21 runtime at runtime\corretto-21\windows-x64: $PortableRoot"
}

$stagingRoot = Join-Path $OutputDir 'staging'
$inputRoot = Join-Path $stagingRoot 'input'
$packageRoot = Join-Path $stagingRoot 'package'
$iconPath = Join-Path $repoRoot 'resources\GLIMPSE_icon.ico'

if (Test-Path $stagingRoot) {
    Remove-Item $stagingRoot -Recurse -Force
}
New-Item -ItemType Directory -Path $inputRoot -Force | Out-Null
New-Item -ItemType Directory -Path $packageRoot -Force | Out-Null
New-Item -ItemType Directory -Path $OutputDir -Force | Out-Null

Copy-Item (Join-Path $PortableRoot 'GLIMPSE-ScenarioBuilder') (Join-Path $inputRoot 'GLIMPSE-ScenarioBuilder') -Recurse -Force
Copy-Item (Join-Path $PortableRoot 'GLIMPSE-ScenarioBuilder\GLIMPSE-ScenarioBuilder.jar') (Join-Path $inputRoot 'GLIMPSE-ScenarioBuilder.jar') -Force

if (Test-Path (Join-Path $PortableRoot 'GLIMPSE-ModelInterface')) {
    Copy-Item (Join-Path $PortableRoot 'GLIMPSE-ModelInterface') (Join-Path $inputRoot 'GLIMPSE-ModelInterface') -Recurse -Force
}

@(
    'options_GCAM-USA-8.2-windows.txt',
    'options_GCAM-global-8.2-windows.txt',
    'options_GCAM-USA-8.2-linux.txt',
    'options_GCAM-global-8.2-linux.txt',
    'model_interface.properties'
) | ForEach-Object {
    $source = Join-Path $PortableRoot $_
    if (Test-Path $source) {
        Copy-Item $source $inputRoot -Force
    }
}

$javaOptions = @(
    '-Dprism.order=sw',
    '--module-path', '$APPDIR/GLIMPSE-ScenarioBuilder/libs/javafx-21/win',
    '--add-modules=javafx.base,javafx.graphics,javafx.controls,javafx.fxml',
    '--add-exports=javafx.base/com.sun.javafx.runtime=ALL-UNNAMED',
    '--add-exports=javafx.graphics/com.sun.javafx.tk=ALL-UNNAMED'
)

$jpackageArgs = @(
    '--type', $PackageType,
    '--dest', $packageRoot,
    '--input', $inputRoot,
    '--name', $AppName,
    '--app-version', $AppVersion,
    '--main-jar', 'GLIMPSE-ScenarioBuilder.jar',
    '--main-class', 'org.eclipse.jdt.internal.jarinjarloader.JarRsrcLoader',
    '--runtime-image', $runtimeImage
)

if ($PackageType -in @('msi', 'exe')) {
    $jpackageArgs += @('--win-menu', '--win-shortcut')
}

if (Test-Path $iconPath) {
    $jpackageArgs += @('--icon', $iconPath)
}

foreach ($opt in $javaOptions) {
    $jpackageArgs += @('--java-options', $opt)
}

& $jpackage.Path @jpackageArgs
if ($LASTEXITCODE -ne 0) {
    throw "jpackage failed with exit code $LASTEXITCODE"
}

if ($PackageType -eq 'app-image') {
    $imageDir = Join-Path $packageRoot $AppName
    $zipPath = Join-Path $OutputDir "$AppName-windows-x64-app-image.zip"
    if (Test-Path $zipPath) { Remove-Item $zipPath -Force }
    Compress-Archive -Path (Join-Path $imageDir '*') -DestinationPath $zipPath -Force
    Write-Host "Created app image: $imageDir"
    Write-Host "Created archive: $zipPath"
} else {
    Get-ChildItem $packageRoot -File | ForEach-Object { Write-Host "Created output: $($_.FullName)" }
}
