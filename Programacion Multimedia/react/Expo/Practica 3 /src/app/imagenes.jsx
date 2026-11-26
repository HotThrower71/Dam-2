import { Directory, File, Paths } from "expo-file-system";
import * as ImagePicker from "expo-image-picker";
import { useRouter } from "expo-router";
import { useEffect, useState } from "react";
import { Image, Platform, ScrollView, StyleSheet, Text, View } from "react-native";

import AvisoError from "../components/AvisoError";
import Boton from "../components/Boton";
import Dato from "../components/Dato";
import Tarjeta from "../components/Tarjeta";
import { colores } from "../utils/colores";
import { avisoPermiso } from "../utils/permisos";
import { formatearBytes } from "../utils/formato";

// Formatos que la app sabe mostrar y procesar. Si llega otro (GIF animado, SVG…) se avisa.
const FORMATOS_VALIDOS = ["image/jpeg", "image/png", "image/webp", "image/heic", "image/heif"];

// Carpeta propia de la app (no la borra el sistema) donde se guardan las copias de las imágenes.
const carpetaGuardadas = new Directory(Paths.document, "imagenes");

// Saca el tipo MIME aunque el sistema no lo dé, mirando la extensión del fichero.
function tipoDeImagen(asset) {
    if (asset.mimeType) return asset.mimeType.toLowerCase();
    const extension = (asset.fileName || asset.uri).split(".").pop().toLowerCase();
    const porExtension = { jpg: "image/jpeg", jpeg: "image/jpeg", png: "image/png", webp: "image/webp", heic: "image/heic", gif: "image/gif" };
    return porExtension[extension] || `desconocido (.${extension})`;
}

// Tamaño en bytes: lo da el picker y, si no, se mira el fichero en disco.
function tamanoDe(asset) {
    if (asset.fileSize) return asset.fileSize;
    try {
        return new File(asset.uri).size;
    } catch (e) {
        return null;
    }
}

// Lee las imágenes guardadas en la carpeta de la app: cuántas hay y cuánto ocupan.
function leerGuardadas() {
    if (Platform.OS === "web" || !carpetaGuardadas.exists) return { numero: 0, bytes: 0 };
    const ficheros = carpetaGuardadas.list().filter((f) => f instanceof File);
    return { numero: ficheros.length, bytes: ficheros.reduce((suma, f) => suma + (f.size || 0), 0) };
}

