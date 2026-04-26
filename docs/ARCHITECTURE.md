# Arquitetura

O monorepo separa o app Android, o Companion desktop e o protocolo compartilhado.

## Android

- `feature/`: telas e ViewModels por fluxo.
- `core/model`: modelos de domínio.
- `core/protocol`: mensagens JSON versionadas.
- `core/transport`: abstração de transporte para demo, Wi-Fi e Bluetooth.
- `core/wakelock`: controle de tela ligada com `FLAG_KEEP_SCREEN_ON`.
- `ui/`: tema e componentes reutilizáveis.

As telas Compose não devem conter lógica de rede. Elas disparam ações para ViewModels, que usam transportes/repositórios.

## Companion

Será implementado em Kotlin/JVM. O papel dele será expor um servidor WebSocket local, validar pareamento e simular teclas com `java.awt.Robot`.

## Protocolo

O contrato entre Android e Companion fica em `shared-protocol/protocol.md`.

