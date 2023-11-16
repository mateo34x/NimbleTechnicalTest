
Fecha de inicio = 8/11/2023
Fecha de fin = 14/11/2023

Funciones de la aplicación
Autenticación:

    ✓ Implementar la pantalla de autenticación para el inicio de sesión.
    ✓ Implementar la autenticación OAuth, incluyendo el almacenamiento de tokens de acceso.
    ✓ Implementar el uso automático de tokens de actualización para mantener al usuario conectado utilizando la API de OAuth.

Pantalla de inicio:
    En la pantalla de inicio, cada tarjeta de encuesta debe mostrar la siguiente información:
        ✓ Imagen de portada (fondo)
        ✓ Nombre (en negrita)
        ✓ Descripción

    Deben existir 2 acciones:
        ✓ Desplazamiento horizontal a través de las encuestas.
        ✓ Un botón "Tomar encuesta" debe llevar al usuario a la pantalla de detalles de la encuesta.

    ✓ La lista de encuestas debe ser recuperada al abrir la aplicación.
    ✓ Mostrar una animación de carga al obtener la lista de encuestas. (fade in)
    ✓ El indicador de navegación de la lista (puntos) debe ser dinámico y basarse en la respuesta de la API.
        Funciones opcionales





    Si tienes tiempo adicional y deseas ir un paso más allá 💪, puedes trabajar en estos requisitos opcionales:



    Pantalla de inicio (Opcionales):
      x Autenticación: implementar la pantalla de recuperación de contraseña olvidada.
      ✓ Implementar el almacenamiento en caché de las encuestas en el dispositivo.
      x Implementar una acción de "tirar para refrescar" para actualizar la lista de encuestas.
      ✓ Mostrar una animación de carga al refrescar la lista de encuestas.


      Requerimientos técnicos
      Desarrollar la aplicación usando:
        ✓ Kotlin
        ✓ Android Studio
        ✓ Gradle
        ✓ Compatible con Android 5.0 (API 21) y versiones posteriores.
        - Utilice Git durante el proceso de desarrollo. Enviar a un repositorio público en Bitbucket, Github o Gitlab. Realice confirmaciones periódicas y combine código mediante solicitudes de extracción.
          Escriba pruebas unitarias utilizando el marco de su elección.
        ✓ Utilice los puntos finales REST o GraphQL. La decision es tuya. Asegúrese de utilizar las URL de producción.



Cosas por agregar
    Logout implementando la revocación del token de acceso
    Animaciones en el splashScreen y la pantalla de inicio de sesión
    Texto "Forgot Password?" en el EditText de contraseña
