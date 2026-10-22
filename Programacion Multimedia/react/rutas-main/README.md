# Rutas con React Native y Expo Router

Un ejemplo pequeño para practicar el recorrido **lista → detalle → volver**.
No utiliza servidor, base de datos ni peticiones: los lugares están guardados en un array.

## 1. Arrancar el proyecto

Necesitas Node.js **22.13 o superior dentro de la versión 22**, o una versión LTS compatible como Node 24. Este proyecto utiliza Expo SDK 58.

Desde la carpeta del proyecto:

```bash
npm install
npm start
```

Escanea el QR con una versión de Expo Go compatible con SDK 58. El móvil y el ordenador deben estar en la misma red. En iPhone puedes escanearlo con la cámara; en Android, desde Expo Go.

Para probarlo en el navegador:

```bash
npm run web
```

Si Expo Go indica que el SDK no es compatible, actualiza Expo Go o utiliza el navegador para este ejercicio.

## 2. Entender las carpetas

```text
src/
  app/
    _layout.jsx       Configura la navegación
    index.jsx         Home: lista de lugares
    detall/
      [id].jsx        Detalle de un lugar
  data/
    llocs.js          Array con los lugares
```

Expo Router crea las rutas a partir de los archivos de `src/app/`.
También admite una carpeta `app/` en la raíz: es la estructura del enunciado. Aquí la colocamos dentro de `src/` para separar el código de la configuración. **`src` y `app` no aparecen en la URL.** No crees ambas carpetas de rutas a la vez.

| Archivo | Ruta | Para qué sirve |
| --- | --- | --- |
| `src/app/index.jsx` | `/` | Mostrar la lista |
| `src/app/detall/[id].jsx` | `/detall/1`, `/detall/2`, etc. | Mostrar un lugar según su `id` |
| `src/app/_layout.jsx` | No es una pantalla | Configurar el navegador y las cabeceras |

Los datos están fuera de `app/` porque **no son pantallas**.

## 3. Preparar los datos

En `src/data/llocs.js` cada lugar tiene este formato:

```js
{
  id: '3',
  nombre: 'Platja de la Barceloneta',
  categoria: 'Mar',
  descripcion: 'Una playa junto al barrio de la Barceloneta.',
}
```

Cada `id` debe ser único. Usamos cadenas (`'3'`, no `3`) porque los parámetros de la URL se leen como texto.

## 4. Configurar la navegación

`src/app/_layout.jsx` utiliza `Stack` de `expo-router`:

```jsx
import { Stack } from 'expo-router';

export default function Layout() {
  return (
    <Stack>
      <Stack.Screen name="index" options={{ title: 'Lugares de Barcelona' }} />
      <Stack.Screen name="detall/[id]" options={{ title: 'Detalle' }} />
    </Stack>
  );
}
```

Un `Stack` funciona como una pila: abres el detalle encima de la lista y, al volver, lo retiras para mostrar la pantalla anterior. Los `Stack.Screen` de este archivo configuran las pantallas; no hace falta importarlas una a una.

El archivo real también aplica colores. En `package.json`, `"main": "expo-router/entry"` activa Expo Router como punto de entrada. Por eso no necesitamos `App.js`.

## 5. Mostrar la lista y abrir un detalle

`src/app/index.jsx` usa `FlatList` para dibujar una tarjeta por lugar. `keyExtractor` identifica cada elemento con su `id`.

Dentro de cada tarjeta está el enlace:

```jsx
import { Link } from 'expo-router';
import { Pressable, Text } from 'react-native';

// Este fragmento va dentro de renderItem, donde existe la variable lugar.
<Link
  href={{
    pathname: '/detall/[id]',
    params: { id: lugar.id },
  }}
  asChild
>
  <Pressable>
    <Text>Ver lugar</Text>
  </Pressable>
</Link>
```

- `pathname` indica la ruta dinámica que queremos abrir.
- `params` aporta el valor de `id` para esa tarjeta.
- `asChild` hace que `Link` utilice el `Pressable` como elemento interactivo, en lugar de crear su propio texto.
- `Pressable` permite dar al enlace el aspecto de un botón.

