# ClubApp — Parcial 2 (Aplicaciones Móviles)

Aplicación Android para socios de un club social y deportivo. Permite iniciar
sesión, ver el carnet de socio, reservar actividades, anotarse a eventos y
gestionar el perfil.

## Pantallas
| Pantalla | Qué hace |
| --- | --- |
| **Login** | Inicio de sesión con email y contraseña (Firebase Authentication), recordar sesión y recuperar contraseña por mail. |
| **Inicio** | Carnet del socio, sus actividades, "Mis Inscripciones Confirmadas" y próximos eventos. |
| **Actividades** | Buscador y filtros por categoría. Permite reservar una actividad. |
| **Eventos** | Torneos y eventos sociales descargados desde internet. Permite anotarse. |
| **Perfil** | Membresía, datos personales, cambio de foto de perfil y cierre de sesión. |

La navegación entre pantallas se hace con una barra inferior fija. El ícono
del encabezado de Inicio también abre el Perfil.

## Funcionalidades
- **Login con Firebase Authentication**: valida email y contraseña, muestra
  errores creados dinámicamente desde Java y puede recordar la sesión.
- **Estado compartido entre pantallas**: lo que el socio reserva en Actividades
  o Eventos aparece en "Mis Inscripciones Confirmadas" de Inicio, donde puede
  cancelarse. Los botones se actualizan en todas las pantallas.
- **Eventos desde URL**: se leen de un archivo JSON publicado en este
  repositorio (`data/events.json`) con OkHttp y Gson. Si no hay conexión, se
  muestran los últimos eventos guardados en la app.
- **Imágenes desde URL**: se descargan con Glide.
- **Foto de perfil**: se elige desde la galería del celular, se guarda en la app
  y se muestra en Perfil y en el carnet de Inicio.
- **Datos entre pantallas**: el nombre y el email del socio viajan del Login a
  Inicio como extras del Intent.
- **Buscadores y filtros** en Actividades y Eventos.

## Tecnologías
- Java + XML (ConstraintLayout, LinearLayout, ScrollView, Button, TextView)
- Firebase Authentication
- OkHttp + Gson
- Glide

## Cómo probarla
Cuenta de prueba: `mariano@clubdeportivo.com` / `club2026`

## Documentación
- Figma: https://www.figma.com/design/SnhrF6bN1Gf0BeMzxbJTzS/ClubApp?node-id=0-1&p=f&t=q43mXr4zFYS8abFS-0
- [Informe del Parcial 2](docs/ClubApp_Parcial2.pdf)

## Alumna
Barbara Linda Clavijo
