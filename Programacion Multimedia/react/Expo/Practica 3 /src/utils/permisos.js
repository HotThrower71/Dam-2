// Texto del aviso cuando se deniega un permiso.
// - que: lo que se necesita ("el micrófono", "la cámara", "tus fotos")
// - respuesta: lo que devuelve request...PermissionsAsync()
// - esReintento: true si se ha pulsado «Reintentar» en el aviso
//
// En Android, si el permiso se deniega dos veces, el sistema ya no vuelve a enseñar la ventana:
// la petición devuelve "denegado" al momento. Por eso, si al reintentar sigue denegado,
// se avisa de que hay que activarlo en los ajustes en vez de enseñar el mismo mensaje otra vez.
export function avisoPermiso(que, respuesta, esReintento) {
    const bloqueado = !respuesta.canAskAgain || esReintento;
    return {
        mensaje: bloqueado
            ? `El sistema ya no muestra la ventana para dar acceso a ${que}. Pulsa «Abrir ajustes», actívalo en Permisos y vuelve a la app: entonces «Reintentar» funcionará.`
            : `Sin acceso a ${que} no se puede seguir. Pulsa «Reintentar» y acepta el permiso.`,
        ajustes: bloqueado,
    };
}
