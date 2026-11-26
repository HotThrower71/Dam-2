import { StyleSheet, Text, View } from "react-native";

import { colores } from "../utils/colores";

// Una fila "etiqueta ........ valor" para las fichas de datos.
export default function Dato({ etiqueta, valor }) {
    return (
        <View style={estilos.fila}>
            <Text style={estilos.etiqueta}>{etiqueta}</Text>
            <Text style={estilos.valor} selectable>
                {valor ?? "—"}
            </Text>
        </View>
    );
}

const estilos = StyleSheet.create({
    fila: {
        flexDirection: "row",
        justifyContent: "space-between",
        gap: 12,
        paddingVertical: 4,
        borderBottomWidth: StyleSheet.hairlineWidth,
        borderBottomColor: colores.borde,
    },
    etiqueta: { color: colores.suave, fontSize: 14 },
    valor: { color: colores.texto, fontSize: 14, fontWeight: "600", flexShrink: 1, textAlign: "right" },
});
