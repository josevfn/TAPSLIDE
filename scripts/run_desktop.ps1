Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

Push-Location "$PSScriptRoot\..\desktop-companion"
try {
    .\gradlew.bat run
}
finally {
    Pop-Location
}
