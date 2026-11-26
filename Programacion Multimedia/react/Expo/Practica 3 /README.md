# Estudi Multimèdia

Práctica final de la UT3 (RA3, integración de contenidos multimedia) del módulo 0489, Programación Multimedia y Dispositivos Móviles, de 2.º de DAM.

Es una app con React Native y Expo SDK 58 (JavaScript) que junta en un mismo menú imágenes, procesado de imágenes, notas de voz, vídeo y animaciones.

| Pantalla | Funcionalidad | Fichero |
|---|---|---|
| F1 · Imágenes | Galería y cámara, permisos, ficha de datos (medidas, peso, formato, EXIF), copias en la carpeta de la app | [src/app/imagenes.jsx](src/app/imagenes.jsx) |
| F2 · Procesado | Recortar (cuadrado / 16:9), redimensionar y convertir a JPEG, PNG y WebP, con una tabla que compara medidas y pesos | [src/app/procesado.jsx](src/app/procesado.jsx) |
| F3 · Notas de voz | Grabar (con pausa), guardar las notas en la app, listarlas, reproducirlas y borrarlas | [src/app/audio.jsx](src/app/audio.jsx) |
| F4 · Vídeo | Vídeo local, remoto y HLS con controles propios, registro de eventos y gestión de errores | [src/app/video.jsx](src/app/video.jsx) |
| F5 · Animaciones | Dos animaciones con `Animated`: un viaje con final y un ecualizador en bucle, ambas con interpolación y limpieza | [src/app/animaciones.jsx](src/app/animaciones.jsx) |
| F6 · Calidad | Este README | — |

---

## 1. Entorno y versiones

| Elemento | Versión |
|---|---|
| Expo SDK | 58 (`expo` ^58.0.26, CLI 58.1.21) |
| React Native | 0.88.1 |
| React | 19.3.0 |
| Node.js | 24.22.3 |
| Sistema de desarrollo | Linux (CachyOS) |

Librerías del proyecto:

| Librería | Versión | Para qué se usa |
|---|---|---|
| `expo-router` | ~58.0.32 | Navegación por ficheros (`src/app`) con un `Stack` |
| `expo-image-picker` | ~58.0.10 | Galería, cámara y sus permisos (F1, F2) |
| `expo-image-manipulator` | ~58.0.16 | Recortar, redimensionar y guardar en JPEG, PNG y WebP (F2) |
| `expo-file-system` | ~58.0.14 | Copiar, mover, medir y borrar ficheros (API nueva de `File`, `Directory` y `Paths`) |
| `expo-audio` | ~58.0.12 | Grabadora y reproductor de las notas de voz (F3) |
| `expo-video` | ~58.0.14 | Reproductor de vídeo MP4 y HLS (F4) |
| `expo-asset` | ~58.0.21 | Incluir el vídeo local en el paquete de la app |
| `expo-status-bar`, `expo-constants`, `expo-linking` | ~58.0.x | Barra de estado, configuración y abrir los ajustes del sistema |
| `react-native-safe-area-context`, `react-native-screens` | 5.11 / 4.30 | Dependencias de la navegación |
| `react-native-web`, `react-dom`, `@expo/metro-runtime` | 0.21 / 19.3 / 58.0 | Probar la interfaz en el navegador |

Las animaciones usan la API `Animated` de React Native, así que no hace falta ninguna librería extra.

### Expo Go o development build

- **Expo Go** es la app de Expo que ya trae compilados los módulos nativos del SDK. Se escanea el QR y la app arranca sin compilar nada. Todas las librerías de esta práctica vienen dentro de Expo Go, así que se puede probar entera con él.
- Un **development build** es una versión propia de la app (`npx expo run:android` o con EAS Build) compilada con nuestro `app.json`. Es la que hace falta cuando se añade código nativo que Expo Go no incluye, o cuando se quiere ver la app como quedará de verdad.

En esta práctica la diferencia más visible está en los **permisos**: los textos que se explican en `app.json` (plugins de `expo-image-picker` y `expo-audio`) solo salen en un development build. En Expo Go se ven los textos genéricos de Expo Go, porque los permisos son de esa app y no de la nuestra.

Entorno elegido para cada tipo de prueba:

