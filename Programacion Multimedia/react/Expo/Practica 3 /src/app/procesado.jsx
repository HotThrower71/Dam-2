import { Directory, File, Paths } from "expo-file-system";
import { ImageManipulator, SaveFormat } from "expo-image-manipulator";
import * as ImagePicker from "expo-image-picker";
import { useLocalSearchParams, useRouter } from "expo-router";
import { useEffect, useState } from "react";
import { Image, Platform, Pressable, ScrollView, StyleSheet, Text, TextInput, View } from "react-native";

import AvisoError from "../components/AvisoError";
import Boton from "../components/Boton";
import Dato from "../components/Dato";
import Tarjeta from "../components/Tarjeta";
import { colores } from "../utils/colores";
import { avisoPermiso } from "../utils/permisos";
import { formatearBytes } from "../utils/formato";

// Los tres formatos de salida. PNG no tiene "calidad": siempre es sin pérdida.
const FORMATOS = [
    { id: SaveFormat.JPEG, nombre: "JPEG", extension: "jpg" },
    { id: SaveFormat.PNG, nombre: "PNG", extension: "png" },
    { id: SaveFormat.WEBP, nombre: "WebP", extension: "webp" },
];

const RECORTES = [
    { id: "ninguno", nombre: "Sin recorte" },
    { id: "cuadrado", nombre: "Cuadrado" },
    { id: "panoramico", nombre: "16:9" },
];

const CALIDADES = [1, 0.8, 0.5];

// Carpeta de caché para los resultados: el sistema puede liberarla y además la vaciamos desde la app.
const carpetaCache = new Directory(Paths.cache, "procesadas");
const hayFicheros = Platform.OS !== "web";

// Rectángulo de recorte centrado en la imagen original según la proporción elegida.
function calcularRecorte(tipo, ancho, alto) {
    if (tipo === "ninguno") return null;
    const proporcion = tipo === "cuadrado" ? 1 : 16 / 9;
    let anchoRecorte = ancho;
    let altoRecorte = Math.round(ancho / proporcion);
    if (altoRecorte > alto) {
        // la imagen es más "apaisada" que la proporción: manda el alto
        altoRecorte = alto;
        anchoRecorte = Math.round(alto * proporcion);
    }
    return {
        originX: Math.floor((ancho - anchoRecorte) / 2),
        originY: Math.floor((alto - altoRecorte) / 2),
        width: anchoRecorte,
        height: altoRecorte,
    };
}

function bytesDelFichero(uri) {
    try {
        return new File(uri).size;
    } catch (e) {
        return null;
    }
}

function leerCache() {
    if (!hayFicheros || !carpetaCache.exists) return { numero: 0, bytes: 0 };
    const ficheros = carpetaCache.list().filter((f) => f instanceof File);
    return { numero: ficheros.length, bytes: ficheros.reduce((s, f) => s + (f.size || 0), 0) };
}

