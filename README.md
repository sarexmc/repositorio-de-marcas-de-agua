# QuitaMarca

Mod para Minecraft **1.20.1 (Forge)** que oculta imágenes (PNG) de la pantalla, por ejemplo marcas de agua que dibuja otro mod. Hay dos formas de usarlo:

1. **Automática:** cualquier imagen de 2000 px o más (ancho o alto, en píxeles reales) deja de dibujarse. Se cambia con `LIMITE_PX` en `GuiGraphicsMixin`.
2. **Con comandos:** se detecta qué texturas se están dibujando y se ponen en "false" para todo el servidor.

## Comandos

| Comando | Dónde | Qué hace |
|---|---|---|
| `/marcaescanear [segundos]` | Cliente | Lista las texturas dibujadas en pantalla (por defecto los últimos 5 s) con su tamaño. Cada línea tiene un botón clicable para ocultarla. No lista las texturas de `minecraft`. |
| `/marca ocultar <textura>` | Servidor (OP nivel 2) | Oculta la textura para todos los jugadores. Se guarda en el mundo. |
| `/marca mostrar <textura>` | Servidor (OP) | Vuelve a mostrarla. |
| `/marca lista` | Servidor (OP) | Muestra las texturas ocultas. |
| `/marca limpiar` | Servidor (OP) | Vuelve a mostrar todas. |

Ejemplo de flujo: abre la pantalla con la marca de agua, escribe `/marcaescanear`, y pulsa **[ocultar para todos]** en la línea de la textura.

## Instalación

Pon el `.jar` en `mods` del **servidor y de los clientes**. Un cliente sin el mod puede entrar, pero seguirá viendo las texturas.

## Compilar

Requiere Java 17.

```bash
gradle wrapper      # solo la primera vez (o copia el wrapper de un MDK de Forge 1.20.1)
./gradlew build
```

El `.jar` queda en `build/libs/`.

## Limitaciones

- Solo afecta a imágenes dibujadas con `blit`. No toca textos, rectángulos de color (`fill`) ni mods que dibujen con su propio render o shaders.
- Las texturas del namespace `minecraft` no se pueden ocultar con comandos (rompería el HUD).
- Con ventanas pequeñas, fondos grandes (como el del menú principal) pueden superar el límite automático.

## Licencia

MIT
