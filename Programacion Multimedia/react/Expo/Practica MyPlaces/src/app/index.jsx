import { router } from "expo-router";
import { StyleSheet, View } from "react-native";

import BotonPlaces from "../components/BotonPlaces";
import TituloPlace from "../components/TituloPlace";

import { places } from "../data/places";

export default function Inicio() {
    return (
        <View>
            <View>
                <TituloPlace titulo="Menu" />
            </View>
            <View style={estilos.lista}>
                {places.map(l => (
                    <BotonPlaces
                        key={l.id}
                        nombre={l.nombre}
                        coordenadas={l.coordenadas}
                        color={l.color}
                        onPress={() => router.push({ pathname: "/place/[id]", params: { id: l.id } })}
                    />
                ))}
            </View>
        </View>
    );
}

const estilos = StyleSheet.create({
    lista: {
        padding: 16,
        gap: 12,
    },
});
