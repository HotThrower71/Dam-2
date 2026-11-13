import { Pressable, StyleSheet, Text, View } from "react-native";

export default function BotonPlaces({ nombre, coordenadas, color, onPress }) {
    return (
        <Pressable
            onPress={onPress}
            style={({ pressed }) => [estilos.boton, { backgroundColor: color }, pressed && estilos.pulsado]}
        >
            <View>
                <Text style={estilos.nombre}>{nombre}</Text>
            </View>
            <View>
                <Text style={estilos.coordenadas}>{coordenadas}</Text>
            </View>
        </Pressable>
    );
}

const estilos = StyleSheet.create({
    boton: {
        minHeight: 72,
        paddingVertical: 16,
        paddingHorizontal: 20,
        borderRadius: 16,
        justifyContent: "center",
        gap: 4,
        // sombra: iOS usa shadow*, Android usa elevation
        shadowColor: "#000000",
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.25,
        shadowRadius: 4,
        elevation: 4,
    },
    pulsado: {
        opacity: 0.8,
        transform: [{ scale: 0.98 }],
    },
    nombre: {
        fontSize: 22,
        fontWeight: "bold",
        color: "#ffffff",
    },
    coordenadas: {
        fontSize: 14,
        color: "rgba(255, 255, 255, 0.8)",
    },
});
