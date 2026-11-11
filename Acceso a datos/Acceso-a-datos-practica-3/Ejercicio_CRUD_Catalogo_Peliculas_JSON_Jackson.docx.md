**EJERCICIO PRÁCTICO  ·  CRUD Y PERSISTENCIA**

# **Gestor CRUD del** **catálogo de películas con JSON**

Tecnologías: Java \+ Jackson · Lectura, escritura actualización y eliminado de datos en un fichero JSON

| MÓDULO | Acceso a Datos | NIVEL | 2.º DAM – IFC32 B |
| :---- | :---- | :---- | :---- |
| **LENGUAJE** | Java | **MODALIDAD** | Individual |

# **Situación**

En la actividad anterior se generó un catálogo de películas en XML a partir de un fichero de texto. En este ejercicio se realizará la migración de esos registros a un fichero JSON y, después, el programa trabajará sobre ese JSON para consultar y mantener el catálogo. El objetivo es practicar la persistencia, serialización y deserialización de objetos Java con un fichero estructurado, sin utilizar una base de datos.

| Resultado esperado Una aplicación de consola Java capaz de leer peliculas.json y realizar las operaciones CRUD: consultar, insertar, modificar y eliminar películas. Los cambios deben permanecer guardados al cerrar y volver a ejecutar el programa. |
| :---- |

# **Objetivos**

* Leer el XML generado en la práctica anterior y trasladar todos sus registros a una estructura JSON.  
* Modelar los datos mediante clases Java convencionales, por ejemplo Pelicula.java.  
* Utilizar Jackson (ObjectMapper) para leer objetos desde JSON y escribirlos de nuevo en el fichero.  
* Implementar las operaciones CRUD con búsqueda por identificador único.  
* Validar los datos introducidos por consola y tratar errores de lectura y escritura.  
* Mantener la integridad del listado de peliculas cuando una operación no se puede completar.

# **Reglas técnicas**

| Utilizar biblioteca Jackson trabajar con JSON NO se debe construir el JSON concatenando cadenas ni procesarlo manualmente línea a línea. Toda lectura y escritura JSON debe realizarse mediante Jackson. |
| :---- |

* Usa ObjectMapper para deserializar el fichero JSON a objetos Java y serializar los objetos Java a JSON.  
* No utilices anotaciones de mapeo o conversión de Jackson (@JsonProperty, @JsonCreator, @JsonAlias, @JsonIgnore...)  
* Los objetos Java de clase *Película* deben ser POJO convencionales, con constructor y métodos getter/setter adecuados.  
* Se debe usar **JDOM2 para leer el XML** de origen durante la migración inicial (¡aconsejable re-aprovechar código\!).  
* Una vez creado peliculas.json, el **CRUD se hará exclusivamente sobre el JSON**.  
* No es necesario conservar la lectura de peliculas.txt ni realizar operaciones CRUD sobre el XML una vez terminada la migración.

# **Desarrollo de la practica**

## **Parte 1\. Preparación del proyecto y migración XML → JSON**

* Duplica el proyecto de la actividad anterior (opcional, si te sirve de ayuda).   
* Localiza el fichero peliculas.xml y comprueba que contiene el catálogo completo.  
* Asegúrate de tener instaladas/incluidas las dependencias necesarias de Jackson.  
  * Puedes aprovechar el ejemplo visto en clase para comprobar si la librería funciona (leer un peliculas.json ya existente).  
* Crea un fichero llamado peliculas.json. Migra todas las películas del XML, conservando sus identificadores y datos. Cada ID y cada año debe almacenarse como número entero, no como texto.  
* Una vez parseadas y recogidas las instancias de películas del XML en una colección (agrupadas), usa Jackson para escribir la estructura JSON.  
* Recuerda que el formato JSON debe mantenerse legible, con sangría, cuando se guarde (no debe ser un JSON de una sola linea).

# **Estructura del fichero peliculas.json**

El documento debe tener un objeto raíz con una propiedad peliculas que contenga un array. Cada elemento representa una estructura JSON de una película (es decir, se trata de un json que contiene un array de otros json).

| Campos obligatorios id (entero), titulo (texto), director (texto), año (entero) y genero (texto). . |
| :---- |

Estructura de ejemplo de 10 películas:

{  
  "peliculas": \[  
    { "id": 1, "titulo": "Pulp Fiction", "director": "Quentin Tarantino", "año": 1994, "genero": "Crimen" },  
    { "id": 2, "titulo": "Jurassic Park", "director": "Steven Spielberg", "año": 1993, "genero": "Aventura" },  
    { "id": 3, "titulo": "El silencio de los corderos", "director": "Jonathan Demme", "año": 1991, "genero": "Terror" },  
    { "id": 4, "titulo": "Whiplash", "director": "Damien Chazelle", "año": 2014, "genero": "Drama" },  
    { "id": 5, "titulo": "Coco", "director": "Lee Unkrich", "año": 2017, "genero": "Animación" },  
    { "id": 6, "titulo": "Mad Max: Fury Road", "director": "George Miller", "año": 2015, "genero": "Acción" },  
    { "id": 7, "titulo": "La La Land", "director": "Damien Chazelle", "año": 2016, "genero": "Romance" },  
    { "id": 8, "titulo": "El gran hotel Budapest", "director": "Wes Anderson", "año": 2014, "genero": "Comedia" },  
    { "id": 9, "titulo": "Blade Runner 2049", "director": "Denis Villeneuve", "año": 2017, "genero": "Ciencia ficción" },  
    { "id": 10, "titulo": "12 angry men", "director": "Sidney Lumet", "año": 1957, "genero": "Drama" }  
  \]  
}

