import * as Device from 'expo-device';
import { Platform, StyleSheet, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import { AnimatedIcon } from '@/components/animated-icon';
import GameCard from "@/components/GameCard.jsx";
import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import { WebBadge } from '@/components/web-badge';
import { BottomTabInset, MaxContentWidth, Spacing } from '@/constants/theme';


function getDevMenuHint() {
  if (Platform.OS === 'web') {
    return <ThemedText type="small">use browser devtools</ThemedText>;
  }
  if (Device.isDevice) {
    return (
      <ThemedText type="small">
        shake device or press <ThemedText type="code">m</ThemedText> in terminal
      </ThemedText>
    );
  }
  const shortcut = Platform.OS === 'android' ? 'cmd+m (or ctrl+m)' : 'cmd+d';
  return (
    <ThemedText type="small">
      press <ThemedText type="code">{shortcut}</ThemedText>
    </ThemedText>
  );
}

export default function HomeScreen() {
  return (
    <ThemedView style={styles.container}>
      <SafeAreaView style={styles.safeArea}>
        <ThemedView style={styles.heroSection}>
          <AnimatedIcon />
          <ThemedText type="title" style={styles.title}>
            Welcome Miquel
          </ThemedText>
        </ThemedView>
        <View style={{ marginTop: 60 }}>
          <GameCard 
              nombre="Batman Arkham City" 
              plataforma="Xbox360" 
              año="2011" 
              // Cambia la ruta dentro del require según donde tengas tu imagen.
              // Por ejemplo, si está en la carpeta assets general del proyecto:
              logoSource={require('../../assets/images/xbox.png')}
          />
          <GameCard 
              nombre="Marvel's Spider-Man" 
              plataforma="PlayStation" 
              año="2018" 
              // En Expo también suele funcionar el alias @/ para ir a la raíz de src
              logoSource={require('@/assets/images/playstation.jpg')}
          />
        </View>  
        {Platform.OS === 'web' && <WebBadge />}
      </SafeAreaView>
    </ThemedView>
  );
}

const styles = StyleSheet.create({
  container: {
    backgroundColor:'blue',
    flex: 1,
    justifyContent: 'center',
    flexDirection: 'column',
  },
  safeArea: {
    flex: 1,
    paddingHorizontal: Spacing.four,
    alignItems: 'center',
    gap: Spacing.three,
    paddingBottom: BottomTabInset + Spacing.three,
    maxWidth: MaxContentWidth,
  },
  heroSection: {
    borderRadius:49,
    alignItems: 'center',
    justifyContent: 'center',
    flex: 1,
    paddingHorizontal: Spacing.four,
    gap: Spacing.four,
  },
  title: {
    textAlign: 'center',
    fontSize: 60,
    color: 'green',
    fontWeight:'bold',
    
  },
  code: {
    textTransform: 'uppercase',
  },
  stepContainer: {
    backgroundColor:'red',
    gap: Spacing.three,
    alignSelf: 'stretch',
    paddingHorizontal: Spacing.three,
    paddingVertical: Spacing.four,
    borderRadius: Spacing.four,
  },
});
