# Distribuição do Companion via GitHub Releases

O Companion desktop será publicado em GitHub Releases.

URL usada pelo app Android:

```text
https://github.com/josevfn/TAPSLIDE/releases/latest
```

## Como publicar uma versão

1. Gerar build do Companion.
2. Compactar como `SlideRemoteCompanion-Windows-v1.0.0.zip`.
3. Criar tag `v1.0.0`.
4. Criar release no GitHub.
5. Anexar o arquivo `.zip`.
6. Atualizar notas da versão.

## Convenção de nomes

```text
SlideRemoteCompanion-Windows-vMAJOR.MINOR.PATCH.zip
```

Exemplo:

```text
SlideRemoteCompanion-Windows-v1.0.0.zip
```

## Futuro

Quando houver site próprio, o app poderá apontar para:

```text
https://slideremote.app/download
```

Essa página poderá redirecionar para a última release do GitHub.
