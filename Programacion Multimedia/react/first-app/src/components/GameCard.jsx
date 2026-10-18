import { Image, StyleSheet, Text, View } from 'react-native';

// Cambiamos "logoUrl" por "logoSource"
export default function GameCard({ nombre, plataforma, año, logoSource }) {
  return (
    <View style={styles.card}>
      <Text style={styles.title}>{nombre}</Text>
      
      <View style={styles.footer}>
        <View style={styles.platformBadge}>
          {/* Aquí quitamos el { uri: ... } y pasamos el origen directamente */}
          <Image source={logoSource} style={styles.logo} />
          <Text style={styles.platformText}>{plataforma}</Text>
        </View>
        
        <Text style={styles.year}>{año}</Text>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: '#fff',
    padding: 15,
    borderRadius: 12,
    marginBottom: 15,
    elevation: 3,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
  },
  title: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 10,
  },
  footer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  platformBadge: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#e6f2ff',
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 6,
  },
  logo: {
    width: 60,
    height: 60,
    marginRight: 6,
    resizeMode: 'contain',
  },
  platformText: {
    fontSize: 12,
    fontWeight: 'bold',
    color: '#0056b3',
  },
  year: {
    fontSize: 14,
    color: '#666',
  }
});