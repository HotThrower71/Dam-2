import React from 'react';
import { View, Text, Image, StyleSheet, TouchableOpacity } from 'react-native';

const GameCard = ({ title, platform, imageUrl, onPress }) => {
  return (
    <TouchableOpacity style={styles.card} onPress={onPress} activeOpacity={0.8}>
      <Image 
        source={{ uri: imageUrl || 'https://via.placeholder.com/150' }} 
        style={styles.image} 
      />
      <View style={styles.infoContainer}>
        <Text style={styles.title}>{title}</Text>
        <Text style={styles.platform}>{platform}</Text>
      </View>
    </TouchableOpacity>
  );
};

const styles = StyleSheet.create({
  card: {
    backgroundColor: '#1f1f1f',
    borderRadius: 12,
    marginVertical: 10,
    marginHorizontal: 16,
    overflow: 'hidden',
    elevation: 5, // Sombra en Android
    shadowColor: '#000', // Sombras en iOS
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.25,
    shadowRadius: 3.84,
  },
  image: {
    width: '100%',
    height: 180,
    resizeMode: 'cover',
  },
  infoContainer: {
    padding: 15,
  },
  title: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#ffffff',
    marginBottom: 5,
  },
  platform: {
    fontSize: 14,
    color: '#a0a0a0',
  },
});

export default GameCard;