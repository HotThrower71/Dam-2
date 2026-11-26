import { useEvent, useEventListener } from "expo";
import { useFocusEffect } from "expo-router";
import { useVideoPlayer, VideoView } from "expo-video";
import { useCallback, useState } from "react";
import { ActivityIndicator, Pressable, ScrollView, StyleSheet, Text, View } from "react-native";

import AvisoError from "../components/AvisoError";
import Boton from "../components/Boton";
import Tarjeta from "../components/Tarjeta";
import { colores } from "../utils/colores";
import { formatearTiempo } from "../utils/formato";

// Fuentes de vídeo. Las dos últimas están mal a propósito para enseñar la gestión de errores.
const FUENTES = [
    { id: "local", nombre: "Local", tipo: "MP4 incluido en la app", fuente: require("../../assets/video/local.mp4") },
    {
        id: "remoto",
        nombre: "Remoto",
        tipo: "MP4 por internet (MDN, CC0)",
        fuente: { uri: "https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4" },
    },
    {
        id: "hls",
        nombre: "HLS",
        tipo: "Streaming adaptativo .m3u8 (Mux)",
        fuente: { uri: "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8", contentType: "hls" },
    },
    {
        id: "hls2",
        nombre: "HLS 2",
        tipo: "Streaming .m3u8 (Apple bipbop)",
        fuente: {
            uri: "https://devstreaming-cdn.apple.com/videos/streaming/examples/img_bipbop_adv_example_fmp4/master.m3u8",
            contentType: "hls",
        },
    },
    {
        id: "noexiste",
        nombre: "No existe",
        tipo: "Error: la URL devuelve 404",
        fuente: { uri: "https://interactive-examples.mdn.mozilla.net/media/cc0-videos/no-existe.mp4" },
    },
    {
        id: "incompatible",
        nombre: "Incompatible",
        tipo: "Error: una página web, no un vídeo",
        fuente: { uri: "https://example.com/index.html", contentType: "progressive" },
    },
];

const VELOCIDADES = [0.5, 1, 1.5, 2];

