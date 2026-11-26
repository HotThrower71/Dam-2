import { useRouter } from "expo-router";
import { Pressable, ScrollView, StyleSheet, Text, View } from "react-native";

import { colores } from "../utils/colores";

// Cada entrada del menú: a qué ruta va y qué funcionalidad del enunciado cubre.
const SECCIONES = [
    { ruta: "/imagenes", codigo: "F1", titulo: "Imágenes", texto: "Galería, cámara, permisos y ficha de la imagen" },
    { ruta: "/procesado", codigo: "F2", titulo: "Procesado", texto: "Redimensionar, recortar y convertir a JPEG, PNG y WebP" },
    { ruta: "/audio", codigo: "F3", titulo: "Notas de voz", texto: "Grabar, listar y reproducir" },
    { ruta: "/video", codigo: "F4", titulo: "Vídeo", texto: "Local, remoto y HLS con controles propios" },
    { ruta: "/animaciones", codigo: "F5", titulo: "Animaciones", texto: "Dos animaciones con inicio, final y limpieza" },
];

export default function Menu() {
    const router = useRouter();

    return (
        <ScrollView contentContainerStyle={estilos.contenido}>
            <Text style={estilos.intro}>Elige qué quieres probar:</Text>
            {SECCIONES.map((s) => (
                <Pressable
                    key={s.ruta}
                    onPress={() => router.push(s.ruta)}
                    style={({ pressed }) => [estilos.opcion, pressed && estilos.pulsado]}
                >
                    <View style={estilos.codigo}>
                        <Text style={estilos.textoCodigo}>{s.codigo}</Text>
                    </View>
                    <View style={estilos.textos}>
                        <Text style={estilos.titulo}>{s.titulo}</Text>
                        <Text style={estilos.subtitulo}>{s.texto}</Text>
                    </View>
                    <Text style={estilos.flecha}>›</Text>
                </Pressable>
            ))}
        </ScrollView>
    );
}

const estilos = StyleSheet.create({
    contenido: { padding: 16, gap: 12 },
    intro: { color: colores.suave, fontSize: 15, marginBottom: 4 },
    opcion: {
        flexDirection: "row",
        alignItems: "center",
        gap: 14,
        backgroundColor: colores.tarjeta,
        borderRadius: 16,
        padding: 16,
        borderWidth: 1,
        borderColor: colores.borde,
    },
    pulsado: { opacity: 0.8, transform: [{ scale: 0.99 }] },
    codigo: {
        width: 44,
        height: 44,
        borderRadius: 12,
        backgroundColor: colores.principal,
        alignItems: "center",
        justifyContent: "center",
    },
    textoCodigo: { color: "#ffffff", fontWeight: "bold", fontSize: 15 },
    textos: { flex: 1, gap: 2 },
    titulo: { fontSize: 18, fontWeight: "bold", color: colores.texto },
    subtitulo: { fontSize: 13, color: colores.suave },
    flecha: { fontSize: 28, color: colores.suave },
});
