// Funciones pequeñas para enseñar números de forma legible.

// 2048 -> "2,0 KB" · 3500000 -> "3,34 MB"
export function formatearBytes(bytes) {
    if (bytes == null || Number.isNaN(bytes)) return "desconocido";
    if (bytes < 1024) return `${bytes} B`;
    const kb = bytes / 1024;
    if (kb < 1024) return `${kb.toFixed(1).replace(".", ",")} KB`;
    return `${(kb / 1024).toFixed(2).replace(".", ",")} MB`;
}

// 75.4 -> "1:15" (minutos:segundos)
export function formatearTiempo(segundos) {
    if (!segundos || segundos < 0 || !Number.isFinite(segundos)) return "0:00";
    const total = Math.floor(segundos);
    const min = Math.floor(total / 60);
    const seg = total % 60;
    return `${min}:${seg.toString().padStart(2, "0")}`;
}

// Fecha corta para nombres de notas: "26/11 16:42"
export function formatearFecha(fecha) {
    const d = new Date(fecha);
    const dosCifras = (n) => n.toString().padStart(2, "0");
    return `${dosCifras(d.getDate())}/${dosCifras(d.getMonth() + 1)} ${dosCifras(d.getHours())}:${dosCifras(d.getMinutes())}`;
}
