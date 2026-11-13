import { Pressable, StyleSheet, View } from "react-native";

// colores entre los que se puede elegir (se pueden cambiar pasando la prop "colores")
export const COLORES = ["#111111", "#1e63d6", "#3aa564", "#d62828", "#8e44ad", "#16a0b5", "#e84393", "#7f8c8d"];

// Fila de círculos de colores.
//   valor:     el color elegido ahora mismo
//   onCambiar: función que se llama con el color nuevo cuando se pulsa un círculo
//   colores:   lista de colores a mostrar (por defecto, COLORES)
export default function SelectorColor({ valor, onCambiar, colores = COLORES }) {
    return (
        <View style={estilos.colores}>
            {colores.map(c => (
                <Pressable
                    key={c}
                    onPress={() => onCambiar(c)}
                    accessibilityLabel={`Color ${c}`}
                    style={[estilos.muestra, { backgroundColor: c }, c === valor && estilos.seleccionada]}
                />
            ))}
        </View>
    );
}

const estilos = StyleSheet.create({
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
});