export default function Video() {
    const [fuenteActual, setFuenteActual] = useState(FUENTES[0]);
    const [eventos, setEventos] = useState([]); // registro de los últimos eventos del reproductor
    const [anchoBarra, setAnchoBarra] = useState(1);

    // el reproductor se crea una vez; la función inicial lo configura
    const reproductor = useVideoPlayer(FUENTES[0].fuente, (p) => {
        p.loop = false;
        p.timeUpdateEventInterval = 0.25; // cada cuánto llega el evento timeUpdate (segundos)
    });

    // estados del reproductor a partir de sus eventos
    const { status, error } = useEvent(reproductor, "statusChange", { status: reproductor.status });
    const { isPlaying } = useEvent(reproductor, "playingChange", { isPlaying: reproductor.playing });
    const { currentTime, bufferedPosition } = useEvent(reproductor, "timeUpdate", {
        currentTime: reproductor.currentTime,
        bufferedPosition: reproductor.bufferedPosition,
    });
    const { muted } = useEvent(reproductor, "mutedChange", { muted: reproductor.muted });
    const { playbackRate } = useEvent(reproductor, "playbackRateChange", { playbackRate: reproductor.playbackRate });

    function anotar(texto) {
        const hora = new Date().toLocaleTimeString();
        setEventos((lista) => [`${hora} · ${texto}`, ...lista].slice(0, 6));
    }

    // escuchas de eventos solo para el registro
    useEventListener(reproductor, "statusChange", ({ status: s, error: e }) => anotar(`statusChange → ${s}${e ? ` (${e.message})` : ""}`));
    useEventListener(reproductor, "playingChange", ({ isPlaying: p }) => anotar(p ? "playingChange → reproduciendo" : "playingChange → pausa"));
    useEventListener(reproductor, "sourceLoad", ({ duration }) => anotar(`sourceLoad → duración ${formatearTiempo(duration)}`));
    useEventListener(reproductor, "playToEnd", () => anotar("playToEnd → ha terminado"));

    // al salir de la pantalla se pausa (el reproductor lo libera useVideoPlayer al desmontar)
    useFocusEffect(
        useCallback(() => {
            return () => reproductor.pause();
        }, [reproductor])
    );

    async function cambiarFuente(f) {
        setFuenteActual(f);
        setEventos([]);
        try {
            await reproductor.replaceAsync(f.fuente);
            reproductor.play();
        } catch (e) {
            anotar(`error al cambiar la fuente: ${e.message || e}`);
        }
    }

    function reintentar() {
        cambiarFuente(fuenteActual);
    }

    function playPausa() {
        if (isPlaying) {
            reproductor.pause();
        } else {
            // si estaba al final, vuelve al principio
            if (terminado || (reproductor.duration && currentTime >= reproductor.duration - 0.3)) reproductor.currentTime = 0;
            reproductor.play();
        }
    }

    // tocar la barra salta a ese punto del vídeo
    function saltarA(evento) {
        const duracion = reproductor.duration;
        if (!duracion) return; // en directo o sin cargar no se puede saltar
        const fraccion = Math.min(Math.max(evento.nativeEvent.locationX / anchoBarra, 0), 1);
        reproductor.currentTime = fraccion * duracion;
    }

    const duracion = reproductor.duration || 0;
    const progreso = duracion ? Math.min(currentTime / duracion, 1) : 0;
    const cargado = duracion ? Math.min(bufferedPosition / duracion, 1) : 0;
    // en Android, al llegar al final el reproductor pasa a "idle" aunque el vídeo sigue cargado:
    // si ya conocemos la duración, se dejan usar los controles para volver a reproducirlo
    const terminado = status === "idle" && duracion > 0;
    const listo = status === "readyToPlay" || terminado;

    const textoEstado = {
        idle: terminado ? "■ Terminado" : "Sin cargar",
        loading: "Cargando…",
        readyToPlay: isPlaying ? "▶ Reproduciendo" : "❚❚ En pausa",
        error: "Error",
    }[status];

    return (
        <ScrollView contentContainerStyle={estilos.contenido}>
            <View style={estilos.fuentes}>
                {FUENTES.map((f) => (
                    <Pressable
                        key={f.id}
                        onPress={() => cambiarFuente(f)}
                        style={[estilos.chip, fuenteActual.id === f.id && estilos.chipActivo]}
                    >
                        <Text style={[estilos.textoChip, fuenteActual.id === f.id && estilos.textoChipActivo]}>{f.nombre}</Text>
                    </Pressable>
                ))}
            </View>
            <Text style={estilos.tipo}>{fuenteActual.tipo}</Text>

            <View style={estilos.marco}>
                {/* nativeControls={false}: los controles son los nuestros, de abajo */}
                <VideoView player={reproductor} style={estilos.video} nativeControls={false} contentFit="contain" />
                {status === "loading" && (
                    <View style={estilos.capa}>
                        <ActivityIndicator size="large" color="#ffffff" />
                    </View>
                )}
            </View>

            {status === "error" && (
                <AvisoError
                    titulo="No se puede reproducir este vídeo"
                    mensaje={`${explicarError(fuenteActual.id)}${error?.message ? `\nDetalle: ${error.message}` : ""}`}
                    onReintentar={reintentar}
                />
            )}

            <Tarjeta>
                <View style={estilos.filaEstado}>
                    <Text style={estilos.estado}>{textoEstado}</Text>
                    <Text style={estilos.tiempo}>
                        {formatearTiempo(currentTime)} / {duracion ? formatearTiempo(duracion) : "--:--"}
                    </Text>
                </View>

                <Pressable onPress={saltarA} onLayout={(e) => setAnchoBarra(e.nativeEvent.layout.width || 1)} style={estilos.barra}>
                    <View style={[estilos.bufer, { width: `${cargado * 100}%` }]} />
                    <View style={[estilos.relleno, { width: `${progreso * 100}%` }]} />
                </Pressable>

                <View style={estilos.fila}>
                    <Boton texto="−10 s" onPress={() => reproductor.seekBy(-10)} variante="secundario" disabled={!listo} estilo={estilos.flex} />
                    <Boton texto={isPlaying ? "Pausa" : "Play"} onPress={playPausa} disabled={!listo} estilo={estilos.flex2} />
                    <Boton texto="+10 s" onPress={() => reproductor.seekBy(10)} variante="secundario" disabled={!listo} estilo={estilos.flex} />
                </View>
                <View style={estilos.fila}>
                    <Boton
                        texto="Reiniciar"
                        onPress={() => {
                            reproductor.currentTime = 0;
                            reproductor.play();
                        }}
                        variante="secundario"
                        disabled={!listo}
                        estilo={estilos.flex}
                    />
                    <Boton
                        texto={muted ? "Activar sonido" : "Silenciar"}
                        onPress={() => (reproductor.muted = !muted)}
                        variante="secundario"
                        estilo={estilos.flex}
                    />
                </View>
                <View style={estilos.fuentes}>
                    {VELOCIDADES.map((v) => (
                        <Pressable
                            key={v}
                            onPress={() => (reproductor.playbackRate = v)}
                            style={[estilos.chip, playbackRate === v && estilos.chipActivo]}
                        >
                            <Text style={[estilos.textoChip, playbackRate === v && estilos.textoChipActivo]}>{v}×</Text>
                        </Pressable>
                    ))}
                </View>
            </Tarjeta>

            <Tarjeta titulo="Eventos del reproductor">
                {eventos.length === 0 ? (
                    <Text style={estilos.evento}>Todavía no ha llegado ninguno.</Text>
                ) : (
                    eventos.map((e, i) => (
                        <Text key={i} style={estilos.evento}>
                            {e}
                        </Text>
                    ))
                )}
            </Tarjeta>
        </ScrollView>
    );
}

