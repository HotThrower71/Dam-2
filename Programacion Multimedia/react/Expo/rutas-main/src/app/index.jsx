import { Link } from 'expo-router';
import { FlatList, Pressable, StyleSheet, Text, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import { llocs } from '../data/llocs';

export default function Home() {
  return (
    <SafeAreaView style={styles.pantalla} edges={['bottom', 'left', 'right']}>
      <FlatList
        data={llocs}
        keyExtractor={(lugar) => lugar.id}
        contentContainerStyle={styles.lista}
        ListHeaderComponent={
          <View style={styles.introduccion}>
            <Text style={styles.etiqueta}>EXPLORA LA CIUDAD</Text>
            <Text style={styles.titulo}>Tu proxima parada</Text>
            <Text style={styles.descripcion}>
              Elige un lugar y descubre que lo hace especial.
            </Text>
          </View>
        }
        renderItem={({ item: lugar }) => (
          <View style={styles.tarjeta}>
            <Text style={styles.etiqueta}>{lugar.categoria}</Text>
            <Text style={styles.nombre}>{lugar.nombre}</Text>
            <Link
              href={{
                pathname: '/detall/[id]',
                params: { id: lugar.id },
              }}
              asChild
            >
              <Pressable
                accessibilityLabel={`Ver ${lugar.nombre}`}
                style={({ pressed }) => [styles.boton, pressed && styles.pulsado]}
              >
                <Text style={styles.textoBoton}>Ver lugar</Text>
              </Pressable>
            </Link>
          </View>
        )}
      />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  pantalla: { flex: 1 },
  lista: {
    padding: 24,
    gap: 16,
    width: '100%',
    maxWidth: 640,
    alignSelf: 'center',
  },
  introduccion: { gap: 12, marginBottom: 12 },
  etiqueta: { fontSize: 12, fontWeight: '700', letterSpacing: 1, color: '#52644e' },
  titulo: { fontSize: 34, fontWeight: '700', color: '#234c3b' },
  descripcion: { fontSize: 17, lineHeight: 26, color: '#555b50' },
  tarjeta: {
    padding: 24,
    gap: 16,
    backgroundColor: '#ffffff',
    borderRadius: 16,
    borderWidth: 1,
    borderColor: '#dedfd4',
  },
  nombre: { fontSize: 23, fontWeight: '600', color: '#24352b' },
  boton: {
    backgroundColor: '#234c3b',
    borderRadius: 10,
    padding: 14,
    minHeight: 48,
    alignItems: 'center',
  },
  pulsado: { opacity: 0.7 },
  textoBoton: { color: '#ffffff', fontSize: 16, fontWeight: '600' },
});
