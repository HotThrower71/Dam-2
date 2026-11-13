import { router } from "expo-router";
import { useState } from "react";
import { KeyboardAvoidingView, Platform, Pressable, ScrollView, StyleSheet, Text, TextInput, View } from "react-native";

import { readPlaces, savePlaces } from "../storage/placesStorage";

// colores entre los que se puede elegir
const COLORES = ["#111111", "#1e63d6", "#3aa564", "#d62828", "#8e44ad", "#16a0b5", "#e84393", "#7f8c8d"];

export default function Nuevo() {
    const [titulo, setTitulo] = useState("");
    const [subtitulo, setSubtitulo] = useState("");
    const [descripcion, setDescripcion] = useState("");
    const [color, setColor] = useState(COLORES[0]); // el elegido (empieza en el primero)
    const [error, setError] = useState("");

    async function guardar() {
        if (titulo.trim() === "") {
            setError("El título está vacío");
            return;
        }
        setError("");
        const lista = await readPlaces();
        const nuevo = {
            id: Date.now().toString(),
            titulo: titulo.trim(),
            subtitulo: subtitulo.trim(),
            descripcion,
            color,
        };
        await savePlaces([...lista, nuevo]); // copia de la lista + el nuevo
        router.back();
    }

    return (
        // evita que el teclado tape los campos y el botón
        <KeyboardAvoidingView style={estilos.pantalla} behavior={Platform.OS === "ios" ? "padding" : "height"}>
            {/* keyboardShouldPersistTaps: el botón responde al primer toque aunque el teclado esté abierto */}
            <ScrollView contentContainerStyle={estilos.contenido} keyboardShouldPersistTaps="handled">
                <Text style={estilos.etiqueta}>Título</Text>
                <TextInput
                    style={[estilos.input, error !== "" && estilos.inputError]}
                    value={titulo}
                    onChangeText={setTitulo}
                    placeholder="Título"
                    placeholderTextColor="#999999"
                />
                {error !== "" && <Text style={estilos.error}>{error}</Text>}

                <Text style={estilos.etiqueta}>Subtítulo</Text>
                <TextInput
                    style={estilos.input}
                    value={subtitulo}
                    onChangeText={setSubtitulo}
                    placeholder="Subtítulo (opcional)"
                    placeholderTextColor="#999999"
                />

                <Text style={estilos.etiqueta}>Descripción</Text>
                <TextInput
                    style={[estilos.input, estilos.multilinea]}
                    value={descripcion}
                    onChangeText={setDescripcion}
                    placeholder="Descripción"
                    placeholderTextColor="#999999"
                    multiline
                />

                <Text style={estilos.etiqueta}>Color</Text>
                <View style={estilos.colores}>
                    {COLORES.map(c => (
                        <Pressable
                            key={c}
                            onPress={() => setColor(c)}
                            accessibilityLabel={`Color ${c}`}
                            style={[estilos.muestra, { backgroundColor: c }, c === color && estilos.seleccionada]}
                        />
                    ))}
                </View>

                <Pressable
                    onPress={guardar}
                    style={({ pressed }) => [estilos.boton, pressed && estilos.pulsado]}
                >
                    <Text style={estilos.textoBoton}>Guardar</Text>
                </Pressable>
            </ScrollView>
        </KeyboardAvoidingView>
    );
}

const estilos = StyleSheet.create({
    pantalla: {
        flex: 1,
        backgroundColor: "#f7f4ed",
    },
    contenido: {
        // margen con los bordes del móvil y separación entre los elementos
        padding: 16,
        gap: 8,
    },
    etiqueta: {
        marginTop: 8,
        fontSize: 12,
        fontWeight: "bold",
        color: "#e07a2f",
        textTransform: "uppercase",
        letterSpacing: 1,
    },
    input: {
        backgroundColor: "#ffffff",
        borderWidth: 1,
        borderColor: "#cccccc",
        borderRadius: 12,
        padding: 14,
        fontSize: 16, // 16 o más: en iOS evita que la pantalla haga zoom al escribir
        color: "#222222",
    },
    inputError: {
        borderColor: "#c0392b",
    },
    multilinea: {
        minHeight: 120,
        textAlignVertical: "top", // Android: que el texto empiece arriba
    },
    error: {
        color: "#c0392b",
        fontSize: 14,
    },
    colores: {
        // las muestras van en fila y saltan a la siguiente línea si no caben
        flexDirection: "row",
        flexWrap: "wrap",
        gap: 12,
    },
    muestra: {
        width: 48,
        height: 48, // zona táctil cómoda
        borderRadius: 24, // la mitad del tamaño = círculo
        borderWidth: 3,
        borderColor: "transparent",
    },
    seleccionada: {
        borderColor: "#e07a2f", // aro naranja alrededor del elegido
        transform: [{ scale: 1.15 }],
    },
    boton: {
        marginTop: 16,
        minHeight: 56,
        borderRadius: 16,
        backgroundColor: "#234c3b",
        alignItems: "center",
        justifyContent: "center",
        // misma sombra que el resto de tarjetas y botones
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
    textoBoton: {
        color: "#ffffff",
        fontSize: 18,
        fontWeight: "bold",
    },
});
