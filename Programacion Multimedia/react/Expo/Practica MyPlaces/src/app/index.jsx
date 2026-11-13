import { router, useFocusEffect } from "expo-router";
import { useCallback, useState } from "react";
import { ScrollView, StyleSheet, Text, View } from "react-native";

import BotonPlaces from "../components/BotonPlaces";
import TituloPlace from "../components/TituloPlace";

import { readPlaces } from "../storage/placesStorage";

export default function Inicio() {
    const [todos, setTodos] = useState([]); // empieza vacío y se rellena al leer
    const [cargado, setCargado] = useState(false); // false hasta que termina la primera lectura

    // cada vez que esta pantalla vuelve a verse (por ejemplo tras crear o borrar un lugar), se vuelve a leer
    useFocusEffect(
        useCallback(() => {
            readPlaces().then(lista => {
                setTodos(lista);
                setCargado(true);
            });
        }, []),
    );

    return (
        <ScrollView>
            <View>
                <TituloPlace titulo="Menu" />
            </View>
            <View style={estilos.lista}>
                {/* lista vacía: se avisa solo cuando ya se ha leído y de verdad no hay ninguno (si no, parpadearía al abrir) */}
                {cargado && todos.length === 0 && (
                    <Text style={estilos.vacio}>Aún no hay lugares. ¡Añade el primero!</Text>
                )}
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
                <BotonPlaces
                    titulo="Descubre"
                    subtitulo="Busca lugares en internet"
                    color="#16a0b5"
                    onPress={() => router.push("/descubre")}
                />
                <BotonPlaces
                    titulo="Mi ubicación"
                    subtitulo="Ver dónde estoy"
                    color="#d9822b"
                    onPress={() => router.push("/ubicacion")}
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
    vacio: {
        paddingVertical: 24,
        fontSize: 16,
        color: "#555555",
        textAlign: "center",
    },
});
