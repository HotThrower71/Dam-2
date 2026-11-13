import { useState } from "react";
import {
    ActivityIndicator,
    FlatList,
    Keyboard,
    KeyboardAvoidingView,
    Platform,
    Pressable,
    StyleSheet,
    Text,
    TextInput,
    View,
} from "react-native";

// API pública: Open-Meteo Geocoding (busca lugares por nombre, sin clave)
const URL_API = "https://geocoding-api.open-meteo.com/v1/search";

export default function Descubre() {
    const [texto, setTexto] = useState("");
    // en qué situación está la pantalla: "inicial" | "cargando" | "error" | "ok"
    const [estado, setEstado] = useState("inicial");
    const [resultados, setResultados] = useState([]);

    // la API pide al menos 2 letras para buscar
    const puedeBuscar = texto.trim().length >= 2 && estado !== "cargando";

    async function buscar() {
        Keyboard.dismiss();
        setEstado("cargando");
        try {
            const url = `${URL_API}?name=${encodeURIComponent(texto.trim())}&count=10&language=es&format=json`;
            const res = await fetch(url);
            if (!res.ok) throw new Error("Respuesta " + res.status); // fetch no falla solo con un 400 o un 500
            const datos = await res.json();
            setResultados(datos.results ?? []); // si no hay resultados, la API no envía "results"
            setEstado("ok");
        } catch (e) {
            setEstado("error"); // sin internet, servidor caído, respuesta mala...
        }
    }

    // elige qué se pinta debajo del buscador según el estado
    function contenido() {
        if (estado === "inicial") {
            return <Text style={estilos.aviso}>Escribe una ciudad o un lugar y pulsa Buscar.</Text>;
        }
        if (estado === "cargando") {
            return <ActivityIndicator size="large" color="#234c3b" style={estilos.cargando} />;
        }
        if (estado === "error") {
            return (
                <View style={estilos.centro}>
                    <Text style={estilos.textoError}>No se pudo cargar. Revisa tu conexión.</Text>
                    <Pressable onPress={buscar} style={({ pressed }) => [estilos.boton, pressed && estilos.pulsado]}>
                        <Text style={estilos.textoBoton}>Reintentar</Text>
                    </Pressable>
                </View>
            );
        }
        if (resultados.length === 0) {
            return <Text style={estilos.aviso}>No hay resultados para «{texto.trim()}».</Text>;
        }
        return (
            <FlatList
                data={resultados}
                keyExtractor={item => String(item.id)}
                contentContainerStyle={estilos.lista}
                keyboardShouldPersistTaps="handled"
                renderItem={({ item }) => (
                    <View style={estilos.tarjeta}>
                        <Text style={estilos.nombre}>{item.name}</Text>
                        <Text style={estilos.zona}>{[item.admin1, item.country].filter(Boolean).join(", ")}</Text>
                        <Text style={estilos.coordenadas}>
                            {item.latitude.toFixed(2)}, {item.longitude.toFixed(2)}
                        </Text>
                    </View>
                )}
            />
        );
    }

    return (
        <KeyboardAvoidingView style={estilos.pantalla} behavior={Platform.OS === "ios" ? "padding" : undefined}>
            <View style={estilos.buscador}>
                <TextInput
                    style={estilos.input}
                    value={texto}
                    onChangeText={setTexto}
                    onSubmitEditing={() => puedeBuscar && buscar()}
                    placeholder="Ciudad o lugar (mín. 2 letras)"
                    placeholderTextColor="#999999"
                    returnKeyType="search"
                />
                <Pressable
                    onPress={buscar}
                    disabled={!puedeBuscar}
                    style={({ pressed }) => [
                        estilos.boton,
                        !puedeBuscar && estilos.desactivado,
                        pressed && estilos.pulsado,
                    ]}
                >
                    <Text style={estilos.textoBoton}>Buscar</Text>
                </Pressable>
            </View>

            <View style={estilos.resultados}>{contenido()}</View>
        </KeyboardAvoidingView>
    );
}

const estilos = StyleSheet.create({
    pantalla: {
        flex: 1,
        backgroundColor: "#f7f4ed",
    },
    buscador: {
        // campo y botón en la misma fila
        flexDirection: "row",
        gap: 8,
        padding: 16,
    },
    input: {
        flex: 1, // el campo ocupa el espacio que deja el botón
        backgroundColor: "#ffffff",
        borderWidth: 1,
        borderColor: "#cccccc",
        borderRadius: 12,
        paddingHorizontal: 14,
        fontSize: 16,
        color: "#222222",
    },
    boton: {
        minHeight: 52,
        paddingHorizontal: 20,
        borderRadius: 12,
        backgroundColor: "#234c3b",
        alignItems: "center",
        justifyContent: "center",
    },
    desactivado: {
        opacity: 0.4,
    },
    pulsado: {
        opacity: 0.8,
        transform: [{ scale: 0.98 }],
    },
    textoBoton: {
        color: "#ffffff",
        fontSize: 16,
        fontWeight: "bold",
    },
    resultados: {
        flex: 1,
    },
    aviso: {
        padding: 24,
        fontSize: 16,
        color: "#555555",
        textAlign: "center",
    },
    cargando: {
        marginTop: 40,
    },
    centro: {
        padding: 24,
        gap: 16,
        alignItems: "center",
    },
    textoError: {
        fontSize: 16,
        color: "#c0392b",
        textAlign: "center",
    },
    lista: {
        padding: 16,
        gap: 12,
    },
    tarjeta: {
        backgroundColor: "#ffffff",
        borderRadius: 16,
        padding: 16,
        gap: 4,
        shadowColor: "#000000",
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.15,
        shadowRadius: 4,
        elevation: 2,
    },
    nombre: {
        fontSize: 20,
        fontWeight: "bold",
        color: "#234c3b",
    },
    zona: {
        fontSize: 15,
        color: "#444444",
    },
    coordenadas: {
        fontSize: 13,
        color: "#e07a2f",
    },
});
