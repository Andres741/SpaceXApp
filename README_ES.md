# SpaceXApp
Esta app ha sido creada por Andrés Conde Rodríguez para expandir mi portafolio.
La APK puede ser descargada en [este enlace](https://drive.google.com/file/d/1NRfdRdKiY0ufzRdQdvZ8NFljj1vyC5gs/view?usp=sharing "SpaceXApp APK")

## Qué hace la aplicación.

Esta app muestra una lista con los últimos lanzamientos de la compañía SpaceX, los detalles de cada 
lanzamiento, provee enlaces con más información, y permite ver y descargar las fotos de los 
lanzamientos y los barcos usados en las misiones.
Cuando la conexión a internet se pierde la app muestra que no puede cargar los datos, y cuando la 
conexión se restablece la app automáticamente vuelve a intentar cargar los datos.

## Cómo funciona la aplicación.

Primero de todo esta app hace uso de la arquitectura MVVM, usa repositorios e inyección de 
dependencias para construir las capas de arquitectura.

Todos los datos se obtienen de la API GraphQL de SpaceX usando el cliente Apollo Kotlin. Todas las 
consultas están contenidas en la clase LaunchRepository.

La app es capaz de reaccionar al estado de conexión a internet gracias a las funciones del archivo 
kotlin Loading.kt, pero la implementación de esta funcionalidad en los ViewHolders se ha hecho 
gracias a la  clase DownloadingImagesCache.
