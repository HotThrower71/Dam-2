import { Stack } from "expo-router";
import { StatusBar } from "expo-status-bar";

import { colores } from "../utils/colores";

export default function Layout() {
    return (
        <>
            <StatusBar style="light" />
            {/* screenOptions se aplica a TODAS las pantallas */}
            <Stack
                screenOptions={{
                    headerStyle: { backgroundColor: colores.principal },
                    headerTintColor: "#ffffff",
                    headerTitleStyle: { fontWeight: "bold" },
                    headerShadowVisible: false,
                    contentStyle: { backgroundColor: colores.fondo },
                }}
            >
                {/* el name es el nombre del fichero de src/app, sin .jsx */}
                <Stack.Screen name="index" options={{ title: "Estudi Multimèdia" }} />
                <Stack.Screen name="imagenes" options={{ title: "Imágenes" }} />
                <Stack.Screen name="procesado" options={{ title: "Procesado" }} />
                <Stack.Screen name="audio" options={{ title: "Notas de voz" }} />
                <Stack.Screen name="video" options={{ title: "Vídeo" }} />
                <Stack.Screen name="animaciones" options={{ title: "Animaciones" }} />
            </Stack>
        </>
    );
}
