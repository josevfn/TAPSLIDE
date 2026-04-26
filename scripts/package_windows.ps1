Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$version = "1.0.0"
$root = Resolve-Path "$PSScriptRoot\.."
$dist = Join-Path $root "dist"
$package = Join-Path $dist "SlideRemoteCompanion-Windows-v$version.zip"
$companion = Join-Path $root "desktop-companion"
$image = Join-Path $companion "build\jpackage\SlideRemoteCompanion"

New-Item -ItemType Directory -Force -Path $dist | Out-Null

Push-Location $companion
try {
    .\gradlew.bat jpackageImage
}
finally {
    Pop-Location
}

if (Test-Path $package) {
    Remove-Item -LiteralPath $package -Force
}

Compress-Archive -Path (Join-Path $image "*") -DestinationPath $package

Write-Host "Pacote gerado: $package"
