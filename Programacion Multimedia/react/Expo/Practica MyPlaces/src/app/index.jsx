import { router, useFocusEffect } from "expo-router";
import { useCallback, useState } from "react";
import { ScrollView, StyleSheet, View } from "react-native";

import BotonPlaces from "../components/BotonPlaces";
import TituloPlace from "../components/TituloPlace";

import { readPlaces } from "../storage/placesStorage";

export default function Inicio() {
    const [todos, setTodos] = useState([]); // empieza vacío y se rellena al leer

    // cada vez que esta pantalla vuelve a verse (por ejemplo tras crear o borrar un lugar), se vuelve a leer
    useFocusEffect(
        useCallback(() => {
            readPlaces().then(setTodos);
        }, []),
    );

    return (
        <ScrollView>
            <View>
                <TituloPlace titulo="Menu" />
            </View>
            <View style={estilos.lista}>
                {todos.map(l => (
                    <BotonPlaces
                        key={l.id}
                        titulo={l.titulo}
                        subtitulo={l.subtitulo}
                        color={l.color}
                        onPress={() => router.push({ pathname: "/place/[id]", params: { id: l.id } })}
                    />
                ))}
            </View>
            <View style={estilos.lista}>
                <BotonPlaces
                    titulo="Añadir lugar"
                    subtitulo="Crear uno nuevo"
                    color="#234c3b"
                    onPress={() => router.push("/nuevo")}
                />
            </View>
        </ScrollView>
    );
}

const estilos = StyleSheet.create({
    lista: {
        padding: 16,
        gap: 12,
    },
});