export default function Procesado() {
    const router = useRouter();
    const params = useLocalSearchParams(); // llegan de la pantalla Imágenes (pueden no llegar)

    const [original, setOriginal] = useState(
        params.uri ? { uri: params.uri, ancho: Number(params.ancho), alto: Number(params.alto), bytes: Number(params.bytes) || null } : null
    );
    const [anchoTexto, setAnchoTexto] = useState("800");
    const [recorte, setRecorte] = useState("ninguno");
    const [calidad, setCalidad] = useState(0.8);
    const [resultados, setResultados] = useState([]); // uno por formato
    const [seleccionado, setSeleccionado] = useState(null);
    const [procesando, setProcesando] = useState(false);
    const [error, setError] = useState(null);
    const [cache, setCache] = useState({ numero: 0, bytes: 0 });

    useEffect(() => {
        setCache(leerCache());
    }, []);

    // Si no venimos de "Imágenes", se puede elegir aquí directamente de la galería.
    async function elegirDeGaleria() {
        setError(null);
        try {
            const permiso = await ImagePicker.requestMediaLibraryPermissionsAsync();
            if (!permiso.granted) {
                setError({ titulo: "Permiso denegado", ...avisoPermiso("tus fotos") });
                return;
            }
            const r = await ImagePicker.launchImageLibraryAsync({ mediaTypes: ["images"], quality: 1 });
            if (r.canceled) return;
            const a = r.assets[0];
            setOriginal({ uri: a.uri, ancho: a.width, alto: a.height, bytes: a.fileSize || bytesDelFichero(a.uri) });
            setResultados([]);
        } catch (e) {
            setError({ titulo: "No se ha podido abrir la galería", mensaje: e.message || String(e), accion: () => elegirDeGaleria() });
        }
    }

    // Comprueba los datos antes de procesar. Devuelve un texto de error o null si todo está bien.
    function validar() {
        const ancho = Number(anchoTexto);
        if (!Number.isInteger(ancho)) return "El ancho tiene que ser un número entero de píxeles.";
        if (ancho < 16) return "El ancho mínimo es 16 px.";
        const rect = calcularRecorte(recorte, original.ancho, original.alto);
        const anchoDisponible = rect ? rect.width : original.ancho;
        if (ancho > anchoDisponible) {
            return `El ancho no puede ser mayor que el de la imagen (${anchoDisponible} px): no se amplían imágenes.`;
        }
        return null;
    }

    // Encadena recortar -> redimensionar, renderiza UNA vez y guarda el resultado en los tres formatos.
    async function procesar() {
        setError(null);
        const problema = validar();
        if (problema) {
            setError({ titulo: "Revisa los datos", mensaje: problema });
            return;
        }
        // recurso inexistente: la caché del picker puede haberse borrado
        if (hayFicheros && !new File(original.uri).exists) {
            setError({
                titulo: "La imagen ya no existe",
                mensaje: "El sistema ha borrado el fichero temporal. Vuelve a elegir la imagen.",
                accion: () => elegirDeGaleria(),
            });
            return;
        }

        setProcesando(true);
        let contexto = null;
        let imagenRenderizada = null;
        try {
            const rect = calcularRecorte(recorte, original.ancho, original.alto);
            const anchoFinal = Number(anchoTexto);
            const anchoBase = rect ? rect.width : original.ancho;
            const altoBase = rect ? rect.height : original.alto;
            const altoEsperado = Math.round((altoBase * anchoFinal) / anchoBase);

            // 1. transformaciones encadenadas (se aplican en este orden)
            contexto = ImageManipulator.manipulate(original.uri);
            if (rect) contexto.crop(rect);
            contexto.resize({ width: anchoFinal }); // solo el ancho: el alto se calcula para no deformar
            imagenRenderizada = await contexto.renderAsync();

            // 2. la misma imagen renderizada se guarda en cada formato
            if (hayFicheros) carpetaCache.create({ idempotent: true });
            const nuevos = [];
            for (const formato of FORMATOS) {
                try {
                    const guardada = await imagenRenderizada.saveAsync({ format: formato.id, compress: calidad });
                    let uri = guardada.uri;
                    if (hayFicheros) {
                        // se mueve a nuestra carpeta de caché para poder medirla y vaciarla luego
                        const fichero = new File(guardada.uri);
                        await fichero.move(new File(carpetaCache, `${Date.now()}-${formato.nombre}.${formato.extension}`));
                        uri = fichero.uri;
                    }
                    nuevos.push({
                        formato: formato.nombre,
                        uri,
                        ancho: guardada.width,
                        alto: guardada.height,
                        bytes: hayFicheros ? bytesDelFichero(uri) : null,
                        // validación: ¿ha salido con las medidas que esperábamos? (±1 px por redondeo)
                        valido: guardada.width === anchoFinal && Math.abs(guardada.height - altoEsperado) <= 1,
                    });
                } catch (e) {
                    // p. ej. un formato que este dispositivo no sabe codificar: se informa y se siguen los demás
                    nuevos.push({ formato: formato.nombre, fallo: `No compatible en este dispositivo (${e.message || e})` });
                }
            }
            setResultados(nuevos);
            setSeleccionado(nuevos.find((r) => !r.fallo)?.formato ?? null);
            setCache(leerCache());
        } catch (e) {
            setError({ titulo: "No se ha podido procesar la imagen", mensaje: e.message || String(e), accion: procesar });
        } finally {
            // limpieza de los objetos nativos (ocupan memoria fuera de JavaScript)
            imagenRenderizada?.release?.();
            contexto?.release?.();
            setProcesando(false);
        }
    }

    function vaciarCache() {
        try {
            if (carpetaCache.exists) carpetaCache.delete();
            setResultados([]);
            setSeleccionado(null);
            setCache(leerCache());
        } catch (e) {
            setError({ titulo: "No se ha podido vaciar la caché", mensaje: e.message || String(e) });
        }
    }

    // ---------- sin imagen todavía ----------
    if (!original) {
        return (
            <ScrollView contentContainerStyle={estilos.contenido}>
                <Text style={estilos.ayuda}>Primero necesitas una imagen.</Text>
                <Boton texto="Elegir de la galería" onPress={() => elegirDeGaleria()} />
                <Boton texto="Ir a Imágenes (galería o cámara)" onPress={() => router.replace("/imagenes")} variante="secundario" />
                {error && <AvisoError titulo={error.titulo} mensaje={error.mensaje} onReintentar={error.accion} irAjustes={error.ajustes} />}
            </ScrollView>
        );
    }

    const validos = resultados.filter((r) => !r.fallo && r.bytes);
    const masLigero = validos.length ? validos.reduce((a, b) => (a.bytes <= b.bytes ? a : b)) : null;
    const vistaPrevia = resultados.find((r) => r.formato === seleccionado);

    return (
        <ScrollView contentContainerStyle={estilos.contenido} keyboardShouldPersistTaps="handled">
            <Tarjeta titulo="Imagen original">
                <Image source={{ uri: original.uri }} style={estilos.miniatura} resizeMode="contain" />
                <Dato etiqueta="Dimensiones" valor={`${original.ancho} × ${original.alto} px`} />
                <Dato etiqueta="Tamaño" valor={formatearBytes(original.bytes)} />
                <Boton texto="Cambiar imagen" onPress={() => elegirDeGaleria()} variante="secundario" />
            </Tarjeta>

            <Tarjeta titulo="Transformaciones">
                <Text style={estilos.etiqueta}>1. Recortar (centrado)</Text>
                <Chips opciones={RECORTES} valor={recorte} onCambio={setRecorte} />

                <Text style={estilos.etiqueta}>2. Redimensionar: ancho final en px</Text>
                <TextInput
                    value={anchoTexto}
                    onChangeText={setAnchoTexto}
                    keyboardType="number-pad"
                    style={estilos.input}
                    placeholder="p. ej. 800"
                    maxLength={5}
                />

                <Text style={estilos.etiqueta}>3. Calidad JPEG / WebP</Text>
                <Chips
                    opciones={CALIDADES.map((c) => ({ id: c, nombre: `${Math.round(c * 100)} %` }))}
                    valor={calidad}
                    onCambio={setCalidad}
                />

                <Boton texto="Procesar y convertir a JPEG, PNG y WebP" onPress={procesar} cargando={procesando} />
            </Tarjeta>

            {error && <AvisoError titulo={error.titulo} mensaje={error.mensaje} onReintentar={error.accion} irAjustes={error.ajustes} />}

            {resultados.length > 0 && (
                <Tarjeta titulo="Comparativa de formatos">
                    <View style={estilos.tabla}>
                        <View style={[estilos.filaTabla, estilos.cabecera]}>
                            <Text style={[estilos.celda, estilos.textoCabecera]}>Formato</Text>
                            <Text style={[estilos.celda, estilos.textoCabecera]}>Medidas</Text>
                            <Text style={[estilos.celda, estilos.textoCabecera]}>Tamaño</Text>
                            <Text style={[estilos.celda, estilos.textoCabecera]}>vs. original</Text>
                        </View>
                        {resultados.map((r) => (
                            <Pressable
                                key={r.formato}
                                onPress={() => !r.fallo && setSeleccionado(r.formato)}
                                style={[estilos.filaTabla, seleccionado === r.formato && estilos.filaElegida]}
                            >
                                <Text style={[estilos.celda, estilos.negrita]}>
                                    {r.formato}
                                    {masLigero?.formato === r.formato ? " ★" : ""}
                                </Text>
                                {r.fallo ? (
                                    <Text style={[estilos.celda, estilos.celdaFallo]}>{r.fallo}</Text>
                                ) : (
                                    <>
                                        <Text style={estilos.celda}>
                                            {r.ancho}×{r.alto} {r.valido ? "✓" : "⚠"}
                                        </Text>
                                        <Text style={estilos.celda}>{formatearBytes(r.bytes)}</Text>
                                        <Text style={estilos.celda}>
                                            {r.bytes && original.bytes ? `${Math.round((r.bytes / original.bytes) * 100)} %` : "—"}
                                        </Text>
                                    </>
                                )}
                            </Pressable>
                        ))}
                    </View>
                    <Text style={estilos.ayuda}>
                        ✓ = las medidas coinciden con las esperadas. ★ = el más ligero. Toca una fila para verla.
                    </Text>
                    {masLigero && (
                        <Text style={estilos.justificacion}>
                            Recomendación: {masLigero.formato}. {justificar(masLigero.formato)}
                        </Text>
                    )}
                    {vistaPrevia && !vistaPrevia.fallo && (
                        <Image
                            source={{ uri: vistaPrevia.uri }}
                            style={[estilos.resultado, { aspectRatio: vistaPrevia.ancho / vistaPrevia.alto }]}
                            resizeMode="contain"
                        />
                    )}
                </Tarjeta>
            )}

            {hayFicheros && (
                <Tarjeta titulo="Caché de resultados">
                    <Dato etiqueta="Ficheros" valor={cache.numero} />
                    <Dato etiqueta="Espacio ocupado" valor={formatearBytes(cache.bytes)} />
                    <Boton texto="Vaciar caché" onPress={vaciarCache} variante="secundario" disabled={cache.numero === 0} />
                </Tarjeta>
            )}
        </ScrollView>
    );
}