export default function Imagenes() {
    const router = useRouter();
    const [imagen, setImagen] = useState(null); // la ficha de la imagen elegida
    const [error, setError] = useState(null); // { titulo, mensaje, reintentar, ajustes }
    const [guardadas, setGuardadas] = useState({ numero: 0, bytes: 0 });
    const [aviso, setAviso] = useState("");

    useEffect(() => {
        setGuardadas(leerGuardadas());
    }, []);

    // Pide el permiso que toque. Devuelve true si se puede seguir.
    async function pedirPermiso(origen) {
        const respuesta =
            origen === "camara"
                ? await ImagePicker.requestCameraPermissionsAsync()
                : await ImagePicker.requestMediaLibraryPermissionsAsync();

        if (respuesta.granted) return true;

        const que = origen === "camara" ? "la cámara" : "tus fotos";
        // sin "reintentar": el permiso se da desde los ajustes
        setError({ titulo: "Permiso denegado", ...avisoPermiso(que) });
        return false;
    }

    // origen: "galeria" | "camara"
    async function elegirImagen(origen) {
        setError(null);
        setAviso("");
        try {
            if (!(await pedirPermiso(origen))) return;

            const opciones = { mediaTypes: ["images"], quality: 1, exif: true };
            const resultado =
                origen === "camara"
                    ? await ImagePicker.launchCameraAsync(opciones)
                    : await ImagePicker.launchImageLibraryAsync(opciones);

            if (resultado.canceled) return; // el usuario ha salido sin elegir: no es un error

            const asset = resultado.assets[0];
            const tipo = tipoDeImagen(asset);
            if (!FORMATOS_VALIDOS.includes(tipo)) {
                setImagen(null);
                setError({
                    titulo: "Formato no compatible",
                    mensaje: `La imagen es de tipo ${tipo}. La app admite JPEG, PNG, WebP y HEIC. Elige otra.`,
                    reintentar: origen,
                });
                return;
            }

            setImagen({
                uri: asset.uri,
                nombre: asset.fileName || asset.uri.split("/").pop(),
                tipo,
                ancho: asset.width,
                alto: asset.height,
                bytes: tamanoDe(asset),
                origen: origen === "camara" ? "Cámara" : "Galería",
                fecha: asset.exif?.DateTimeOriginal || asset.exif?.DateTime || null,
                camara: [asset.exif?.Make, asset.exif?.Model].filter(Boolean).join(" ") || null,
            });
        } catch (e) {
            // p. ej. un emulador sin cámara, o el sistema no deja abrir el selector
            setError({
                titulo: origen === "camara" ? "No se ha podido abrir la cámara" : "No se ha podido abrir la galería",
                mensaje: `${e.message || e}. Si estás en un emulador, puede que no tenga cámara.`,
                reintentar: origen,
            });
        }
    }

    // Copia la imagen de la caché del picker a la carpeta propia de la app (persiste al cerrar la app).
    async function guardarCopia() {
        try {
            carpetaGuardadas.create({ idempotent: true }); // idempotent: si ya existe, no falla
            const origen = new File(imagen.uri);
            const destino = new File(carpetaGuardadas, `${Date.now()}-${imagen.nombre}`);
            await origen.copy(destino);
            setGuardadas(leerGuardadas());
            setAviso("Copia guardada en la carpeta de la app.");
        } catch (e) {
            setError({ titulo: "No se ha podido guardar", mensaje: e.message || String(e) });
        }
    }

    function borrarGuardadas() {
        try {
            if (carpetaGuardadas.exists) carpetaGuardadas.delete();
            setGuardadas(leerGuardadas());
            setAviso("Se han borrado las copias guardadas.");
        } catch (e) {
            setError({ titulo: "No se han podido borrar", mensaje: e.message || String(e) });
        }
    }

    return (
        <ScrollView contentContainerStyle={estilos.contenido}>
            <View style={estilos.fila}>
                <Boton texto="Elegir de la galería" onPress={() => elegirImagen("galeria")} estilo={estilos.flex} />
                <Boton texto="Hacer una foto" onPress={() => elegirImagen("camara")} variante="acento" estilo={estilos.flex} />
            </View>

            {error && (
                <AvisoError
                    titulo={error.titulo}
                    mensaje={error.mensaje}
                    onReintentar={error.reintentar ? () => elegirImagen(error.reintentar) : undefined}
                    irAjustes={error.ajustes}
                />
            )}

            {!imagen && !error && (
                <Text style={estilos.ayuda}>
                    Elige una imagen o haz una foto. La primera vez la app te pedirá permiso.
                </Text>
            )}

            {imagen && (
                <Tarjeta titulo="Ficha de la imagen">
                    <Image
                        source={{ uri: imagen.uri }}
                        style={[estilos.foto, { aspectRatio: imagen.ancho && imagen.alto ? imagen.ancho / imagen.alto : 1 }]}
                        resizeMode="contain"
                        onError={() =>
                            setError({ titulo: "No se puede mostrar la imagen", mensaje: "El fichero no existe o está dañado." })
                        }
                    />
                    <Dato etiqueta="Nombre" valor={imagen.nombre} />
                    <Dato etiqueta="Origen" valor={imagen.origen} />
                    <Dato etiqueta="Formato" valor={imagen.tipo} />
                    <Dato etiqueta="Dimensiones" valor={`${imagen.ancho} × ${imagen.alto} px`} />
                    <Dato etiqueta="Megapíxeles" valor={((imagen.ancho * imagen.alto) / 1e6).toFixed(1).replace(".", ",")} />
                    <Dato etiqueta="Tamaño" valor={formatearBytes(imagen.bytes)} />
                    {imagen.fecha && <Dato etiqueta="Fecha (EXIF)" valor={imagen.fecha} />}
                    {imagen.camara && <Dato etiqueta="Cámara (EXIF)" valor={imagen.camara} />}

                    <Boton
                        texto="Procesar esta imagen"
                        onPress={() =>
                            router.push({
                                pathname: "/procesado",
                                params: { uri: imagen.uri, ancho: imagen.ancho, alto: imagen.alto, bytes: imagen.bytes ?? "" },
                            })
                        }
                    />
                    {Platform.OS !== "web" && <Boton texto="Guardar copia en la app" onPress={guardarCopia} variante="secundario" />}
                </Tarjeta>
            )}

            {aviso ? <Text style={estilos.ok}>{aviso}</Text> : null}

            {Platform.OS !== "web" && (
                <Tarjeta titulo="Ficheros de la app">
                    <Text style={estilos.ayuda}>
                        Las imágenes elegidas viven en la caché (el sistema puede borrarlas). Las copias guardadas van a la
                        carpeta de documentos de la app.
                    </Text>
                    <Dato etiqueta="Copias guardadas" valor={guardadas.numero} />
                    <Dato etiqueta="Espacio ocupado" valor={formatearBytes(guardadas.bytes)} />
                    <Boton texto="Borrar copias guardadas" onPress={borrarGuardadas} variante="secundario" disabled={guardadas.numero === 0} />
                </Tarjeta>
            )}
        </ScrollView>
    );
}

const estilos = StyleSheet.create({
    contenido: { padding: 16, gap: 14 },
    fila: { flexDirection: "row", gap: 10 },
    flex: { flex: 1 },
    ayuda: { color: colores.suave, fontSize: 14, lineHeight: 20 },
    foto: { width: "100%", maxHeight: 320, borderRadius: 10, backgroundColor: "#e9e5dc" },
    ok: { color: colores.ok, fontWeight: "600" },
});