| Qué se prueba | Dónde | Por qué |
|---|---|---|
| Interfaz, navegación, animaciones, procesado | Emulador Android + Expo Go | Es rápido y se recarga al guardar |
| Cámara y micrófono | Móvil físico + Expo Go | El emulador tiene una cámara virtual y el micrófono depende del PC |
| Permisos denegados para siempre | Móvil físico | Para ver el caso «no volver a preguntar» y el botón «Abrir ajustes» |
| Textos propios de los permisos | Development build | Expo Go no usa los textos de `app.json` |
| HLS y vídeo remoto | Móvil físico con wifi y con datos | Para ver el búfer y cómo se adapta la calidad |
| Interfaz en navegador | `npm run web` | Solo para revisar la maquetación (ver limitaciones) |

---

## 2. Instrucciones de ejecución

```bash
npm install
npx expo start
```

Luego:

- **Móvil físico:** abre Expo Go y escanea el QR (el móvil y el PC tienen que estar en la misma red; si no, usa `npx expo start --tunnel`).
- **Emulador Android:** con el emulador abierto, pulsa `a` en la terminal de Expo.
- **Navegador:** pulsa `w` o lanza `npm run web`.
- **Development build (Android):** `npx expo run:android` (hace falta Android Studio y el SDK de Android).

Para comprobar que el proyecto compila sin abrir nada: `npx expo export --platform android`.

---

## 3. Permisos y justificación

| Permiso | Dónde se pide | Por qué hace falta | Si se deniega |
|---|---|---|---|
| Fotos / galería | F1 y F2, al pulsar «Elegir de la galería» | Para leer la imagen que el usuario elige | Mensaje y botón «Reintentar». Si se ha denegado para siempre, botón «Abrir ajustes» |
| Cámara | F1, al pulsar «Hacer una foto» | Para hacer la foto que luego se procesa | Igual que la galería |
| Micrófono | F3, al pulsar «Grabar nota» | Para grabar las notas de voz | Igual, con el texto adaptado al micrófono |

Cada permiso se pide justo cuando hace falta, nunca al abrir la app, y solo el que corresponde a lo que se ha pulsado. La respuesta `canAskAgain` decide si se ofrece «Reintentar» (se puede volver a preguntar) o «Abrir ajustes» (el usuario marcó que no se le pregunte más).

No se piden permisos de almacenamiento: las copias de imágenes, los resultados del procesado y las notas de voz se guardan en las carpetas propias de la app (`Paths.document` y `Paths.cache`), y para eso no hace falta ningún permiso.

---

## 4. Gestión de errores

Todos los errores salen en el componente común [AvisoError](src/components/AvisoError.jsx): un título, un mensaje comprensible y, cuando tiene sentido, un botón para reintentar o para abrir los ajustes.

| Tipo de error | Ejemplo en la app | Qué hace |
|---|---|---|
| Permiso denegado | Galería, cámara o micrófono | Explica qué no se puede hacer y ofrece «Reintentar» o «Abrir ajustes» |
| Recurso inexistente | Una nota de voz cuyo fichero se ha borrado; la imagen temporal que el sistema ha limpiado; un vídeo que da 404 | La nota sale como «Fichero no encontrado» con el botón «Quitar de la lista»; la imagen pide elegirla otra vez; el vídeo ofrece «Reintentar» |
| Formato incompatible | GIF o SVG en F1; una página HTML en el reproductor de vídeo; un formato que el móvil no sabe codificar en F2 | Avisa del tipo recibido; en F2 la fila de ese formato dice «No compatible en este dispositivo» y los otros formatos se generan igual |
| Operación fallida | No se abre la cámara (emulador sin cámara), falla la grabación, falla el procesado | Mensaje con el detalle y «Reintentar» |
| Datos no válidos | Ancho de redimensión vacío, menor de 16 px o mayor que la imagen | «Revisa los datos» con el motivo, antes de procesar nada |

En la pantalla de vídeo hay dos fuentes rotas a propósito, **No existe** y **Incompatible**, para provocar un error en la demostración.

---

## 5. Ficheros, caché y memoria

