# Slide Remote Protocol

Versão atual: `1`.

## Mensagem de comando

```json
{
  "type": "command",
  "version": 1,
  "sessionId": "uuid-da-sessao",
  "command": "NEXT_SLIDE",
  "timestamp": 1710000000000
}
```

## Mensagem de pareamento

```json
{
  "type": "pairing_request",
  "version": 1,
  "deviceName": "Celular de Jose",
  "pairingCode": "482913"
}
```

## Resposta positiva

```json
{
  "type": "pairing_accepted",
  "version": 1,
  "sessionId": "uuid-da-sessao",
  "computerName": "Notebook-Jose"
}
```

## Resposta negativa

```json
{
  "type": "pairing_rejected",
  "version": 1,
  "reason": "INVALID_CODE"
}
```

## Heartbeat

```json
{
  "type": "heartbeat",
  "version": 1,
  "sessionId": "uuid-da-sessao",
  "timestamp": 1710000000000
}
```

## Comandos

```text
NEXT_SLIDE
PREVIOUS_SLIDE
START_PRESENTATION
END_PRESENTATION
BLACK_SCREEN
WHITE_SCREEN
HEARTBEAT
PING
PONG
```

## QR Code do Companion

```json
{
  "app": "slide-remote",
  "version": 1,
  "mode": "wifi",
  "host": "192.168.0.10",
  "port": 8765,
  "pairingCode": "482913"
}
```

