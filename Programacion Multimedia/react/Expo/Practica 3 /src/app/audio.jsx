import {
    RecordingPresets,
    requestRecordingPermissionsAsync,
    setAudioModeAsync,
    useAudioPlayer,
    useAudioPlayerStatus,
    useAudioRecorder,
    useAudioRecorderState,
} from "expo-audio";
import { Directory, File, Paths } from "expo-file-system";
import { useEffect, useRef, useState } from "react";
import { FlatList, Platform, StyleSheet, Text, View } from "react-native";

import AvisoError from "../components/AvisoError";
import Boton from "../components/Boton";
import Tarjeta from "../components/Tarjeta";
import { colores } from "../utils/colores";
import { accionReintentar, avisoPermiso } from "../utils/permisos";
import { formatearBytes, formatearFecha, formatearTiempo } from "../utils/formato";

// Las notas se guardan en la carpeta de documentos de la app (persisten al cerrarla)
// y la lista (nombre, duración, fecha) en un JSON en la misma carpeta.
const hayFicheros = Platform.OS !== "web";
const carpetaNotas = new Directory(Paths.document, "notas");
const ficheroIndice = new File(carpetaNotas, "notas.json");

async function cargarNotas() {
    if (!hayFicheros || !ficheroIndice.exists) return [];
    try {
        const notas = JSON.parse(await ficheroIndice.text());
        // se marca si el audio ya no está en disco (recurso inexistente)
        return notas.map((n) => ({ ...n, existe: new File(carpetaNotas, n.archivo).exists }));
    } catch (e) {
        return []; // índice dañado: se empieza con la lista vacía
    }
}

async function guardarIndice(notas) {
    if (!hayFicheros) return;
    carpetaNotas.create({ idempotent: true });
    // "existe" se calcula al leer, no se guarda
    const limpias = notas.map(({ existe, ...resto }) => resto);
    await ficheroIndice.write(JSON.stringify(limpias));
}

