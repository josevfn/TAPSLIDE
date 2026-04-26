# Desktop Companion

Companion Windows do Slide Remote.

## Recursos

- UI Swing simples.
- Servidor WebSocket em `ws://IP:8765/slide-remote`.
- Codigo de pareamento de 6 digitos com expiracao de 5 minutos.
- QR Code com host, porta e codigo.
- Sessao unica para o celular pareado.
- Simulacao de teclado com `java.awt.Robot`.

## Rodar

```powershell
.\gradlew.bat run
```

## Gerar executavel Windows

```powershell
.\gradlew.bat jpackageImage
```

O executavel fica dentro de:

```text
build/jpackage/SlideRemoteCompanion/SlideRemoteCompanion.exe
```

Para distribuir, use o script da raiz:

```powershell
..\scripts\package_windows.ps1
```
