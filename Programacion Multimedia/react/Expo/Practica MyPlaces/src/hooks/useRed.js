import NetInfo from "@react-native-community/netinfo";
import { useEffect, useState } from "react";

// Hook que dice si hay conexión y de qué tipo es. Se actualiza solo cuando la red cambia.
//   conectado: true si hay conexión a internet (o todavía no se sabe)
//   tipo:      "wifi", "cellular", "ethernet", "none"…
export default function useRed() {
    // al principio todavía no se sabe: se supone que hay conexión
    const [red, setRed] = useState({ conectado: true, tipo: "unknown" });

    useEffect(() => {
        // addEventListener se llama una vez al empezar y cada vez que la red cambia
        const dejarDeEscuchar = NetInfo.addEventListener(estado => {
            // isConnected y isInternetReachable pueden ser null mientras se averigua: solo "false" cuenta como sin conexión
            const hayRed = estado.isConnected !== false && estado.isInternetReachable !== false;
            setRed({ conectado: hayRed, tipo: estado.type });
        });
        return dejarDeEscuchar; // limpieza: deja de escuchar al cerrar la pantalla
    }, []);

    return red;
}