export default function Audio() {
    // grabadora y su estado (se refresca cada 200 ms mientras graba)
    const grabadora = useAudioRecorder(RecordingPresets.HIGH_QUALITY);
    const estadoGrabadora = useAudioRecorderState(grabadora, 200);

    // un único reproductor: al elegir una nota se le cambia la fuente
    const reproductor = useAudioPlayer(null);
    const estadoReproductor = useAudioPlayerStatus(reproductor);

    const [notas, setNotas] = useState([]);
    const [notaActiva, setNotaActiva] = useState(null); // id de la nota cargada en el reproductor
    const [pausada, setPausada] = useState(false); // grabación en pausa
    const [error, setError] = useState(null);
    const grabandoRef = useRef(false);

    useEffect(() => {
        cargarNotas().then(setNotas);
    }, []);

    // al terminar una nota se vuelve al principio para poder repetirla
    useEffect(() => {
        if (estadoReproductor.didJustFinish) {
            reproductor.pause();
            reproductor.seekTo(0);
        }
    }, [estadoReproductor.didJustFinish]);

    // limpieza al salir de la pantalla: si se estaba grabando, se para la grabación
    useEffect(() => {
        return () => {
            if (grabandoRef.current) grabadora.stop().catch(() => {});
        };
    }, []);

    async function empezarGrabacion() {
        setError(null);
        try {
            const permiso = await requestRecordingPermissionsAsync();
            if (!permiso.granted) {
                const aviso = avisoPermiso("el micrófono", permiso);
                setError({
                    titulo: "Permiso de micrófono denegado",
                    mensaje: aviso.mensaje,
                    accion: accionReintentar(aviso.bloqueado, () => empezarGrabacion()),
                });
                return;
            }
            reproductor.pause(); // no se graba y reproduce a la vez
            // en iOS hay que avisar al sistema de que vamos a grabar
            await setAudioModeAsync({ allowsRecording: true, playsInSilentMode: true });
            await grabadora.prepareToRecordAsync();
            grabadora.record();
            grabandoRef.current = true;
            setPausada(false);
        } catch (e) {
            setError({ titulo: "No se ha podido empezar a grabar", mensaje: e.message || String(e), accion: () => empezarGrabacion() });
        }
    }

    function pausarOReanudar() {
        if (pausada) {
            grabadora.record();
            setPausada(false);
        } else {
            grabadora.pause();
            setPausada(true);
        }
    }

    async function pararGrabacion() {
        try {
            const duracion = estadoGrabadora.durationMillis / 1000;
            await grabadora.stop();
            grabandoRef.current = false;
            setPausada(false);
            // se vuelve al modo normal para que el sonido salga por el altavoz
            await setAudioModeAsync({ allowsRecording: false, playsInSilentMode: true });

            const uri = grabadora.uri;
            if (!uri) throw new Error("La grabadora no ha devuelto ningún fichero");
            if (duracion < 0.5) {
                setError({ titulo: "Nota demasiado corta", mensaje: "Mantén la grabación al menos medio segundo." });
                return;
            }

            const fecha = Date.now();
            let nota = { id: String(fecha), fecha, duracion, archivo: uri, existe: true, bytes: null };

            if (hayFicheros) {
                // se mueve de la caché a nuestra carpeta para que no la borre el sistema
                carpetaNotas.create({ idempotent: true });
                const extension = uri.split(".").pop();
                const destino = new File(carpetaNotas, `nota-${fecha}.${extension}`);
                await new File(uri).move(destino);
                nota = { ...nota, archivo: destino.name, bytes: destino.size };
            }

            const nuevas = [nota, ...notas];
            setNotas(nuevas);
            await guardarIndice(nuevas);
        } catch (e) {
            grabandoRef.current = false;
            setError({ titulo: "No se ha podido guardar la nota", mensaje: e.message || String(e) });
        }
    }

    function uriDe(nota) {
        return hayFicheros ? new File(carpetaNotas, nota.archivo).uri : nota.archivo;
    }

    function reproducir(nota) {
        setError(null);
        if (hayFicheros && !new File(carpetaNotas, nota.archivo).exists) {
            setNotas((lista) => lista.map((n) => (n.id === nota.id ? { ...n, existe: false } : n)));
            setError({
                titulo: "El audio no existe",
                mensaje: "El fichero de esta nota se ha borrado del dispositivo. Puedes quitarla de la lista.",
                accion: () => borrar(nota),
                textoAccion: "Quitar de la lista",
            });
            return;
        }
        try {
            if (notaActiva !== nota.id) {
                reproductor.replace({ uri: uriDe(nota) });
                setNotaActiva(nota.id);
            }
            reproductor.play();
        } catch (e) {
            setError({ titulo: "No se ha podido reproducir", mensaje: e.message || String(e), accion: () => reproducir(nota) });
        }
    }

    function parar() {
        reproductor.pause();
        reproductor.seekTo(0);
    }

    async function borrar(nota) {
        try {
            if (notaActiva === nota.id) {
                reproductor.pause();
                reproductor.replace(null);
                setNotaActiva(null);
            }
            if (hayFicheros) {
                const fichero = new File(carpetaNotas, nota.archivo);
                if (fichero.exists) fichero.delete();
            }
            const nuevas = notas.filter((n) => n.id !== nota.id);
            setNotas(nuevas);
            await guardarIndice(nuevas);
            setError(null);
        } catch (e) {
            setError({ titulo: "No se ha podido borrar", mensaje: e.message || String(e) });
        }
    }

    const grabando = estadoGrabadora.isRecording || pausada;
    const textoEstadoGrabacion = estadoGrabadora.isRecording ? "● Grabando" : pausada ? "❚❚ En pausa" : "Listo para grabar";

    return (
        <FlatList
            data={notas}
            keyExtractor={(n) => n.id}
            contentContainerStyle={estilos.contenido}
            ListHeaderComponent={
                <View style={estilos.cabecera}>
                    <Tarjeta titulo="Grabadora">
                        <View style={estilos.filaEstado}>
                            <Text style={[estilos.estado, estadoGrabadora.isRecording && estilos.estadoGrabando]}>
                                {textoEstadoGrabacion}
                            </Text>
                            <Text style={estilos.cronometro}>{formatearTiempo(estadoGrabadora.durationMillis / 1000)}</Text>
                        </View>
                        {!grabando ? (
                            <Boton texto="Grabar nota" onPress={() => empezarGrabacion()} variante="acento" />
                        ) : (
                            <View style={estilos.fila}>
                                <Boton texto={pausada ? "Reanudar" : "Pausar"} onPress={pausarOReanudar} variante="secundario" estilo={estilos.flex} />
                                <Boton texto="Parar y guardar" onPress={pararGrabacion} variante="peligro" estilo={estilos.flex} />
                            </View>
                        )}
                    </Tarjeta>

                    {error && (
                        <AvisoError
                            titulo={error.titulo}
                            mensaje={error.mensaje}
                            onReintentar={error.accion}
                            textoReintentar={error.textoAccion}
                            irAjustes={error.ajustes}
                        />
                    )}

                    <Text style={estilos.subtitulo}>Mis notas ({notas.length})</Text>
                </View>
            }
            ListEmptyComponent={<Text style={estilos.vacio}>Aún no hay notas. ¡Graba la primera!</Text>}
            renderItem={({ item, index }) => {
                const activa = item.id === notaActiva;
                const sonando = activa && estadoReproductor.playing;
                const duracion = activa && estadoReproductor.duration ? estadoReproductor.duration : item.duracion;
                const progreso = activa && duracion ? Math.min(estadoReproductor.currentTime / duracion, 1) : 0;
                let estadoNota = "Parada";
                if (activa && estadoReproductor.isBuffering) estadoNota = "Cargando…";
                else if (sonando) estadoNota = "▶ Reproduciendo";
                else if (activa && estadoReproductor.currentTime > 0) estadoNota = "❚❚ En pausa";

                return (
                    <View style={[estilos.nota, activa && estilos.notaActiva]}>
                        <View style={estilos.filaEstado}>
                            <Text style={estilos.nombreNota}>Nota {notas.length - index}</Text>
                            <Text style={estilos.detalle}>{formatearFecha(item.fecha)}</Text>
                        </View>
                        <Text style={estilos.detalle}>
                            {item.existe === false ? "⚠ Fichero no encontrado" : estadoNota} ·{" "}
                            {activa ? `${formatearTiempo(estadoReproductor.currentTime)} / ` : ""}
                            {formatearTiempo(duracion)}
                            {item.bytes ? ` · ${formatearBytes(item.bytes)}` : ""}
                        </Text>
                        <View style={estilos.barra}>
                            <View style={[estilos.relleno, { width: `${progreso * 100}%` }]} />
                        </View>
                        <View style={estilos.fila}>
                            <Boton
                                texto={sonando ? "Pausa" : "Reproducir"}
                                onPress={() => (sonando ? reproductor.pause() : reproducir(item))}
                                disabled={grabando}
                                estilo={estilos.flex}
                            />
                            <Boton texto="Parar" onPress={parar} variante="secundario" disabled={!activa} estilo={estilos.flex} />
                            <Boton texto="Borrar" onPress={() => borrar(item)} variante="peligro" estilo={estilos.flex} />
                        </View>
                    </View>
                );
            }}
        />
    );
}

