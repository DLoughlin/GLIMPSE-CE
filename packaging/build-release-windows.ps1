[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [string]$CorrettoHome,

    [ValidateSet('app-image', 'msi', 'exe')]
    [string]$PackageType = 'msi',

    [string]$BundleName = 'GLIMPSE-CE-portable',

    [string]$OutputDir = (Join-Path $PSScriptRoot '..\dist'),

    [string]$AppName = 'GLIMPSE-CE-ScenarioBuilder',

    [string]$AppVersion = '8.2'
)

$ErrorActionPreference = 'Stop'

$portableScript = Join-Path $PSScriptRoot 'package-portable-release.ps1'
$jpackageScript = Join-Path $PSScriptRoot 'package-jpackage-windows.ps1'

if (-not (Test-Path $portableScript)) {
    throw "Missing helper script: $portableScript"
}
if (-not (Test-Path $jpackageScript)) {
    throw "Missing helper script: $jpackageScript"
}

Write-Host "[1/2] Staging portable bundle with Corretto 21..."
& $portableScript -CorrettoHome $CorrettoHome -OutputDir $OutputDir -BundleName $BundleName
if ($LASTEXITCODE -ne 0) {
    throw "Portable bundle staging failed with exit code $LASTEXITCODE"
}

$portableRoot = Join-Path $OutputDir $BundleName
Write-Host "[2/2] Building Windows package via jpackage ($PackageType)..."
& $jpackageScript -PortableRoot $portableRoot -OutputDir (Join-Path $OutputDir 'jpackage\windows') -PackageType $PackageType -AppName $AppName -AppVersion $AppVersion
if ($LASTEXITCODE -ne 0) {
    throw "jpackage step failed with exit code $LASTEXITCODE"
}

Write-Host "Release build completed successfully."
