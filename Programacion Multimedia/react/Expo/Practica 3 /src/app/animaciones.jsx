import { useEffect, useRef, useState } from "react";
import { Animated, Easing, ScrollView, StyleSheet, Text, View } from "react-native";

import Boton from "../components/Boton";
import Tarjeta from "../components/Tarjeta";
import { colores } from "../utils/colores";

export default function Animaciones() {
    return (
        <ScrollView contentContainerStyle={estilos.contenido}>
            <Text style={estilos.ayuda}>
                Las dos animaciones usan la API Animated de React Native con interpolación. Al salir de la pantalla se
                paran y se quitan sus escuchas (limpieza).
            </Text>
            <Viaje />
            <Ecualizador />
        </ScrollView>
    );
}

// ---------------------------------------------------------------------------
// Animación 1: una pelota cruza la pista una vez (tiene final).
// Un único valor 0 -> 1 se interpola a posición, giro, tamaño y color.
// ---------------------------------------------------------------------------
function Viaje() {
    const progreso = useRef(new Animated.Value(0)).current;
    const animacion = useRef(null);
    const [estado, setEstado] = useState("Parada"); // Parada | En marcha | Terminada | Interrumpida
    const [porcentaje, setPorcentaje] = useState(0);
    const [anchoPista, setAnchoPista] = useState(0);

    useEffect(() => {
        // escucha para enseñar el % mientras se mueve
        const id = progreso.addListener(({ value }) => setPorcentaje(Math.round(value * 100)));
        // LIMPIEZA al desmontar: se para la animación y se quita la escucha
        return () => {
            animacion.current?.stop();
            progreso.removeListener(id);
        };
    }, []);

    function iniciar() {
        progreso.setValue(0);
        setEstado("En marcha");
        animacion.current = Animated.timing(progreso, {
            toValue: 1,
            duration: 2500,
            easing: Easing.inOut(Easing.cubic),
            useNativeDriver: false, // false porque se anima el color, que el driver nativo no admite
        });
        // el callback se llama al FINALIZAR: finished = true si llegó al final, false si se paró antes
        animacion.current.start(({ finished }) => setEstado(finished ? "Terminada" : "Interrumpida"));
    }

    function parar() {
        animacion.current?.stop();
    }

    function reiniciar() {
        animacion.current?.stop();
        progreso.setValue(0);
        setEstado("Parada");
    }

    const tamano = 56;
    // interpolaciones: el mismo progreso se traduce a cada propiedad
    const x = progreso.interpolate({ inputRange: [0, 1], outputRange: [0, Math.max(anchoPista - tamano, 0)] });
    const giro = progreso.interpolate({ inputRange: [0, 1], outputRange: ["0deg", "720deg"] });
    const escala = progreso.interpolate({ inputRange: [0, 0.5, 1], outputRange: [1, 1.35, 1] });
    const color = progreso.interpolate({
        inputRange: [0, 0.5, 1],
        outputRange: [colores.principal, colores.acento, colores.ok],
    });

    return (
        <Tarjeta titulo="1 · Viaje (inicio y final)">
            <Text style={estilos.estado}>
                Estado: <Text style={estilos.negrita}>{estado}</Text> · {porcentaje} %
            </Text>
            <View style={estilos.pista} onLayout={(e) => setAnchoPista(e.nativeEvent.layout.width)}>
                <Animated.View
                    style={[
                        estilos.pelota,
                        { width: tamano, height: tamano, backgroundColor: color, transform: [{ translateX: x }, { rotate: giro }, { scale: escala }] },
                    ]}
                >
                    <Text style={estilos.textoPelota}>★</Text>
                </Animated.View>
            </View>
            <View style={estilos.fila}>
                <Boton texto="Iniciar" onPress={iniciar} disabled={estado === "En marcha"} estilo={estilos.flex} />
                <Boton texto="Parar" onPress={parar} variante="peligro" disabled={estado !== "En marcha"} estilo={estilos.flex} />
                <Boton texto="Reiniciar" onPress={reiniciar} variante="secundario" estilo={estilos.flex} />
            </View>
        </Tarjeta>
    );
}

// ---------------------------------------------------------------------------
// Animación 2: ecualizador en bucle (no acaba sola; se para con el botón).
// Cada barra tiene su valor; se lanzan desfasadas con stagger y se repiten con loop.
// ---------------------------------------------------------------------------
const NUM_BARRAS = 7;

