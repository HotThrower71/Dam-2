import { useState } from 'react'

import './App.css'

function App() {
const [nombre,setNombre] = useState("");
const [correo,setCorreo] = useState("");
const [edad,setEdad] = useState("");


console.log(nombre,edad,correo);
function enviar(){
  alert(nombre,correo,edad);
}

  return (
    <>

    <form onSubmit={enviar}>
    <input
		type="text" //Tipo de boton 
		placeholder="Nombre"
		onChange = {(e) => setNombre(e.target.value)}/>
    
    <input
		type="mail" //Tipo de boton 
		placeholder="Correo"
		onChange = {(e) => setCorreo(e.target.value)}/>
    
    <input
		type="number" //Tipo de boton 
		placeholder="Edad"
    min={20}
    max={120}
		onChange = {(e) => setEdad(e.target.value)}/>

    <button type='submit' placeholder="Enviar" >
    Enviar
    </button>
    </form>
    </>
  )
}

export default App