// Mensaje comprensible según qué se intentaba reproducir.
function explicarError(id) {
    if (id === "noexiste") return "El vídeo no existe en el servidor (error 404).";
    if (id === "incompatible") return "El fichero no es un vídeo o su formato no es compatible.";
    if (id === "local") return "No se ha podido abrir el vídeo incluido en la app.";
    return "Comprueba tu conexión a internet: no se ha podido descargar el vídeo.";
}

const estilos = StyleSheet.create({
    contenido: { padding: 16, gap: 12 },
    fuentes: { flexDirection: "row", flexWrap: "wrap", gap: 8 },
    chip: { borderWidth: 1.5, borderColor: colores.principal, borderRadius: 20, paddingHorizontal: 12, paddingVertical: 6 },
    chipActivo: { backgroundColor: colores.principal },
    textoChip: { color: colores.principal, fontWeight: "600" },
    textoChipActivo: { color: "#ffffff" },
    tipo: { color: colores.suave, fontSize: 13 },
    marco: { width: "100%", aspectRatio: 16 / 9, backgroundColor: "#000000", borderRadius: 12, overflow: "hidden" },
    video: { width: "100%", height: "100%" },
    capa: { ...StyleSheet.absoluteFillObject, alignItems: "center", justifyContent: "center", backgroundColor: "rgba(0,0,0,0.35)" },
    filaEstado: { flexDirection: "row", justifyContent: "space-between", alignItems: "center" },
    estado: { fontSize: 15, fontWeight: "600", color: colores.texto },
    tiempo: { fontSize: 15, color: colores.suave, fontVariant: ["tabular-nums"] },
    barra: { height: 14, borderRadius: 7, backgroundColor: "#ebe6dc", overflow: "hidden", justifyContent: "center" },
    bufer: { position: "absolute", left: 0, top: 0, bottom: 0, backgroundColor: "#d5cdbd" },
    relleno: { position: "absolute", left: 0, top: 0, bottom: 0, backgroundColor: colores.acento },
    fila: { flexDirection: "row", gap: 8 },
    flex: { flex: 1 },
    flex2: { flex: 1.4 },
    evento: { fontSize: 12, color: colores.suave, fontFamily: "monospace" },
});