## **Clases Java recomendadas**

* Pelicula: contiene id, titulo, director, año y genero, con constructory getters/setters.  
* (OPCIONAL) Catalogo: contiene una List\<Pelicula\> llamada peliculas, con constructor sin parámetros y getters/setters.  
* Main: presenta el menú, solicita datos, invoca las operaciones y coordina la lectura y escritura del fichero.

# **Menú de gestión del documento**

Implementa un menú de consola que se repita después de cada operación hasta que se seleccione la opción de Salir.

\========== CATÁLOGO DE PELÍCULAS \==========  
1\. Convertir XML a JSON  
2\. Mostrar todas las películas  
3\. Buscar una película por ID  
4\. Añadir una película  
5\. Modificar una película por ID  
6\. Eliminar una película por ID  
0\. Salir  
\==========================================  
Selecciona una opción:

## **Parte 2\. Lectura y consulta del JSON**

* Lee peliculas.json mediante ObjectMapper y convierte su contenido en un objeto Catalogo que agrupe instancias de Pelicula (o en una estructura equivalente basada en clases Java).  
* Muestra todas las películas con ID, título, director, año y género por pantalla.  
* Permite buscar una película por ID. Si no existe, informa del resultado.

## **Parte 3\. Añadir películas**

* Solicita título, director, año y género. Valida los datos antes de modificar el catálogo.  
  * Considerar cuando se debe introducir texto o enteros.  
  * Si el proceso falla, repetir último paso o volver al menú principal (comentar opción elegida).  
* Genera automáticamente el ID: debe ser uno más que el ID máximo existente. Si el catálogo está vacío, el primer ID será 1\.  
* Añade la nueva película a la lista de Catalogo y guarda el resultado en peliculas.json mediante Jackson.  
* Informa del éxito de la operación y muestra el ID asignado. No permitas ID duplicados (sí se permiten saltos).

## **Parte 4\. Modificar películas**

* Solicita el ID de la película que se desea modificar y busca el objeto correspondiente.  
* Si existe, muestra sus valores actuales y permite modificar título, director, año y género.  
* Actualiza el objeto encontrado y persiste el catálogo completo en peliculas.json usando Jackson.  
* Si el ID no existe, informa al usuario y no escribas cambios en el fichero.

## **Parte 5\. Eliminar películas**

* Solicita el ID de la película que se desea eliminar y busca el registro.  
* Si existe, muestra los datos (indicando su eliminación) y eliminalo.  
* Elimina la película de la lista de peliculas y guarda el catálogo actualizado con Jackson.  
* Si el ID no existe, no alteres el contenido del fichero.

| Tipo de borrado Implementa un borrado físico: la película se elimina del array peliculas del JSON y deja de figurar en el catálogo. |
| :---- |

# **Validaciones y errores**

## **Parte 6\. Validación y gestión de errores**

Comprueba el comportamiento del programa, como mínimo, en estas situaciones:

* El fichero peliculas.xml no existe. El programa debe informar el problema y detenerse (salir).  
* El fichero peliculas.json no existe. El programa debe informar, indicando que se lea el fichero xml primero. NO debe detenerse, debe volver al menú.  
* Se introduce un ID inexistente al buscar, modificar o eliminar. Informar y volver al menú.  
* Se introduce un año no numérico o texto vacío en campos obligatorios.  Informar y volver al menú o solicitar datos correctos.   
* Se introduce una opción de menú no válida; Informar y volver al menú.  
* Se produce un error de entrada/salida al leer o guardar el fichero. Informar y volver al menú.

**Requisitos de robustez:** maneja las excepciones pertinentes (por ejemplo, IOException, JsonProcessingException y NumberFormatException), informa al usuario y evita que un error en una operación cierre inesperadamente el programa o destruya el catálogo válido.

# **Entrega**

* Proyecto Java completo y ejecutable, con Main.java, Pelicula.java, Catalogo.java (si se utiliza) y los demás archivos necesarios.  
  * Es recomendable tratar de separar las operaciones en clases en la medida de lo posible.  
* Fichero peliculas.xml inicial.  
* Fichero peliculas.json con los registros migrados desde el XML y los cambios realizados durante las pruebas.  
* Indicar la configuración de Jackson incluida en el proyecto (por ejemplo, pom.xml si se utiliza Maven).  
* Demostración obligatoria del funcionamiento: lectura inicial, búsqueda, alta, modificación, borrado y al menos una situación de error (más indicaciones durante la demostración).