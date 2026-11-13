import { StyleSheet, Text, View } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";

import useRed from "../hooks/useRed";

// nombre legible de cada tipo de red que devuelve NetInfo
const TIPOS = {
    wifi: "Wi-Fi",
    cellular: "Datos móviles",
    ethernet: "Ethernet",
    bluetooth: "Bluetooth",
    vpn: "VPN",
    other: "Otra red",
    unknown: "Red desconocida",
};

// Barra que muestra el estado de la conexión y el tipo de red.
export default function BannerRed() {
    const { conectado, tipo } = useRed();
    const insets = useSafeAreaInsets(); // espacio que ocupan los gestos o la barra de abajo del móvil

    return (
        <View
            style={[
                estilos.banner,
                conectado ? estilos.conectado : estilos.sinConexion,
                { paddingBottom: 8 + insets.bottom },
            ]}
        >
            <Text style={[estilos.texto, conectado ? estilos.textoConectado : estilos.textoSinConexion]}>
                {conectado ? `Conectado · ${TIPOS[tipo] ?? "Red desconocida"}` : "Sin conexión a internet"}
            </Text>
        </View>
    );
}

const estilos = StyleSheet.create({
    banner: {
        paddingTop: 8,
        paddingHorizontal: 16,
        alignItems: "center",
    },
    conectado: {
        backgroundColor: "#d8f0e0",
    },
    sinConexion: {
        backgroundColor: "#f6d5d5",
    },
    texto: {
        fontSize: 14,
        fontWeight: "bold",
    },
    textoConectado: {
        color: "#1d5e36",
    },
    textoSinConexion: {
        color: "#8a2323",
    },
});
