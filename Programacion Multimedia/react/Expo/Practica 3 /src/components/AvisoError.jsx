import { Linking, StyleSheet, Text, View } from "react-native";

import { colores } from "../utils/colores";
import Boton from "./Boton";

// Mensaje de error comprensible con, si tiene sentido, una acción para reintentar.
// - onReintentar: si llega, se enseña el botón "Reintentar".
// - irAjustes: si es true, se ofrece abrir los ajustes del sistema (para permisos denegados para siempre).
export default function AvisoError({ titulo = "Algo ha fallado", mensaje, onReintentar, textoReintentar = "Reintentar", irAjustes = false }) {
    return (
        <View style={estilos.caja} accessibilityRole="alert">
            <Text style={estilos.titulo}>{titulo}</Text>
            {mensaje ? <Text style={estilos.mensaje}>{mensaje}</Text> : null}
            <View style={estilos.botones}>
                {onReintentar ? <Boton texto={textoReintentar} onPress={onReintentar} variante="peligro" estilo={estilos.boton} /> : null}
                {irAjustes ? (
                    <Boton texto="Abrir ajustes" onPress={() => Linking.openSettings()} variante="secundario" estilo={estilos.boton} />
                ) : null}
            </View>
        </View>
    );
}

const estilos = StyleSheet.create({
    caja: {
        backgroundColor: colores.errorFondo,
        borderLeftWidth: 4,
        borderLeftColor: colores.error,
        borderRadius: 12,
        padding: 14,
        gap: 6,
    },
    titulo: { color: colores.error, fontWeight: "bold", fontSize: 15 },
    mensaje: { color: colores.texto, fontSize: 14, lineHeight: 20 },
    botones: { flexDirection: "row", flexWrap: "wrap", gap: 8, marginTop: 4 },
    boton: { minHeight: 40 },
});
