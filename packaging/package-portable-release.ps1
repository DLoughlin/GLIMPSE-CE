[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [string]$CorrettoHome,

    [string]$OutputDir = (Join-Path $PSScriptRoot '..\dist'),

    [string]$BundleName = 'GLIMPSE-CE-portable'
)

$ErrorActionPreference = 'Stop'

$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$CorrettoHome = (Resolve-Path $CorrettoHome).Path

if (-not (Test-Path (Join-Path $CorrettoHome 'bin\java.exe'))) {
    throw "CorrettoHome must point to a Corretto 21 Windows runtime or JDK that contains bin\java.exe: $CorrettoHome"
}

$stageRoot = Join-Path $OutputDir $BundleName
$windowsRuntimeRoot = Join-Path $stageRoot 'runtime\corretto-21\windows-x64'

$requiredItems = @(
    'GLIMPSE-ScenarioBuilder',
    'GLIMPSE-ModelInterface',
    'run_GLIMPSE_GCAM-USA-8.2-windows.bat',
    'run_GLIMPSE_GCAM-global-8.2-windows.bat',
    'run_GLIMPSE_GCAM-USA-8.2-linux.sh',
    'run_GLIMPSE_GCAM-global-8.2-linux.sh',
    'options_GCAM-USA-8.2-windows.txt',
    'options_GCAM-global-8.2-windows.txt',
    'options_GCAM-USA-8.2-linux.txt',
    'options_GCAM-global-8.2-linux.txt'
)

if (Test-Path $stageRoot) {
    Remove-Item $stageRoot -Recurse -Force
}
New-Item -ItemType Directory -Path $stageRoot -Force | Out-Null

foreach ($item in $requiredItems) {
    $source = Join-Path $repoRoot $item
    if (-not (Test-Path $source)) {
        throw "Missing required release input: $source"
    }

    Copy-Item $source -Destination $stageRoot -Recurse -Force
}

New-Item -ItemType Directory -Path $windowsRuntimeRoot -Force | Out-Null
Copy-Item (Join-Path $CorrettoHome '*') -Destination $windowsRuntimeRoot -Recurse -Force

$zipPath = Join-Path $OutputDir "$BundleName-windows-x64.zip"
if (Test-Path $zipPath) {
    Remove-Item $zipPath -Force
}
New-Item -ItemType Directory -Path $OutputDir -Force | Out-Null
Compress-Archive -Path (Join-Path $stageRoot '*') -DestinationPath $zipPath -Force

Write-Host "Created portable release: $zipPath"
Write-Host "Bundle root: $stageRoot"
Write-Host "Bundled runtime expected by launchers: runtime\corretto-21\windows-x64"
