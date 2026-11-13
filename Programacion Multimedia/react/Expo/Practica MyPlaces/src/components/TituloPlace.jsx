import { StyleSheet, Text, View } from "react-native";

export default function TituloPlace({ titulo }) {
    return (
        <View style={estilos.contenedor}>
            <Text style={estilos.titulo}>{titulo}</Text>
            <View style={estilos.linea} />
        </View>
    );
}

const estilos = StyleSheet.create({
    contenedor: {
        // mismo margen lateral que la lista de botones, para que queden alineados
        marginHorizontal: 16,
        marginTop: 16,
        paddingVertical: 20,
        paddingHorizontal: 20,
        borderRadius: 16,
        backgroundColor: "#234c3b",
        // centra el texto y la rayita en horizontal
        alignItems: "center",
        // misma sombra que los botones (iOS: shadow*, Android: elevation)
        shadowColor: "#000000",
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.25,
        shadowRadius: 4,
        elevation: 4,
    },
    titulo: {
        fontSize: 28,
        fontWeight: "bold",
        color: "#ffffff",
        textAlign: "center",
        letterSpacing: 1,
        textTransform: "uppercase",
    },
    linea: {
        marginTop: 10,
        width: 48,
        height: 4,
        borderRadius: 2,
        backgroundColor: "#e07a2f",
    },
});
