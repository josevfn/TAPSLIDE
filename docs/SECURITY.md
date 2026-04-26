# Segurança

O MVP usa segurança básica para rede local/hotspot:

- Código de pareamento de 6 dígitos.
- Expiração do código em 5 minutos.
- Criação de `sessionId` após pareamento aceito.
- Rejeição de comandos sem sessão válida.
- Apenas um celular conectado no MVP.

O sistema foi pensado para redes locais confiáveis. Em versões futuras, o protocolo pode adicionar criptografia de sessão.

