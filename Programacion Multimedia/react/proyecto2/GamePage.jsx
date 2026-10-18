import React from 'react';
import { View, StyleSheet, FlatList, SafeAreaView, Text, StatusBar } from 'react-native';
import GameCard from './GameCard';

// Array con los datos de los diferentes juegos
const GAMES_DATA = [
  {
    id: '1',
    title: 'Elden Ring',
    platform: 'PS5 / PC',
    imageUrl: 'https://images.unsplash.com/photo-1605901309584-818e25960b8f?auto=format&fit=crop&q=80&w=800',
  },
  {
    id: '2',
    title: 'Subnautica 2',
    platform: 'PC / PS5',
    imageUrl: 'https://images.unsplash.com/photo-1682687220742-aba13b6e50ba?auto=format&fit=crop&q=80&w=800',
  },
  {
    id: '3',
    title: 'Euro Truck Simulator 2',
    platform: 'PC',
    imageUrl: 'https://images.unsplash.com/photo-1601584115197-04ecc0da31d7?auto=format&fit=crop&q=80&w=800',
  },
  {
    id: '4',
    title: 'Rumble VR',
    platform: 'Meta Quest 3',
    imageUrl: 'https://images.unsplash.com/photo-1622979135225-d2ba269cf1ac?auto=format&fit=crop&q=80&w=800',
  },
  {
    id: '5',
    title: 'Watch Dogs 2',
    platform: 'PS4 / PC',
    imageUrl: 'https://images.unsplash.com/photo-1550745165-9bc0b252726f?auto=format&fit=crop&q=80&w=800',
  }
];

const GamesPage = () => {
  
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

export default GamesPage;