import { router, Stack, useLocalSearchParams } from "expo-router";
import { useEffect, useState } from "react";
import { Alert, Platform, Pressable, ScrollView, StyleSheet, Text, View } from "react-native";

import { deletePlace, readPlaces } from "../../storage/placesStorage";

export default function Place() {
    const { id } = useLocalSearchParams(); //preguntar Miquel
    const [lista, setLista] = useState([]); // empieza vacía y se rellena al leer
    const [cargando, setCargando] = useState(true);

    // lee todos los lugares guardados
    useEffect(() => {
        readPlaces().then(l => {
            setLista(l);
            setCargando(false);
        });
    }, []);

    const place = lista.find(y => y.id === id); //Busca en el map cuando el id de alli coinicda con el de aqui

    // mientras carga no se avisa de "no encontrado"
    if (!place) return cargando ? null : <Text style={estilos.noEncontrado}>Lugar no encontrado </Text>;

    async function borrar() {
        await deletePlace(id);
        router.back(); // vuelve al menú, que se vuelve a leer y ya no lo muestra
    }

    function confirmarBorrado() {
        if (Platform.OS === "web") {
            // en la web Alert.alert no muestra botones, se usa el confirm del navegador
            if (window.confirm("¿Eliminar este lugar?")) borrar();
            return;
        }
        Alert.alert("Eliminar lugar", `¿Seguro que quieres eliminar "${place.titulo}"?`, [
            { text: "Cancelar", style: "cancel" },
            { text: "Eliminar", style: "destructive", onPress: borrar },
        ]);
    }

    return (
        <ScrollView style={estilos.pantalla} contentContainerStyle={estilos.contenido}>
            {/* la barra de arriba toma el título y el color del lugar */}
            <Stack.Screen
                options={{
                    title: place.titulo,
                    headerStyle: { backgroundColor: place.color },
                    headerTintColor: "#ffffff",
                }}
            />

            {/* cabecera: tarjeta del color del lugar, como su botón de la lista */}
            <View style={[estilos.cabecera, { backgroundColor: place.color }]}>
                <Text style={estilos.titulo}>{place.titulo}</Text>
                <Text style={estilos.subtitulo}>{place.subtitulo}</Text>
            </View>

            {/* tarjeta blanca con la descripción */}
            <View style={estilos.tarjeta}>
                <Text style={estilos.etiqueta}>Descripción</Text>
                <Text style={estilos.descripcion}>{place.descripcion}</Text>
            </View>

            {/* ahora aparece en todos los lugares */}
            <Pressable onPress={confirmarBorrado} style={({ pressed }) => [estilos.botonEliminar, pressed && estilos.pulsado]}>
                <Text style={estilos.textoEliminar}>Eliminar lugar</Text>
            </Pressable>
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
    titulo: {
        fontSize: 32,
        fontWeight: "bold",
        color: "#ffffff",
        textAlign: "center",
        letterSpacing: 0.5,
    },
    subtitulo: {
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
    botonEliminar: {
        minHeight: 56,
        borderRadius: 16,
        backgroundColor: "#c0392b",
        alignItems: "center",
        justifyContent: "center",
        // misma sombra que el resto de botones
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
    textoEliminar: {
        color: "#ffffff",
        fontSize: 18,
        fontWeight: "bold",
    },
    noEncontrado: {
        padding: 24,
        fontSize: 18,
        textAlign: "center",
    },
});
