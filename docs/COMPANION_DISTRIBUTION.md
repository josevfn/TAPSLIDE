# Distribuição do Companion

O Companion desktop será distribuído por GitHub Releases.

URL usada pelo app Android:

```text
https://github.com/josevfn/TAPSLIDE/releases/latest/download/SlideRemoteCompanion-Windows-v1.0.0.zip
```

No MVP, o formato recomendado é `.zip`, pois tende a reduzir bloqueios de navegador e simplifica a distribuição.

O `.zip` contem um app image gerado por `jpackage`, incluindo:

```text
SlideRemoteCompanion.exe
app/
runtime/
```
