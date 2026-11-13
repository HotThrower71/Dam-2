import { Stack, useLocalSearchParams } from "expo-router";
import { ScrollView, StyleSheet, Text, View } from "react-native";

import { places } from "../../data/places";

export default function Place() {
    const { id } = useLocalSearchParams(); //preguntar Miquel
    const place = places.find(y => y.id === id); //Busca en el map cuando el id de alli coinicda con el de aqui

    if (!place) return <Text style={estilos.noEncontrado}>Lugar no encontrado </Text>;

    return (
        <ScrollView style={estilos.pantalla} contentContainerStyle={estilos.contenido}>
            {/* la barra de arriba toma el nombre y el color del lugar */}
            <Stack.Screen
                options={{
                    title: place.nombre,
                    headerStyle: { backgroundColor: place.color },
                    headerTintColor: "#ffffff",
                }}
            />

            {/* cabecera: tarjeta del color del lugar, como su botón de la lista */}
            <View style={[estilos.cabecera, { backgroundColor: place.color }]}>
                <Text style={estilos.nombre}>{place.nombre}</Text>
                <Text style={estilos.coordenadas}>{place.coordenadas}</Text>
            </View>

            {/* tarjeta blanca con la descripción */}
            <View style={estilos.tarjeta}>
                <Text style={estilos.etiqueta}>Descripción</Text>
                <Text style={estilos.descripcion}>{place.descripcion}</Text>
            </View>
        </ScrollView>
    );
}

const estilos = StyleSheet.create({
    pantalla: {
        flex: 1,
        backgroundColor: "#f7f4ed",
    },
    contenido: {
        // margen respecto a los bordes del móvil y separación entre las dos tarjetas
        padding: 16,
        gap: 16,
    },
    cabecera: {
        borderRadius: 16,
        paddingVertical: 32,
        paddingHorizontal: 20,
        alignItems: "center",
        gap: 6,
        // misma sombra que los botones (iOS: shadow*, Android: elevation)
        shadowColor: "#000000",
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.25,
        shadowRadius: 4,
        elevation: 4,
    },
    nombre: {
        fontSize: 32,
        fontWeight: "bold",
        color: "#ffffff",
        textAlign: "center",
        letterSpacing: 0.5,
    },
    coordenadas: {
        fontSize: 14,
        color: "rgba(255, 255, 255, 0.8)",
    },
    tarjeta: {
        backgroundColor: "#ffffff",
        borderRadius: 16,
        padding: 20,
        gap: 8,
        shadowColor: "#000000",
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.15,
        shadowRadius: 4,
        elevation: 2,
    },
    etiqueta: {
        fontSize: 12,
        fontWeight: "bold",
        color: "#e07a2f",
        textTransform: "uppercase",
        letterSpacing: 1,
    },
    descripcion: {
        fontSize: 18,
        lineHeight: 26,
        color: "#333333",
    },
    noEncontrado: {
        padding: 24,
        fontSize: 18,
        textAlign: "center",
    },
});
