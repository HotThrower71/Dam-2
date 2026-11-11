import { useState } from 'react'
import heroImg from './assets/hero.png'
import reactLogo from './assets/react.svg'
import viteLogo from './assets/vite.svg'
import './App.css'

// import ProfileCard from './ProfileCard.jsx'
import GameCard from "./GameCard.jsx";



function App() {
  const [count, setCount] = useState(0)
  const nombre =" Ismael";
  const games = [
{ nombre: "Celeste", plataforma: "PS5",año:"2025" },
{ nombre: "Batman", plataforma: "PS5",año:"2070" },
{ nombre: "Iron Man", plataforma: "PS5",año:"2028" },
];

  return (
    <>
{games.map(game => (
<GameCard key={game.nombre} nombre={game.nombre} plataforma={game.plataforma} año={game.año} />
))}

    <GameCard nombre="Batman Arkham City" plataforma="Xbox360" año="2011" />
    <GameCard nombre="Marvel's Spider-Man" plataforma="PS4" año="2018" />
    <GameCard nombre="Marvel´s Wolverine" plataforma="PS5" año="2026" />
  
  
  </>
  )
}

export default App
