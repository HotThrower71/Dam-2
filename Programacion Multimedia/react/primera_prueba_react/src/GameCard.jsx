import { useState } from 'react'

export default function GameCard({ nombre, plataforma, año }) {
  const [fav, setFav] = useState(false)
  console.log(fav)

  return (
    <div>
      <h1>{nombre}</h1>
      <h2>{plataforma}</h2>
      <h3>{año}</h3>
    </div>
  )
}