const estilos = StyleSheet.create({
    contenido: { padding: 16, gap: 12 },
    cabecera: { gap: 14 },
    fila: { flexDirection: "row", gap: 8 },
    flex: { flex: 1 },
    filaEstado: { flexDirection: "row", justifyContent: "space-between", alignItems: "center" },
    estado: { fontSize: 15, fontWeight: "600", color: colores.suave },
    estadoGrabando: { color: colores.error },
    cronometro: { fontSize: 32, fontWeight: "bold", color: colores.texto, fontVariant: ["tabular-nums"] },
    subtitulo: { fontSize: 17, fontWeight: "bold", color: colores.texto, marginTop: 4 },
    vacio: { color: colores.suave, textAlign: "center", marginTop: 20 },
    nota: { backgroundColor: colores.tarjeta, borderRadius: 14, padding: 14, gap: 8, borderWidth: 1, borderColor: colores.borde },
    notaActiva: { borderColor: colores.acento, borderWidth: 2 },
    nombreNota: { fontSize: 16, fontWeight: "bold", color: colores.texto },
    detalle: { fontSize: 13, color: colores.suave },
    barra: { height: 6, borderRadius: 3, backgroundColor: "#ebe6dc", overflow: "hidden" },
    relleno: { height: "100%", backgroundColor: colores.acento },
});