function Ecualizador() {
    const valores = useRef(Array.from({ length: NUM_BARRAS }, () => new Animated.Value(0))).current;
    const bucle = useRef(null);
    const intervalo = useRef(null);
    const [enMarcha, setEnMarcha] = useState(false);
    const [vueltas, setVueltas] = useState(0);

    useEffect(() => {
        // LIMPIEZA al desmontar: un bucle infinito (y su temporizador) seguirían consumiendo si no se paran
        return () => {
            bucle.current?.stop();
            clearInterval(intervalo.current);
            valores.forEach((v) => v.stopAnimation());
        };
    }, []);

    function iniciar() {
        setVueltas(0);
        // una "vuelta": cada barra sube y baja, empezando cada una 90 ms después de la anterior
        const vuelta = Animated.stagger(
            90,
            valores.map((v, i) =>
                Animated.sequence([
                    Animated.timing(v, { toValue: 1, duration: 260 + i * 25, easing: Easing.out(Easing.quad), useNativeDriver: true }),
                    Animated.timing(v, { toValue: 0, duration: 260 + i * 25, easing: Easing.in(Easing.quad), useNativeDriver: true }),
                ])
            )
        );
        bucle.current = Animated.loop(vuelta); // sin "iterations": se repite hasta que se pare
        setEnMarcha(true);
        // contador de vueltas: lo que dura una vuelta = desfase total + subida y bajada de la última barra
        const msPorVuelta = 90 * (NUM_BARRAS - 1) + 2 * (260 + (NUM_BARRAS - 1) * 25);
        intervalo.current = setInterval(() => setVueltas((n) => n + 1), msPorVuelta);
        bucle.current.start(() => {
            // se llama al FINALIZAR (en un bucle infinito, solo cuando lo paramos)
            clearInterval(intervalo.current);
            setEnMarcha(false);
            valores.forEach((v) => v.setValue(0));
        });
    }

    function parar() {
        bucle.current?.stop();
    }

    return (
        <Tarjeta titulo="2 · Ecualizador (bucle)">
            <Text style={estilos.estado}>
                Estado: <Text style={estilos.negrita}>{enMarcha ? "En marcha" : "Parada"}</Text> · vueltas: {vueltas}
            </Text>
            <View style={estilos.ecualizador}>
                {valores.map((v, i) => {
                    // interpolación: 0..1 -> altura (escala vertical) y opacidad
                    const scaleY = v.interpolate({ inputRange: [0, 1], outputRange: [0.15, 1] });
                    const opacity = v.interpolate({ inputRange: [0, 1], outputRange: [0.4, 1] });
                    return (
                        <Animated.View
                            key={i}
                            style={[estilos.barra, { opacity, transform: [{ scaleY }] }, i % 2 === 1 && estilos.barraAlterna]}
                        />
                    );
                })}
            </View>
            <View style={estilos.fila}>
                <Boton texto="Iniciar" onPress={iniciar} disabled={enMarcha} estilo={estilos.flex} />
                <Boton texto="Parar" onPress={parar} variante="peligro" disabled={!enMarcha} estilo={estilos.flex} />
            </View>
        </Tarjeta>
    );
}

const estilos = StyleSheet.create({
    contenido: { padding: 16, gap: 14 },
    ayuda: { color: colores.suave, fontSize: 14, lineHeight: 20 },
    estado: { color: colores.suave, fontSize: 14 },
    negrita: { fontWeight: "bold", color: colores.texto },
    pista: {
        height: 90,
        justifyContent: "center",
        backgroundColor: "#f0ece3",
        borderRadius: 12,
        paddingHorizontal: 0,
        overflow: "hidden",
    },
    pelota: { borderRadius: 28, alignItems: "center", justifyContent: "center" },
    textoPelota: { color: "#ffffff", fontSize: 24, fontWeight: "bold" },
    ecualizador: {
        height: 120,
        flexDirection: "row",
        alignItems: "flex-end",
        justifyContent: "center",
        gap: 10,
        backgroundColor: "#f0ece3",
        borderRadius: 12,
        paddingVertical: 10,
    },
    barra: { width: 18, height: 100, borderRadius: 6, backgroundColor: colores.principal, transformOrigin: "bottom" },
    barraAlterna: { backgroundColor: colores.acento },
    fila: { flexDirection: "row", gap: 8 },
    flex: { flex: 1 },
});
