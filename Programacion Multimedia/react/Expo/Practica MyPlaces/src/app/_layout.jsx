import { Stack } from "expo-router";
import { View } from "react-native";

import BannerRed from "../components/BannerRed";

export default function Layout() {
    return (
        // la caja ocupa toda la pantalla: arriba las pantallas (Stack) y abajo la barra de conexión,
        // que así se ve en TODAS las pantallas sin repetirla en cada una
        <View style={{ flex: 1 }}>
            {/* screenOptions se aplica a TODAS las pantallas de la app */}
            <Stack
                screenOptions={{
                    headerStyle: { backgroundColor: "#234c3b" }, // barra de arriba en el verde del título
                    headerTintColor: "#ffffff", // texto y flecha de volver en blanco
                    headerTitleStyle: { fontWeight: "bold" },
                    headerShadowVisible: false, // quita la línea de sombra bajo la barra
                    contentStyle: { backgroundColor: "#f7f4ed" }, // fondo crema común a todas las pantallas
                }}
            >
                {/* el name es el nombre del fichero, sin .jsx */}
                <Stack.Screen name="index" options={{ title: "MyPlaces" }} />
                <Stack.Screen name="nuevo" options={{ title: "Nuevo lugar" }} />
                <Stack.Screen name="descubre" options={{ title: "Descubre" }} />
                <Stack.Screen name="ubicacion" options={{ title: "Mi ubicación" }} />
            </Stack>
            <BannerRed />
        </View>
    );
}
