import * as Location from "expo-location";
import { useState } from "react";
import { ActivityIndicator, Linking, Platform, Pressable, ScrollView, StyleSheet, Text, View } from "react-native";

// Intenta convertir las coordenadas en una dirección legible (calle, ciudad…).
// Si el dispositivo no puede (sin internet, web…), devuelve "" y solo se enseñan las coordenadas.
async function nombreDelLugar(coords) {
    try {
        const [direccion] = await Location.reverseGeocodeAsync({
            latitude: coords.latitude,
            longitude: coords.longitude,
        });
        if (!direccion) return "";
        return [direccion.street, direccion.city, direccion.region, direccion.country].filter(Boolean).join(", ");
    } catch (e) {
        return "";
    }
}

export default function Ubicacion() {
    // en qué situación está la pantalla: "inicial" | "cargando" | "ok" | "denegado" | "error"
    const [estado, setEstado] = useState("inicial");
    const [coords, setCoords] = useState(null);
    const [lugar, setLugar] = useState("");

    async function usarUbicacion() {
        setEstado("cargando");
        try {
            // 1. se pide permiso: el sistema muestra su diálogo y la función espera la respuesta
            const { status } = await Location.requestForegroundPermissionsAsync();
            if (status !== "granted") {
                setEstado("denegado"); // sin permiso: se avisa y no se sigue, pero la app no se rompe
                return;
            }
            // 2. con permiso: se pide la posición actual
            const posicion = await Location.getCurrentPositionAsync({ accuracy: Location.Accuracy.Balanced });
            setCoords(posicion.coords);
            setLugar(await nombreDelLugar(posicion.coords));
            setEstado("ok");
        } catch (e) {
            setEstado("error"); // GPS apagado, sin señal, tiempo agotado…
        }
    }

    return (
        <ScrollView style={estilos.pantalla} contentContainerStyle={estilos.contenido}>
            <Pressable
                onPress={usarUbicacion}
                disabled={estado === "cargando"}
                style={({ pressed }) => [
                    estilos.boton,
                    estado === "cargando" && estilos.desactivado,
                    pressed && estilos.pulsado,
                ]}
            >
                <Text style={estilos.textoBoton}>Utilizar mi ubicación</Text>
            </Pressable>

            {estado === "inicial" && (
                <Text style={estilos.aviso}>Pulsa el botón para ver dónde estás. La app te pedirá permiso.</Text>
            )}

            {estado === "cargando" && <ActivityIndicator size="large" color="#234c3b" style={estilos.cargando} />}

            {estado === "ok" && coords && (
                <View style={estilos.tarjeta}>
                    <Text style={estilos.etiqueta}>Ubicación</Text>
                    <Text style={estilos.lugar}>{lugar || "Nombre del lugar no disponible"}</Text>

                    <Text style={estilos.etiqueta}>Coordenadas</Text>
                    <Text style={estilos.dato}>Latitud: {coords.latitude.toFixed(5)}</Text>
                    <Text style={estilos.dato}>Longitud: {coords.longitude.toFixed(5)}</Text>
                    {coords.accuracy != null && (
                        <Text style={estilos.precision}>Precisión: ±{Math.round(coords.accuracy)} m</Text>
                    )}
                </View>
            )}

            {estado === "denegado" && (
                <View style={estilos.tarjeta}>
                    <Text style={estilos.textoError}>
                        Permiso de ubicación denegado. Puedes seguir usando el resto de la app sin ubicación.
                    </Text>
                    {/* en el móvil se puede ir a los ajustes para dar el permiso; en la web no existe */}
                    {Platform.OS !== "web" && (
                        <Pressable
                            onPress={() => Linking.openSettings()}
                            style={({ pressed }) => [estilos.botonSecundario, pressed && estilos.pulsado]}
                        >
                            <Text style={estilos.textoSecundario}>Abrir ajustes</Text>
                        </Pressable>
                    )}
                </View>
            )}

            {estado === "error" && (
                <View style={estilos.tarjeta}>
                    <Text style={estilos.textoError}>
                        No se pudo obtener la ubicación. Comprueba que el GPS está activado e inténtalo de nuevo.
                    </Text>
                </View>
            )}
        </ScrollView>
    );
}

const estilos = StyleSheet.create({
    pantalla: {
        flex: 1,
        backgroundColor: "#f7f4ed",
    },
    contenido: {
        padding: 16,
        gap: 16,
    },
    boton: {
        minHeight: 56,
        borderRadius: 16,
        backgroundColor: "#234c3b",
        alignItems: "center",
        justifyContent: "center",
        shadowColor: "#000000",
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.25,
        shadowRadius: 4,
        elevation: 4,
    },
    desactivado: {
        opacity: 0.5,
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
    aviso: {
        fontSize: 16,
        color: "#555555",
        textAlign: "center",
    },
    cargando: {
        marginTop: 16,
    },
    tarjeta: {
        backgroundColor: "#ffffff",
        borderRadius: 16,
        padding: 20,
        gap: 6,
        shadowColor: "#000000",
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.15,
        shadowRadius: 4,
        elevation: 2,
    },
    etiqueta: {
        marginTop: 6,
        fontSize: 12,
        fontWeight: "bold",
        color: "#e07a2f",
        textTransform: "uppercase",
        letterSpacing: 1,
    },
    lugar: {
        fontSize: 20,
        fontWeight: "bold",
        color: "#234c3b",
    },
    dato: {
        fontSize: 18,
        color: "#333333",
    },
    precision: {
        fontSize: 14,
        color: "#777777",
    },
    textoError: {
        fontSize: 16,
        color: "#c0392b",
    },
    botonSecundario: {
        marginTop: 8,
        minHeight: 48,
        borderRadius: 12,
        borderWidth: 2,
        borderColor: "#234c3b",
        alignItems: "center",
        justifyContent: "center",
    },
    textoSecundario: {
        color: "#234c3b",
        fontSize: 16,
        fontWeight: "bold",
    },
});
