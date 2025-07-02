# cities

![image](https://github.com/user-attachments/assets/9f08a0f4-4c17-44bf-9237-21a906404a31)


En esta oportunidad el desarrollo de esta aplicacion se hizo utilizando clean arquitecture
para poder separar en capas las responsabilidades.

El patron de arquitectura android utilizado fue : MVVM

Capas utilizadas en mi aplicación:

Domain : El cerebro de la app. Aquí viven las reglas de negocio esenciales y las
operaciones clave definidas en funciones dentro de los repositories que son interfaces y que definen 
lo que la app puede hacer (ej. buscar ciudades, marcar favoritas). No depende de nada externo.

Data :  Se encarga de obtener y almacenar datos (desde
una API o base de datos). El Domain le "pide" datos sin saber de dónde vienen, y la capa de Datos
se encarga de conseguirlos.

Presentation: La cara de la app. Representa los elementos visuales y con lo que intercatúa el usuario.
Jetpack  Compose construye la interfaz, mientras los View Models preparan los datos del Dominio para
mostrarlos y manejan tus acciones.


Techstack:
Kotlin, Jetpack Compose para la UI, Kotlin Flow para datos reactivos, Room para base de datos,
Paging 3 para listas grandes, y Hilt para la inyeccion y manejo de dependencias.
