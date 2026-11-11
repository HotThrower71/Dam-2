import React from 'react';
import { View, StyleSheet, FlatList, SafeAreaView, Text, StatusBar } from 'react-native';
import GameCard from './GameCard';

// Array con los datos de los diferentes juegos
const GAMES_DATA = [
  {
    id: '1',
    title: 'The Legend of Zelda: Breath of the Wild',
    platform: 'Nintendo Switch',
    imageUrl: 'https://images.unsplash.com/photo-1612287230202-1ff1d85d1bdf?auto=format&fit=crop&q=80&w=800',
  },
  {
    id: '2',
    title: 'Minecraft',
    platform: 'PC / Consolas / Móvil',
    imageUrl: 'https://images.unsplash.com/photo-1587573089734-09cb69c0f2b4?auto=format&fit=crop&q=80&w=800',
  },
  {
    id: '3',
    title: 'God of War Ragnarök',
    platform: 'PS5 / PS4',
    imageUrl: 'https://images.unsplash.com/photo-1511512578047-dfb367046420?auto=format&fit=crop&q=80&w=800',
  },
  {
    id: '4',
    title: 'Hollow Knight',
    platform: 'PC / Switch',
    imageUrl: 'https://images.unsplash.com/photo-1550745165-9bc0b252726f?auto=format&fit=crop&q=80&w=800',
  },
  {
    id: '5',
    title: 'Forza Horizon 5',
    platform: 'Xbox / PC',
    imageUrl: 'https://images.unsplash.com/photo-1493711662062-fa541adb3fc8?auto=format&fit=crop&q=80&w=800',
  }
];

const GamesPage2 = () => {

  // Función para renderizar cada item del FlatList
  const renderItem = ({ item }) => (
    <GameCard
      title={item.title}
      platform={item.platform}
      imageUrl={item.imageUrl}
      onPress={() => console.log(`Has tocado ${item.title}`)}
    />
  );

  return (
    <SafeAreaView style={styles.container}>
      <StatusBar barStyle="light-content" />
      <View style={styles.header}>
        <Text style={styles.headerTitle}>Mi Biblioteca</Text>
      </View>

      <FlatList
        data={GAMES_DATA}
        keyExtractor={(item) => item.id}
        renderItem={renderItem}
        contentContainerStyle={styles.listContainer}
        showsVerticalScrollIndicator={false}
      />
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#121212', // Fondo oscuro
  },
  header: {
    padding: 20,
    paddingTop: 40,
    backgroundColor: '#121212',
  },
  headerTitle: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#ffffff',
  },
  listContainer: {
    paddingBottom: 20,
  },
});

export default GamesPage2;
