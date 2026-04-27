# Slide Remote

Transforme seu celular Android em um passador de slides.

## Modos de uso

### Bluetooth direto

Funciona sem instalar nada no computador quando o aparelho Android oferece suporte ao modo Bluetooth HID/presenter.

### Wi-Fi / Hotspot com Companion

Modo mais estável para apresentações. Requer abrir o Slide Remote Companion no computador.

O Companion pode ser baixado diretamente em:

```text
https://github.com/josevfn/TAPSLIDE/releases/latest/download/SlideRemoteCompanion-Windows-v1.0.2.zip
```

### Modo demonstração

Permite testar a interface de controle remoto sem conectar a um computador.

## Estrutura

```text
slide-remote/
├─ android-app/
├─ desktop-companion/
├─ shared-protocol/
├─ docs/
└─ scripts/
```

## Rodar Android

Linux/macOS:

```bash
cd android-app
./gradlew assembleDebug
```

Windows PowerShell:

```powershell
cd android-app
.\gradlew.bat assembleDebug
```

## Rodar Companion

```bash
cd desktop-companion
./gradlew run
```

Windows PowerShell:

```powershell
cd desktop-companion
.\gradlew.bat run
```

## Publicar Companion no GitHub Releases

O arquivo recomendado para o MVP é:

```text
SlideRemoteCompanion-Windows-v1.0.2.zip
```

Fluxo resumido:

1. Gerar build do Companion.
2. Compactar o pacote Windows.
3. Criar a tag `v1.0.2`.
4. Criar uma release no GitHub.
5. Anexar o arquivo `.zip`.

Detalhes em `docs/GITHUB_RELEASES.md`.

## Limitações conhecidas

- A Fase 1 entrega a experiência Android base, modo demonstração, tela do Companion e estrutura do protocolo.
- A conexão WebSocket real, QR Code funcional e simulação de teclado no Companion entram nas próximas fases.
- Bluetooth direto depende de suporte HID do aparelho Android e pode não estar disponível em muitos dispositivos.

## Roadmap

1. Finalizar Companion desktop com WebSocket e QR Code.
2. Integrar Android ao Companion via pareamento e sessão.
3. Adicionar heartbeat e reconexão.
4. Empacotar o Companion para Windows.
5. Avaliar suporte Bluetooth direto por dispositivo.
