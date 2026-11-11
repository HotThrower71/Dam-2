import { Stack } from 'expo-router';

export default function Layout() {
  return (
    <Stack
      screenOptions={{
        headerStyle: { backgroundColor: '#f7f4ed' },
        headerTintColor: '#234c3b',
        contentStyle: { backgroundColor: '#f7f4ed' },
      }}
    >
      <Stack.Screen name="index" options={{ title: 'Lugares de Barcelona' }} />
      <Stack.Screen name="detall/[id]" options={{ title: 'Detalle' }} />
    </Stack>
  );
}
