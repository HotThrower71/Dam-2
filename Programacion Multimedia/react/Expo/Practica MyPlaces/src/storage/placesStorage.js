import AsyncStorage from "@react-native-async-storage/async-storage";

import { places } from "../data/places";

// clave con la que se guarda todo en el almacenamiento del móvil
const CLAVE = "places_v3";

// Lee la lista guardada. La PRIMERA vez (todavía no hay nada guardado) devuelve los lugares de ejemplo.
export async function readPlaces() {
    const texto = await AsyncStorage.getItem(CLAVE); // texto guardado, o null si nunca se ha guardado
    return texto ? JSON.parse(texto) : places; // de texto a lista, o los de ejemplo
}

// Guarda la lista completa (AsyncStorage solo guarda texto, por eso el stringify).
export async function savePlaces(lista) {
    await AsyncStorage.setItem(CLAVE, JSON.stringify(lista));
}

// Borra un lugar (de ejemplo o creado): se queda con todos MENOS el que tiene ese id.
export async function deletePlace(id) {
    const lista = await readPlaces();
    await savePlaces(lista.filter(l => l.id !== id));
}