- **F1:** la imagen elegida está en la caché del picker, que el sistema puede borrar. «Guardar copia en la app» la copia a `documentos/imagenes/`. La tarjeta «Ficheros de la app» enseña cuántas copias hay y cuánto ocupan, y permite borrarlas.
- **F2:** los resultados se mueven a `cache/procesadas/` para poder medirlos y vaciarlos con «Vaciar caché». Después de procesar se liberan los objetos nativos (`release()`) del contexto y de la imagen renderizada, porque ocupan memoria fuera de JavaScript.
- **F3:** cada nota se mueve de la caché a `documentos/notas/` y la lista se guarda en `notas.json`, así sigue ahí al cerrar la app. Al cargar la lista se comprueba que cada fichero existe.
- **F4:** el reproductor lo crea y lo libera `useVideoPlayer`; al salir de la pantalla se pausa con `useFocusEffect`.
- **F5:** al salir de la pantalla se paran las animaciones, se quitan las escuchas (`removeListener`) y se cancela el temporizador del contador de vueltas.

---

## 6. Tabla de pruebas

Leyenda: ✅ correcto · ❌ falla · ➖ no aplica.

Dispositivo de pruebas: Xiaomi POCO F7 Pro con Expo Go (_versión de Android_). Los resultados del dispositivo salen del vídeo de demostración del 26/11/2026. Emulador: _(nombre del AVD y versión de Android)_.

| # | Caso | Resultado esperado | Emulador | Dispositivo | Resultado |
|---|---|---|---|---|---|
| 1 | Elegir una foto JPEG de la galería | Sale la ficha con medidas, peso, formato y EXIF | | POCO F7 Pro | ✅ 3072 × 4096 px, 2,91 MB, `image/jpeg`, fecha y cámara del EXIF |
| 2 | Hacer una foto con la cámara | Sale la ficha con origen «Cámara» | | | |
| 3 | Denegar el permiso de galería | Aviso con «Reintentar»; al reintentar vuelve a preguntar | | | |
| 4 | Denegar la cámara para siempre | Aviso con «Abrir ajustes», que abre los ajustes de la app | | | |
| 5 | Elegir un GIF | «Formato no compatible» con el tipo `image/gif` | | | |
| 6 | Cámara en un emulador sin cámara | «No se ha podido abrir la cámara» con «Reintentar» | | ➖ | |
| 7 | Guardar copia y luego borrar copias | El contador y el espacio ocupado suben y vuelven a 0 | | | |
| 8 | Procesar sin recorte a 800 px | Tres filas (JPEG, PNG, WebP) con ✓, peso y % frente al original | | | |
| 9 | Recorte cuadrado + 500 px | Las tres salen a 500 × 500 con ✓ | | POCO F7 Pro | ✅ Probado con 200 px: las tres a 200 × 200 ✓. JPEG 11,5 KB, PNG 58,2 KB, WebP 5,7 KB (★) |
| 10 | Recorte 16:9 + calidad 50 % | Medidas 16:9; JPEG y WebP pesan menos que con el 80 % | | | |
| 11 | Ancho vacío, «abc» o 10 | «Revisa los datos» con el motivo | | | |
| 12 | Ancho mayor que la imagen | Aviso de que no se amplían imágenes | | | |
| 13 | Vaciar la caché de resultados | Los ficheros pasan a 0 y desaparece la comparativa | | POCO F7 Pro | ✅ De 3 ficheros (75,4 KB) a 0 |
| 14 | Grabar una nota, pausar, reanudar y guardar | El cronómetro se para en la pausa; la nota sale en la lista con su duración y peso | | POCO F7 Pro | ✅ Nota de 0:06 y 96,1 KB en la lista (la pausa no se probó en el vídeo) |
| 15 | Grabar menos de medio segundo | «Nota demasiado corta» | | | |
| 16 | Reproducir, pausar y parar una nota | El estado y la barra de progreso cambian; al acabar vuelve al principio | | POCO F7 Pro | ✅ «Reproduciendo» y «En pausa» con la barra avanzando |
| 17 | Cerrar la app y volver a abrirla | Las notas siguen en la lista | | | |
| 18 | Denegar el micrófono | Aviso con «Reintentar» o «Abrir ajustes» | | | |
| 19 | Borrar una nota | Desaparece de la lista y del disco | | | |
| 20 | Vídeo local | Se reproduce sin internet; funcionan play, ±10 s, saltar con la barra, silencio y velocidades | | POCO F7 Pro | ❌ → corregido: al llegar al final el estado pasaba a `idle` y los controles se quedaban bloqueados. Ahora sale «Terminado» y Play/Reiniciar lo vuelven a reproducir. Falta repetir la prueba |
| 21 | Vídeo remoto MP4 | Se ve el búfer en la barra y los eventos `statusChange` y `sourceLoad` | | POCO F7 Pro | ✅ Carga, `sourceLoad` (0:05), y funcionan pausa y las velocidades 1,5× y 2× |
| 22 | HLS (Mux y Apple) | Se reproduce el stream `.m3u8` | | | |
| 23 | Fuente «No existe» | «No se puede reproducir» (404) con «Reintentar» | | | |
| 24 | Fuente «Incompatible» | Aviso de formato no compatible | | | |
| 25 | Vídeo remoto sin conexión (modo avión) | Aviso de comprobar la conexión; al volver la red, «Reintentar» funciona | | | |
| 26 | Salir de Vídeo mientras se reproduce | El sonido se para | | | |
| 27 | Animación Viaje: iniciar hasta el final | Llega al 100 % y el estado pasa a «Terminada» | | POCO F7 Pro | ✅ |
| 28 | Animación Viaje: parar a mitad | Estado «Interrumpida» con el porcentaje en el que se ha parado | | | |
| 29 | Ecualizador: iniciar y parar | Las barras se mueven en bucle, cuenta vueltas y al parar vuelven a reposo | | | |
| 30 | Salir de Animaciones con el ecualizador en marcha | No queda nada en marcha (limpieza) | | | |
| 31 | Compilar el proyecto (`npx expo export` para Android y web) | Se generan los dos paquetes sin errores | ➖ | ➖ | ✅ (26/11/2026) |