Si pulsas la tarjeta con `id: '3'`, navegas a `/detall/3`.
Para un enlace de texto sencillo, el ejemplo del enunciado también funciona:

```jsx
<Link href={{ pathname: '/detall/[id]', params: { id: '3' } }}>
  <Text>Veure lloc</Text>
</Link>
```

## 6. Recibir el identificador

En `src/app/detall/[id].jsx`:

```jsx
import { useLocalSearchParams } from 'expo-router';
import { llocs } from '../../data/llocs';

// Dentro del componente Detall:
const { id } = useLocalSearchParams();
const lugar = llocs.find((lugar) => lugar.id === id);
```

`useLocalSearchParams()` lee los parámetros de esta pantalla.
`find()` busca el lugar cuyo identificador coincide con el recibido.

**Los corchetes de `[id].jsx` significan que ese trozo de la ruta cambia.** Un solo archivo sirve para todos los lugares. No hay que crear `1.jsx`, `2.jsx` y `3.jsx`.

Si el lugar no existe, la pantalla muestra «Lugar no encontrado» y conserva el botón para volver. Así evitamos intentar leer `nombre` de un valor inexistente.

## 7. Añadir el botón Volver

El detalle importa `router` de `expo-router` y utiliza esta función:

```jsx
function volver() {
  if (router.canGoBack()) {
    router.back();
  } else {
    router.replace('/');
  }
}
```

El `Pressable` llama a la función con `onPress={volver}`.

- `router.back()` vuelve a la pantalla anterior.
- `router.canGoBack()` comprueba si hay una pantalla anterior.
- `router.replace('/')` va a la home si abrimos el detalle directamente y no hay historial. Reemplaza el detalle para no dejarlo detrás de la home.

En la navegación normal desde la lista, también aparece el botón de retroceso de la cabecera.

## 8. Extensión: nombre en la cabecera

En el JSX del detalle:

```jsx
<Stack.Screen
  options={{ title: lugar ? lugar.nombre : 'Lugar no encontrado' }}
/>
```

Este `Stack.Screen` **configura la cabecera de la pantalla actual**; no crea otra pantalla. El título se calcula con los datos del lugar, así que no necesitamos enviar el nombre por la URL.

## 9. Los componentes de interfaz

- `Text` muestra texto; en React Native no escribimos texto suelto dentro de `View`.
- `View` agrupa los elementos de una tarjeta.
- `StyleSheet.create()` reúne los estilos al final de cada pantalla.
- `SafeAreaView`, de `react-native-safe-area-context`, protege el contenido en los bordes del dispositivo. No añadimos el borde superior porque lo gestiona la cabecera.
- `ScrollView` permite desplazar el detalle en pantallas pequeñas o con texto grande.

Los estilos no controlan las rutas. Para entender la navegación, céntrate primero en `Stack`, `Link`, `useLocalSearchParams` y `router.back()`.

## 10. Práctica para los alumnos

1. Arranca la aplicación y abre los tres lugares.
2. Comprueba que cambian el nombre, la descripción y la cabecera.
3. Pulsa «Volver» y verifica que regresas a la lista.
4. Añade un cuarto objeto a `src/data/llocs.js`, con un `id` nuevo.
5. Comprueba que aparece su tarjeta y funciona su detalle sin crear otra pantalla.
6. En el navegador, abre `/detall/999` y comprueba el mensaje de lugar inexistente.
7. Abre `/detall/3` en una pestaña nueva y prueba «Volver». Si no hay historial de navegación de la app, debe mostrar la home.

Pregunta final: **¿por qué un solo archivo `[id].jsx` puede mostrar cuatro lugares distintos?**

## Comprobar el código

```bash
npm run lint
npm run typecheck
```

Las pantallas siguen siendo JavaScript. TypeScript se utiliza solamente como herramienta para comprobar los archivos `.js` y `.jsx` mediante `checkJs`; no hace falta aprender TypeScript para seguir el ejercicio.

Documentación oficial: [instalación de Expo Router](https://docs.expo.dev/router/installation/), [navegación](https://docs.expo.dev/router/basics/navigation/) y [cabeceras con Stack](https://docs.expo.dev/router/advanced/stack/).
