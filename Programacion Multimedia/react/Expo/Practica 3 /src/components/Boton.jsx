import { ActivityIndicator, Pressable, StyleSheet, Text } from "react-native";

import { colores } from "../utils/colores";

// Botón común de la app.
// variante: "primario" (relleno) | "secundario" (solo borde) | "peligro" (rojo) | "acento" (naranja)
export default function Boton({ texto, onPress, variante = "primario", disabled = false, cargando = false, estilo }) {
    const desactivado = disabled || cargando;
    return (
        <Pressable
            onPress={onPress}
            disabled={desactivado}
            accessibilityRole="button"
            style={({ pressed }) => [
                estilos.base,
                estilos[variante],
                desactivado && estilos.desactivado,
                pressed && estilos.pulsado,
                estilo,
            ]}
        >
            {cargando ? (
                <ActivityIndicator color={variante === "secundario" ? colores.principal : "#ffffff"} />
            ) : (
                <Text style={[estilos.texto, variante === "secundario" && estilos.textoSecundario]}>{texto}</Text>
            )}
        </Pressable>
    );
}

const estilos = StyleSheet.create({
    base: {
        minHeight: 46,
        paddingHorizontal: 16,
        paddingVertical: 10,
        borderRadius: 12,
        alignItems: "center",
        justifyContent: "center",
    },
    primario: { backgroundColor: colores.principal },
    acento: { backgroundColor: colores.acento },
    peligro: { backgroundColor: colores.error },
    secundario: { backgroundColor: "transparent", borderWidth: 1.5, borderColor: colores.principal },
    desactivado: { opacity: 0.45 },
    pulsado: { opacity: 0.8, transform: [{ scale: 0.98 }] },
    texto: { color: "#ffffff", fontSize: 15, fontWeight: "bold" },
    textoSecundario: { color: colores.principal },
});