---

## 7. Limitaciones conocidas

- **Web:** el navegador no tiene sistema de ficheros de la app, así que en web no hay copias guardadas, caché medible ni notas persistentes: las notas solo duran mientras la pestaña está abierta. El tamaño de los ficheros procesados tampoco se puede medir. La cámara y el micrófono dependen del navegador.
- **HLS en web:** depende de que el navegador sepa reproducir HLS de forma nativa. En Android e iOS funciona con el reproductor del sistema.
- **Textos de permisos:** en Expo Go no salen los textos propios de `app.json`, solo en un development build.
- **Formatos según el dispositivo:** si un móvil no sabe codificar un formato (por ejemplo WebP en algún iOS antiguo), esa fila sale como «No compatible» y los demás formatos se generan igual. Las fotos HEIC de iPhone se leen, pero la ficha depende de lo que dé el sistema.
- **Recorte:** el recorte siempre está centrado; no se puede mover el área a mano. Tampoco se amplían imágenes (el ancho final no puede ser mayor que el original).
- **Notas de voz:** se graban en M4A/AAC con el preset `HIGH_QUALITY`; no se puede cambiar el formato desde la app. No se puede reproducir mientras se graba.
- **Vídeo remoto y HLS:** necesitan internet y dependen de que los servidores de prueba de MDN, Mux y Apple sigan activos.
- **Contador de vueltas del ecualizador:** se calcula con un temporizador según lo que dura una vuelta, así que es aproximado si el móvil va lento.

---

## 8. Fuentes y licencias de los recursos

| Recurso | Origen | Licencia |
|---|---|---|
| `assets/video/local.mp4` (12 s, 640 × 360, H.264 + AAC) | Generado con FFmpeg (carta de ajuste `testsrc2`, texto y tono de prueba) | Creación propia, sin restricciones |
| Vídeo remoto `flower.mp4` | MDN Web Docs, `interactive-examples.mdn.mozilla.net/media/cc0-videos/` | CC0 (dominio público) |
| HLS `x36xhzz.m3u8` | Streams de prueba de Mux, `test-streams.mux.dev` | Stream público de prueba; el contenido es *Big Buck Bunny* (© Blender Foundation, CC BY 3.0) |
| HLS `bipbop` | Ejemplos de streaming de Apple, `devstreaming-cdn.apple.com` | Stream de ejemplo publicado por Apple para pruebas |
| Fuente «Incompatible» | `example.com` (IANA) | Dominio reservado para ejemplos |
| Iconos y símbolos (★, ▶, ❚❚, ⚠) | Caracteres Unicode | — |

Las imágenes y las notas de voz las crea el usuario con su móvil; la app no incluye ninguna.
