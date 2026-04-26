Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

Push-Location "$PSScriptRoot\..\android-app"
try {
    .\gradlew.bat assembleDebug
}
finally {
    Pop-Location
}