// Por qué conviene cada formato (se enseña junto al más ligero).
function justificar(formato) {
    if (formato === "WebP") return "Comprime mejor que JPEG con la misma calidad y lo leen Android, iOS y los navegadores: ideal para la web y las apps.";
    if (formato === "JPEG") return "Es el estándar para fotos: compatible con todo y con buena compresión con pérdida.";
    return "Sin pérdida: útil para capturas, gráficos o imágenes con texto, aunque en fotos suele ocupar más.";
}

// Botones de opción (solo uno marcado).
function Chips({ opciones, valor, onCambio }) {
    return (
        <View style={estilos.chips}>
            {opciones.map((o) => (
                <Pressable key={o.id} onPress={() => onCambio(o.id)} style={[estilos.chip, valor === o.id && estilos.chipActivo]}>
                    <Text style={[estilos.textoChip, valor === o.id && estilos.textoChipActivo]}>{o.nombre}</Text>
                </Pressable>
            ))}
        </View>
    );
}

const estilos = StyleSheet.create({
    contenido: { padding: 16, gap: 14 },
    ayuda: { color: colores.suave, fontSize: 13, lineHeight: 19 },
    etiqueta: { color: colores.texto, fontWeight: "600", fontSize: 14 },
    miniatura: { width: "100%", height: 160, borderRadius: 10, backgroundColor: "#e9e5dc" },
    resultado: { width: "100%", maxHeight: 300, borderRadius: 10, backgroundColor: "#e9e5dc" },
    input: {
        borderWidth: 1,
        borderColor: colores.borde,
        borderRadius: 10,
        paddingHorizontal: 12,
        paddingVertical: 10,
        fontSize: 16,
        backgroundColor: "#fbfaf7",
    },
    chips: { flexDirection: "row", flexWrap: "wrap", gap: 8 },
    chip: { borderWidth: 1.5, borderColor: colores.principal, borderRadius: 20, paddingHorizontal: 14, paddingVertical: 7 },
    chipActivo: { backgroundColor: colores.principal },
    textoChip: { color: colores.principal, fontWeight: "600" },
    textoChipActivo: { color: "#ffffff" },
    tabla: { borderWidth: 1, borderColor: colores.borde, borderRadius: 10, overflow: "hidden" },
    filaTabla: { flexDirection: "row", borderTopWidth: StyleSheet.hairlineWidth, borderTopColor: colores.borde },
    cabecera: { backgroundColor: colores.principal, borderTopWidth: 0 },
    filaElegida: { backgroundColor: "#e6efe9" },
    celda: { flex: 1, paddingVertical: 8, paddingHorizontal: 6, fontSize: 13, color: colores.texto },
    celdaFallo: { flex: 3, color: colores.error },
    textoCabecera: { color: "#ffffff", fontWeight: "bold" },
    negrita: { fontWeight: "bold" },
    justificacion: { color: colores.texto, fontSize: 14, lineHeight: 20 },
});
