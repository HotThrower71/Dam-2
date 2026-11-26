import { AppState, Linking } from "react-native";

// Texto del aviso cuando se deniega un permiso.
// - que: lo que se necesita ("el micrófono", "la cámara", "tus fotos")
// - respuesta: lo que devuelve request...PermissionsAsync()
//
// Mientras canAskAgain sea true, «Reintentar» vuelve a pedir el permiso y sale la ventana del sistema.
// En Android, si el permiso se deniega dos veces, canAskAgain pasa a false: el sistema ya no enseña
// la ventana y la única forma de darlo es desde los ajustes. Solo entonces «Reintentar» lleva a los ajustes.
export function avisoPermiso(que, respuesta) {
    const bloqueado = !respuesta.canAskAgain;
    return {
        mensaje: bloqueado
            ? `El sistema ya no muestra la ventana para dar acceso a ${que}. Pulsa «Reintentar»: se abrirán los ajustes de la app. Activa el permiso y, al volver, la app lo intentará sola.`
            : `Sin acceso a ${que} no se puede seguir. Pulsa «Reintentar» y acepta el permiso.`,
        bloqueado,
    };
}

// Abre los ajustes de la app y, cuando el usuario vuelve a ella, llama a "reintentar".
// Así «Reintentar» funciona siempre, aunque Android ya no enseñe la ventana del permiso.
export function reintentarDesdeAjustes(reintentar) {
    const escucha = AppState.addEventListener("change", (estado) => {
        if (estado === "active") {
            escucha.remove(); // solo una vez
            reintentar();
        }
    });
    Linking.openSettings();
}

// Lo que tiene que hacer el botón «Reintentar»: volver a pedir el permiso o, si está bloqueado, pasar por los ajustes.
export function accionReintentar(bloqueado, reintentar) {
    return bloqueado ? () => reintentarDesdeAjustes(reintentar) : reintentar;
}
