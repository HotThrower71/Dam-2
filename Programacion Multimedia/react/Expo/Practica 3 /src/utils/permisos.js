// Aviso cuando se deniega un permiso.
// - que: lo que se necesita ("el micrófono", "la cámara", "tus fotos")
//
// No se ofrece «Reintentar»: en Android, cuando el permiso se ha denegado dos veces, el sistema
// ya no vuelve a enseñar la ventana. La forma segura de darlo es desde los ajustes de la app.
export function avisoPermiso(que) {
    return {
        mensaje: `Sin acceso a ${que} no se puede seguir. Pulsa «Abrir ajustes», activa el permiso en Permisos y vuelve a intentarlo.`,
        ajustes: true, // AvisoError enseña el botón «Abrir ajustes»
    };
}
