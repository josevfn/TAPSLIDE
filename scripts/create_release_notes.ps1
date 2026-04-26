Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$version = if ($args.Count -gt 0) { $args[0] } else { "v1.0.0" }

@"
# Slide Remote $version

## Android

- Tela inicial limpa.
- Modo Wi-Fi / Hotspot com link do Companion.
- Modo demonstracao.
- Tela remota com timer.

## Companion

- Pacote Windows: SlideRemoteCompanion-Windows-$version.zip
"@

