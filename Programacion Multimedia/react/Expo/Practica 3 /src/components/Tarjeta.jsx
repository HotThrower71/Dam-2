import { StyleSheet, Text, View } from "react-native";

import { colores } from "../utils/colores";

// Caja blanca con un título opcional. Agrupa cada bloque de cada pantalla.
export default function Tarjeta({ titulo, children, estilo }) {
    return (
        <View style={[estilos.tarjeta, estilo]}>
            {titulo ? <Text style={estilos.titulo}>{titulo}</Text> : null}
            {children}
        </View>
    );
}

const estilos = StyleSheet.create({
    tarjeta: {
        backgroundColor: colores.tarjeta,
        borderRadius: 16,
        padding: 16,
        gap: 10,
        borderWidth: 1,
        borderColor: colores.borde,
    },
    titulo: { fontSize: 17, fontWeight: "bold", color: colores.texto },
});
