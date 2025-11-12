Sistema de Gestión de Reservas con Análisis Predictivo ML

Trabajo Práctico - Programación de Vanguardia
Universidad de la Ciudad de Buenos Aires - Año: 2025

Integrantes del Equipo:

Guido Rearte

Katherike Monduela

Gustavo Fernandez

Julio Duhalde

Gianluca D'Archivio

Silvina Perez Heredia

Descripción del Proyecto
Sistema web completo de gestión de reservas con arquitectura de microservicios que integra:

Frontend moderno con React 18 + Vite

Backend robusto con Java Spring Boot 3

Microservicio de analítica con Python FastAPI

Machine Learning real con Prophet para predicciones de ocupación

Sistema de roles con control de acceso diferenciado

Validación de conflictos para evitar reservas duplicadas

Autenticación JWT y seguridad Spring Security 6

Visualización de datos con Recharts

Bases de datos separadas: MySQL para transacciones, PostgreSQL para analítica

Funcionalidades Principales
Gestión de Reservas

Crear, consultar, editar y eliminar reservas

Asociar personas, salas y artículos

Validación automática de disponibilidad (evita conflictos de horarios)

Interfaz intuitiva con tablas responsivas

Controles de acceso basados en roles (solo admin puede editar/eliminar)

Autenticación y Seguridad

Registro e inicio de sesión

Tokens JWT con expiración

Sistema de roles (ROLE_USER, ROLE_ADMIN)

Rol incluido como claim en JWT

Rutas protegidas con verificación de permisos

Manejo automático de sesiones

Control de Acceso por Roles 

ROLE_USER (Usuario Estándar):

Ver todas las reservas

Crear nuevas reservas

Ver estadísticas básicas

Acceso denegado a funciones administrativas

ROLE_ADMIN (Administrador):

Todas las funciones de ROLE_USER

Editar reservas existentes

Eliminar reservas

Acceso a análisis predictivo con ML

Badge visual "ADMIN" en interfaz

Validación de Disponibilidad 

Prevención de conflictos: Detecta automáticamente si hay reservas superpuestas

Validación en tiempo real: Antes de crear o actualizar reservas

Respuesta HTTP 409 CONFLICT: Cuando hay superposición de horarios

Mensajes claros: Informa al usuario sobre el conflicto detectado

Algoritmo robusto: Valida tanto salas como artículos separadamente

Estadísticas Básicas

Total de reservas

Reservas por sala

Reservas por artículo

Actualización en tiempo real

Análisis Predictivo con Machine Learning

Sincronización automática de datos históricos (MySQL → PostgreSQL)

Entrenamiento de modelo Prophet con series temporales

Predicciones de ocupación para los próximos 30 días

Gráficos profesionales con Recharts:

Línea de predicción principal

Área de confianza (límites superior e inferior)

Tooltips interactivos

Eje temporal con fechas futuras

Métricas visuales:

Cantidad de predicciones generadas

Ocupación promedio estimada

Modelo utilizado (Prophet ML)

Acceso restringido a administradores

Stack Tecnológico Completo
Frontend

React 18.2.0 - Biblioteca de interfaz de usuario

Vite 5.0.8 - Build tool moderno y rápido

React Router DOM 6.8.1 - Enrutamiento del lado del cliente

Axios - Cliente HTTP para consumir APIs

Tailwind CSS 3.4.17 - Framework de estilos utility-first

Recharts  - Librería de gráficos para visualización ML

Context API - Gestión de estado global con soporte de roles 

Backend - Microservicio de Reservas (Java)

Java 17 - Lenguaje de programación

Spring Boot 3.2.0 - Framework principal

Spring Web - API REST

Spring Data JPA - Capa de persistencia

Hibernate - ORM

Spring Security 6 - Autenticación y autorización

@EnableMethodSecurity - Control de acceso a nivel de método

@PreAuthorize - Restricciones por rol en endpoints

JSON Web Tokens (JWT) - Gestión de tokens con claims de rol 

MySQL 8.0.34 - Base de datos transaccional

SpringDoc OpenAPI 2.2.0 - Documentación Swagger

Lombok - Reducción de código boilerplate

Backend - Microservicio de Analítica (Python)

Python 3.9+ - Lenguaje de programación

FastAPI - Framework web moderno y rápido

Uvicorn - Servidor ASGI

Prophet  - Framework de ML para series temporales (Meta/Facebook)

Pandas  - Manipulación de datos para ML

SQLAlchemy  - ORM para PostgreSQL

psycopg2-binary  - Driver PostgreSQL

Requests - Cliente HTTP

Infraestructura

Docker & Docker Compose - Orquestación de contenedores

PostgreSQL 15  - Base de datos analítica

MySQL 8.0.34 - Base de datos transaccional

Maven 3.9+ - Gestión de dependencias Java

npm 8.19+ - Gestión de dependencias JavaScript

Git - Control de versiones

Instalación y Ejecución
Prerrequisitos

Docker Desktop instalado y corriendo

Node.js 18+ y npm 8.19+ instalados

Maven 3.9+ (para compilar el proyecto Java)

Git

Opción 1: Arranque Automático con Script (Recomendado)
Windows PowerShell:

# 1. Navegar a la carpeta Java
cd Migracion_Java

# 2. Ejecutar script de arranque
.\arranque.ps1

# 3. En otra terminal, iniciar React
cd ..\Vistas_React
npm run dev


El script automáticamente:

Verifica Docker Desktop

Limpia contenedores anteriores

Construye y levanta los 4 servicios (MySQL, PostgreSQL, Java, Python)

Espera a que los servicios estén listos

(NOTA: El script ya no crea el admin, ver "Creación del Usuario Administrador" en la Guía de Instalación del proyecto)

Abre React en una ventana separada

(Nota: He actualizado esta sección para que coincida con nuestro último script arranque.ps1 (v5), que requiere la creación manual del admin)

Opción 2: Arranque Manual Paso a Paso

Compilar el Proyecto Java

cd Migracion_Java
mvn clean package -DskipTests


Levantar los Servicios Backend

docker-compose up -d


Esto iniciará:

reservas-mysql en puerto 3307 (host) → 3306 (contenedor)

postgres-analytics en puerto 5432

reservas-app en puerto 8080

analitica-app en puerto 8000

Verificar que los Servicios Están Corriendo

docker-compose ps


Se debería ver:

NAME                  STATUS              PORTS
reservas-mysql        Up (healthy)        0.0.0.0:3307->3306/tcp
postgres-analytics    Up (healthy)        0.0.0.0:5432->5432/tcp
reservas-app          Up                  0.0.0.0:8080->8080/tcp
analitica-app         Up                  0.0.0.0:8000->8000/tcp


Espera 30-40 segundos para que Java termine de iniciar. Verifica los logs:

docker-compose logs reservas-app --tail 20


Busca el mensaje: Started GestionReservasApplication in X.XXX seconds

Iniciar el Frontend React

# Abrir una nueva terminal
cd ../Vistas_React

# Instalar dependencias (solo la primera vez)
npm install

# Iniciar el servidor de desarrollo
npm run dev


El servidor de Vite se iniciará en: http://localhost:5173

Acceder a la Aplicación
Abre tu navegador en: http://localhost:5173

🚦 Checklist Post-Instalación (¡MUY IMPORTANTE!)

Después de arrancar el sistema, DEBES crear el usuario administrador manualmente.

Verificar que los 4 contenedores están Up (healthy) o Up ejecutando docker ps.

Ir a http://localhost:5173

Registrar un usuario con:

Email: admin@test.com

Password: admin123

Abrir una terminal PowerShell y ejecutar el comando para promover ese usuario a ADMIN:

docker exec -it reservas-mysql mysql -ureservas_user -preservas123 reservas_db -e "UPDATE users SET role='ROLE_ADMIN' WHERE email='admin@test.com';"


Verificar que el rol se aplicó:

docker exec -it reservas-mysql mysql -ureservas_user -preservas123 reservas_db -e "SELECT id, email, role FROM users;"


Deberías ver:

+----+----------------+------------+
| id | email          | role       |
+----+----------------+------------+
| ...| ...            | ...        |
| X  | admin@test.com | ROLE_ADMIN |
+----+----------------+------------+


Volver al navegador, cerrar sesión (si estabas logueado) y volver a iniciar sesión con admin@test.com / admin123.

Verificación Visual:

Badge amarillo "ADMIN" en navbar

Link "Análisis Predictivo"

Botones "Editar" y "Eliminar" en reservas

Si NO ves estos elementos:

Ejecuta localStorage.clear() en la consola del navegador (F12)

Recarga la página (F5)

Haz login nuevamente

Uso de la Aplicación
1. Registro de Usuario

Abre http://localhost:5173

Haz clic en "Registrarse"

Completa el formulario:

Email: usuario@ejemplo.com

Contraseña: mínimo 6 caracteres

Por defecto, se crea con ROLE_USER

Serás redirigido automáticamente al login

2. Inicio de Sesión

IMPORTANTE SOBRE ROLES:
Los roles se determinan al momento del login y se guardan en el token JWT. Si cambias roles en la base de datos, DEBES:

Cerrar sesión en la aplicación

Limpiar localStorage: localStorage.clear()

Hacer login nuevamente

Credenciales de Prueba:

Como Usuario Estándar:

Email: prueba@test.com (Regístralo tú mismo)

Password: 123456

Rol: ROLE_USER (permisos limitados)

Qué se puede hacer:

Ver todas las reservas

Crear nuevas reservas

Ver estadísticas

NO editar/eliminar reservas

NO acceder a análisis predictivo

Como Administrador: 

Email: admin@test.com (Debes crearlo siguiendo el Checklist Post-Instalación)

Password: admin123

Rol: ROLE_ADMIN (permisos completos)

Qué puedes hacer:

Todo lo de ROLE_USER +

Editar cualquier reserva

Eliminar cualquier reserva

Acceder a análisis predictivo con ML

Cómo verificar que eres admin:

Deberías ver badge amarillo "ADMIN" junto a tu email

Deberías ver link "Análisis Predictivo" en el navbar

Deberías ver botones "Editar" y "Eliminar" en cada reserva

Si eres admin pero NO ves estos elementos:
→ Ve a la sección de Troubleshooting: "Usuario admin no muestra privilegios"

El sistema guardará tu token JWT con el rol incluido. Serás redirigido a la página de Reservas.

3. Gestión de Reservas

Funcionalidades para TODOS los usuarios:

Ver reservas: La tabla muestra todas las reservas existentes

Crear reserva: Botón "Nueva Reserva" → Completa el formulario

Validación automática: Si hay conflicto, recibirás mensaje claro

Ejemplo: " La sala ya está reservada en ese horario"

Funcionalidades solo para ADMINISTRADORES: 

Editar reserva: Botón "Editar" en cada fila (solo visible para admin)

Eliminar reserva: Botón "Eliminar" con confirmación (solo visible para admin)

Diferencias visuales por rol:

Usuario estándar: Ve "Solo lectura" en lugar de botones

Administrador: Ve badge "ADMIN" en navbar + botones "Editar" y "Eliminar"

4. Ver Estadísticas Básicas

Haz clic en "Estadísticas" en el navbar

Verás:

Total de reservas

Reservas por sala

Reservas por artículo

Accesible para todos los usuarios

5. Análisis Predictivo con Machine Learning (Solo Administradores)

IMPORTANTE: Esta funcionalidad requiere ROLE_ADMIN

Paso 1: Sincronizar y Entrenar

Haz clic en "Análisis Predictivo" en el navbar

Si no eres admin: Verás mensaje "Acceso denegado: Solo administradores"

Presiona el botón "Sincronizar y Entrenar Modelo"

Espera 15-30 segundos (el proceso incluye):

Sincronización de 20 reservas históricas (MySQL → PostgreSQL)

Entrenamiento del modelo Prophet con series temporales

Generación de 30 predicciones futuras

Cálculo de intervalos de confianza

Paso 2: Visualizar Predicciones

Después del entrenamiento, se verá:

Mensaje de éxito:

Modelo entrenado exitosamente. 30 predicciones generadas.

Gráfico profesional con:

Línea azul: Predicción de ocupación

Área celeste: Intervalo de confianza (límites superior e inferior)

Eje X: Fechas futuras (próximos 30 días)

Eje Y: Número estimado de reservas

Tooltips interactivos: Al pasar el mouse sobre el gráfico

Tarjetas de métricas:

Predicciones generadas: 30

Ocupación promedio estimada: X.X reservas/día

Modelo utilizado: Prophet ML

Paso 3: Actualizar Predicciones

Si creas nuevas reservas, presiona " Sincronizar y Entrenar Modelo" de nuevo

El modelo se reentrenará con los datos actualizados

Las predicciones se ajustarán automáticamente

6. Validación de Conflictos 

Escenario de Conflicto:

Usuario A reserva Sala A: 15/11 de 09:00 a 11:00

Usuario B intenta reservar Sala A: 15/11 de 10:00 a 12:00

Lo que sucede:

Sistema detecta superposición de horarios

Backend responde con HTTP 409 CONFLICT o 400 BAD REQUEST

Frontend muestra mensaje claro:

La sala ya está reservada en ese horario

Lógica de validación:

Solapamiento detectado cuando: (Inicio1 < Fin2) AND (Fin1 > Inicio2)

Se valida en: Creación de reserva + Actualización de reserva

Se valida para: Salas Y Artículos por separado

7. Cerrar Sesión

Haz clic en "Cerrar Sesión" en el navbar

Serás redirigido al login

Tu token será eliminado

Troubleshooting

Error: Connection refused al servicio Java

# Ver logs del servicio Java
cd Migracion_Java
docker-compose logs reservas-app

# Reiniciar servicios
docker-compose down -v
docker-compose up -d


Error: Duplicate entry en MySQL

# Limpiar volumen de MySQL
docker-compose down -v
docker-compose up -d


Error: Puerto 8080 ya está en uso

# Identificar proceso
netstat -ano | findstr :8080

# Terminar proceso (reemplazar 12345 con el PID real)
taskkill /PID 12345 /F

# Reiniciar Docker Compose
docker-compose up -d


Frontend no se conecta al backend

# Verificar que todos los servicios estén corriendo
docker-compose ps

# Verificar que Vite esté corriendo
# Debería ver: "Local: http://localhost:5173/"

# Verificar conectividad
curl http://localhost:8080/swagger-ui.html    # Backend Java
curl http://localhost:8000/docs              # Backend Python


Error en entrenamiento de Prophet

# Ver logs de Python
docker-compose logs analitica-app --tail 50

# Reiniciar servicio Python
docker-compose restart analitica-app

# Si persiste, reconstruir
docker-compose up --build -d analitica-app


Gráfico ML no se muestra

Verifica que Recharts esté instalado:

cd Vistas_React
npm list recharts


Si no está instalado:

npm install recharts


Reinicia el servidor de desarrollo:

npm run dev


Error 403 Forbidden al intentar editar/eliminar 

Causa: Usuario con ROLE_USER intentando acceder a funciones de administrador

Solución:

Verifica tu rol actual en el navbar (debería ver badge "ADMIN" si eres admin)

Cierra sesión y vuelve a iniciar con credenciales de administrador:

Email: admin@test.com

Password: admin123

Si el problema persiste, verifica que el token JWT contenga el rol:

Abre DevTools → Console

Ejecuta:

const user = JSON.parse(localStorage.getItem('user'));
console.log('Rol:', user.role);
// Debería mostrar: "ROLE_ADMIN"


Error 409 Conflict al crear reserva 

Causa: Existe una reserva que se superpone en tiempo y recurso

Solución:

Lee el mensaje de error detallado que indica:

Qué recurso está en conflicto (Sala X o Artículo Y)

Horario de la reserva existente

Opciones:

Elige otro horario que no se superponga

Elige otro recurso (otra sala o artículo)

Si eres admin, verifica si la reserva existente es válida

Error: Usuario admin no muestra privilegios de administrador 

Síntomas:

Hiciste login con admin@test.com

El sistema te deja entrar PERO:

NO ves badge amarillo "ADMIN"

NO ves link "Análisis Predictivo"

NO ves botones "Editar" y "Eliminar"

Ves mensaje "Solo lectura"

Causa: Token JWT viejo en localStorage con rol ROLE_USER (probablemente de cuando te registraste antes de ser promovido).

Diagnóstico:

Abrir DevTools (F12) → Console

Ejecutar:

const user = JSON.parse(localStorage.getItem('user'));
console.log('Rol actual:', user.role);


Si muestra ROLE_USER en lugar de ROLE_ADMIN, es el problema.

Solución:

// En la consola del navegador
localStorage.clear();
location.reload();


Luego:

Hacer login nuevamente con admin@test.com / admin123

Verificar rol:

const user = JSON.parse(localStorage.getItem('user'));
console.log('Rol después del login:', user.role);
// Debería mostrar: "ROLE_ADMIN"


Verificación visual después del login:

Badge amarillo "ADMIN" junto al email

Link "Análisis Predictivo" en navbar

Botones "Editar" y "Eliminar" en cada reserva

Prevención:

Siempre hacer "Cerrar Sesión" antes de cambiar roles en la BD

Después de modificar roles en MySQL, limpiar localStorage

Los tokens JWT son inmutables - cambios en BD no actualizan tokens existentes

Si el problema persiste:

Verificar que el usuario tenga el rol correcto en la base de datos:

docker exec -it reservas-mysql mysql -u reservas_user -preservas123 reservas_db -e "SELECT id, email, role FROM users WHERE email = 'admin@test.com';"


Si muestra ROLE_USER, corregir:

docker exec -it reservas-mysql mysql -u reservas_user -preservas123 reservas_db -e "UPDATE users SET role = 'ROLE_ADMIN' WHERE email = 'admin@test.com';"


Luego limpiar localStorage y hacer login nuevamente.

Detener los Servicios

Backend:

# En la carpeta Migracion_Java

# Detener sin eliminar datos
docker-compose down

# Detener y eliminar volúmenes (limpia las bases de datos)
docker-compose down -v


Frontend:

# Presionar Ctrl + C en la terminal donde corre npm run dev


Consejos para Evitar Problemas

Siempre esperar 40-60 segundos después de docker-compose up -d antes de probar la aplicación

Usar docker-compose down -v cuando se quiera empezar limpio (borra las bases de datos)

Usar docker-compose down cuando se quieran mantener los datos

Verificar los logs antes de asumir que algo está mal: docker-compose logs reservas-app --tail 20

Si se modifica código Java, recompilar con mvn clean package -DskipTests antes de reiniciar

El frontend React tiene HMR: Los cambios se reflejan automáticamente sin reiniciar

Para entrenamiento ML, asegurarse de tener al menos 10-20 reservas históricas

El primer entrenamiento de Prophet puede tardar hasta 30 segundos

Para probar funciones de admin, usar las credenciales específicas de administrador

Si recibes error 409, revisa el mensaje para identificar el conflicto específico

Si cambias roles en la BD, SIEMPRE limpia localStorage antes de hacer login

Los tokens JWT son inmutables - cambios en la BD no afectan tokens ya generados

Para verificar tu rol actual: JSON.parse(localStorage.getItem('user')).role en consola

Si eres admin pero no ves privilegios, el problema es casi siempre un token viejo

Usa "Cerrar Sesión" en lugar de solo cerrar el navegador para evitar tokens obsoletos

URLs de Acceso Rápido
| Servicio | URL | Descripción |
| :--- | :--- | :--- |
| Frontend React | http://localhost:5173 | Interfaz de usuario principal |
| API Java | http://localhost:8080 | Backend de reservas |
| Swagger UI | http://localhost:8080/swagger-ui.html | Documentación interactiva Java |
| API Python | http://localhost:8000 | Servicio de analítica + ML |
| FastAPI Docs  | http://localhost:8000/docs | Documentación interactiva Python |
| MySQL | localhost:3307 | Base de datos (solo acceso local) |
| PostgreSQL  | localhost:5432 | Base de datos analítica (solo acceso local) |

Credenciales MySQL:

Usuario: reservas_user

Contraseña: reservas123

Base de datos: reservas_db

Credenciales PostgreSQL :

Usuario: analytics_user

Contraseña: analytics123

Base de datos: analytics_db

Credenciales de Prueba:

Usuario Estándar:

Email: prueba@test.com

Password: 123456

Rol: ROLE_USER

Administrador: 

Email: admin@test.com

Password: admin123

Rol: ROLE_ADMIN

Resumen de Comandos Clave
| Comando | Efecto |
| :--- | :--- |
| .\arranque.ps1 | Reinicio limpio + datos de prueba |
| docker-compose up -d | Levantar (mantiene datos) |
| docker-compose down | Detener (mantiene datos) |
| docker-compose down -v | Detener + borrar datos |
| docker ps | Ver estado de contenedores |
| docker-compose logs reservas-app --tail 20 | Ver logs de Java |
| npm run dev | Iniciar React |
| localStorage.clear() | Limpiar tokens viejos (en consola del navegador) |

Prueba Rápida de Funcionalidades Nuevas 
1. Probar Sistema de Roles:

Login como usuario estándar (prueba@test.com / 123456 - debes registrarlo):

✓ Puedes ver reservas

✓ Puedes crear reservas

✗ NO ves botones "Editar" ni "Eliminar"

✗ NO ves opción "Análisis Predictivo"

Logout y login como admin (admin@test.com / admin123 - debes crearlo y promoverlo):

✓ Ves badge "ADMIN" en navbar

✓ Ves botones "Editar" y "Eliminar" en reservas

✓ Ves opción "Análisis Predictivo"

✓ Puedes modificar/eliminar cualquier reserva

2. Probar Validación de Conflictos:

Identifica una reserva existente (ej: Sala A, 2024-12-03 09:00-11:00)

Intenta crear nueva reserva con conflicto:

Sala A

2024-12-03 10:00-12:00 (se superpone con existente)

Sistema detecta y muestra: "La sala ya está reservada en ese horario"

Ajusta horario a 14:00-16:00 (sin conflicto)

✓ Reserva creada exitosamente

🎓 Objetivo Académico
Este proyecto demuestra competencias en:

1. Arquitectura de Software Moderna

Arquitectura de microservicios con separación de responsabilidades

Comunicación entre servicios mediante API REST

Bases de datos especializadas (MySQL para transacciones, PostgreSQL para analítica)

Orquestación con Docker Compose

2. Desarrollo Full-Stack

Frontend moderno con React 18 y Vite

Backend robusto con Java Spring Boot

Microservicio especializado con Python FastAPI

Integración fluida entre tecnologías heterogéneas

3. Machine Learning Aplicado 

 Implementación de algoritmo Prophet para series temporales

Preprocesamiento de datos para ML

Entrenamiento y evaluación de modelos

Visualización de predicciones con intervalos de confianza

Integración de ML en aplicaciones web reales

4. Seguridad Web

Autenticación JWT con Spring Security

Autorización basada en tokens

Sistema de roles con control de acceso multinivel

@PreAuthorize para endpoints sensibles

Validación de permisos en frontend y backend

Configuración CORS para comunicación segura

Hashing de contraseñas con BCrypt

5. Validación y Lógica de Negocio 

Validación de conflictos con queries JPQL optimizadas

Prevención de inconsistencias de datos

Manejo de errores con códigos HTTP apropiados (409 Conflict)

Mensajes de error descriptivos para el usuario

Separación de lógica de validación (crear vs actualizar)

6. Buenas Prácticas de Desarrollo

Código organizado y modular

Separación de capas (Controller, Service, Repository)

Manejo de errores robusto

Documentación con Swagger/OpenAPI

Versionado con Git

7. DevOps y Despliegue

Contenerización con Docker

Orquestación con Docker Compose

Scripts de automatización (arranque.ps1)

Ambientes reproducibles

Arquitectura del Sistema
Diagrama de Microservicios

┌─────────────────────────────────────────────────────────────┐
│                       FRONTEND (React + Vite)               │
│                       Puerto: 5173                          │
│   - Interfaz de usuario                                     │
│   - Gestión de reservas                                     │
│   - Estadísticas básicas                                    │
│   -  Gráficos ML con Recharts                             │
│   -  UI condicional por rol (badges, botones)             │
│   -  Manejo de errores 403 (sin permisos)                 │
│   -  Manejo de errores 409 (conflictos)                   │
└────────────┬──────────────────────────────┬─────────────────┘
             │                              │
             ▼                              ▼
┌─────────────────────────┐      ┌─────────────────────────────┐
│ MICROSERVICIO RESERVAS  │      │ MICROSERVICIO ANALÍTICA     │
│   (Java Spring Boot)    │◄─────┤   (Python FastAPI)          │
│   Puerto: 8080          │      │   Puerto: 8000              │
│                         │      │                             │
│ - API REST              │      │ - Estadísticas básicas      │
│ - Autenticación JWT     │      │ -  Sincronización         │
│ - CRUD Reservas         │      │ -  Modelo Prophet         │
│ - Spring Security       │      │ -  Predicciones ML        │
│ -  Endpoint sync      │      │                             │
│ -  Sistema de roles   │      │                             │
│ -  Validación         │      │                             │
│     conflictos          │      │                             │
└────────┬────────────────┘      └──────────┬──────────────────┘
         │                                 │
         ▼                                 ▼
┌─────────────────────┐      ┌────────────────────────────┐
│   MySQL 8.0         │      │   PostgreSQL 15          │
│   Puerto: 3307      │      │   Puerto: 5432             │
│                     │      │                            │
│ - users           │      │ - reservas_snapshot        │
│   + campo role      │      │ - predictions              │
│ - personas          │      │                            │
│ - salas             │      │ (Almacena datos para ML)   │
│ - articulos         │      │                            │
│ - reservas          │      │                            │
└─────────────────────┘      └────────────────────────────┘


Flujo de Validación de Conflictos 

1. Usuario intenta crear/editar reserva
           │
           ▼
2. Frontend envía: POST /api/reservas (o PUT)
   {
     "persona": {"id": 1},
     "sala": {"id": 2},
     "fechaHoraInicio": "2024-11-15T09:00:00",
     "fechaHoraFin": "2024-11-15T11:00:00"
   }
           │
           ▼
3. ReservaService llama a validarDisponibilidad()
           │
           ▼
4. ReservaRepository ejecuta query JPQL:
   SELECT COUNT(r) FROM Reserva r
   WHERE r.sala.id = :salaId
   AND r.fechaHoraInicio < :fin
   AND r.fechaHoraFin > :inicio
   [AND r.id != :reservaId] // Si es UPDATE
           │
           ├─→ COUNT > 0: HAY CONFLICTO
           │       │
           │       └─→ Lanza ResponseStatusException(400/409)
           │               │
           │               └─→ Frontend intercepta y muestra mensaje
           │
           └─→ COUNT = 0: NO HAY CONFLICTO
                   │
                   └─→ Guarda reserva normalmente


 Seguridad
Autenticación JWT con Roles 
Flujo completo:

Registro/Login:

Usuario envía credenciales

Backend valida y genera JWT

 JWT incluye claim "role": {"email": "admin@test.com", "role": "ROLE_ADMIN"}

Token enviado al frontend

Almacenamiento:

Frontend guarda token en localStorage

AuthContext decodifica token y extrae rol

Verificación en cada petición:

Axios interceptor agrega token automáticamente: Authorization: Bearer {token}

Backend valida firma y extrae rol del token

Spring Security verifica permisos según anotaciones

Endpoints protegidos por rol:

// Solo administradores
@PreAuthorize("hasRole('ADMIN')")
@PutMapping("/{id}")
public ResponseEntity<?> actualizarReserva(...) { ... }

@PreAuthorize("hasRole('ADMIN')")
@DeleteMapping("/{id}")
public ResponseEntity<?> eliminarReserva(...) { ... }

@PreAuthorize("hasRole('ADMIN')")
@PostMapping("/sync-analytics")
public ResponseEntity<?> sincronizarAnalytics() { ... }

// Todos los usuarios autenticados
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
@GetMapping
public ResponseEntity<?> listarReservas() { ... }

@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
@PostMapping
public ResponseEntity<?> crearReserva(...) { ... }


Manejo de errores de autorización:

HTTP 401 Unauthorized: Token inválido o expirado → Redirige a login

HTTP 403 Forbidden: Usuario sin permisos → Muestra mensaje "Acceso denegado" 

HTTP 409 Conflict: Conflicto de reserva → Muestra detalles del conflicto 

CORS

Backend configurado para aceptar peticiones desde:

http://localhost:5173 (Frontend React con Vite)

http://localhost:3000 (Alternativa)

Configurado en múltiples capas: WebConfig, SecurityConfig, @CrossOrigin

Contraseñas

Hasheadas con BCrypt antes de guardar en base de datos

Nunca se almacenan en texto plano

Validación de Disponibilidad 

Previene: Reservas simultáneas del mismo recurso

Validación: A nivel de servicio antes de persistir

Queries optimizadas: Índices en fechas para performance

Algoritmo robusto:

Detecta superposición: (Inicio1 < Fin2) AND (Fin1 > Inicio2)

Excluye reserva actual en UPDATE (no detecta conflicto consigo misma)

Valida salas y artículos por separado

 Documentación de la API
Swagger - Microservicio Java

URL: http://localhost:8080/swagger-ui.html

Cómo Autorizar en Swagger:

Registra un usuario usando POST /api/auth/register

Inicia sesión con POST /api/auth/login para obtener el token JWT

Haz clic en el botón Authorize (esquina superior derecha)

Ingresa: Bearer <tu-token-jwt>

Haz clic en Authorize y cierra la ventana

¡Listo! Ahora puedes probar todos los endpoints protegidos

 Probar endpoints de admin:

Loguea con admin@test.com / admin123 para obtener token con ROLE_ADMIN

Solo con este token podrás acceder a endpoints con @PreAuthorize("hasRole('ADMIN')")

FastAPI Docs - Microservicio Python 

URL: http://localhost:8000/docs

Documentación interactiva automática de todos los endpoints de analítica y ML.

📁 Estructura del Proyecto

Reservas_(java_c_security_python_react)/
│
├── Migracion_Java/                 # Backend Java
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/reservas/
│   │   │   │   ├── controller/     # Controladores REST
│   │   │   │   │   ├── AuthController.java
│   │   │   │   │   ├── ReservaController.java (+ sync-analytics )
│   │   │   │   │   ├── PersonaController.java
│   │   │   │   │   ├── SalaController.java
│   │   │   │   │   └── ArticuloController.java
│   │   │   │   ├── service/          # Lógica de negocio
│   │   │   │   │   ├── AnalyticsService.java 
│   │   │   │   │   ├── ReservaService.java (+ validación conflictos )
│   │   │   │   │   └── ...
│   │   │   │   ├── repository/       # Acceso a datos (JPA)
│   │   │   │   │   ├── ReservaRepository.java (+ queries conflictos )
│   │   │   │   │   └── ...
│   │   │   │   ├── model/            # Entidades JPA
│   │   │   │   │   ├── User.java (+ campo role )
│   │   │   │   │   ├── Role.java  (Enum ROLE_USER, ROLE_ADMIN)
│   │   │   │   │   └── ...
│   │   │   │   ├── dto/              # Data Transfer Objects
│   │   │   │   │   └── SyncRequest.java 
│   │   │   │   ├── security/         # Configuración JWT y CORS
│   │   │   │   │   ├── JwtService.java (+ rol como claim )
│   │   │   │   │   ├── SecurityConfig.java (+ @EnableMethodSecurity )
│   │   │   │   │   └── ...
│   │   │   │   ├── config/           # Configuraciones generales
│   │   │   │   └── GestionReservasApplication.java
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       └── data.sql          # (Script de datos ahora en arranque.ps1)
│   │   └── test/                   # Tests unitarios
│   ├── docker-compose.yml          # Orquestación (4 servicios)
│   ├── Dockerfile                  # Imagen del servicio Java
│   ├── entrypoint.sh               # Script de inicio
│   ├── arranque.ps1              # Script de arranque automático (v5)
│   └── pom.xml                     # Configuración Maven
│
├── Analitica_Python/               # Microservicio Python
│   ├── main.py                     # API FastAPI (+ endpoints ML )
│   ├── requirements.txt            # Dependencias (+ Prophet )
│   ├── Dockerfile
│   ├── .env                      # Variables de entorno PostgreSQL
│   ├── database/                 # Capa de base de datos
│   │   ├── __init__.py
│   │   ├── connection.py           # SQLAlchemy Engine
│   │   └── models.py               # ReservaSnapshot, Prediction
│   ├── services/                 # Servicios de negocio
│   │   ├── __init__.py
│   │   ├── sync_service.py         # Sincronización de datos
│   │   └── ml_service.py           # Machine Learning con Prophet
│   └── utils/                    # Utilidades
│       ├── __init__.py
│       └── date_helpers.py         # Helpers de fechas
│
└── Vistas_React/                   # Frontend React
    ├── src/
    │   ├── components/             # Componentes reutilizables
    │   │   ├── Navbar.jsx (+ link Análisis Predictivo  + badge admin )
    │   │   ├── Footer.jsx
    │   │   ├── PrivateRoute.jsx
    │   │   └── AdminRoute.jsx    # Rutas protegidas para admin
    │   ├── pages/                  # Páginas principales
    │   │   ├── Home.jsx
    │   │   ├── Login.jsx
    │   │   ├── Register.jsx
    │   │   ├── Reservas.jsx (+ botones condicionales por rol )
    │   │   ├── Analytics.jsx
    │   │   └── PredictiveAnalytics.jsx   # Página ML con gráficos
    │   ├── services/               # Servicios de API
    │   │   ├── api.js (+ interceptor 403/409 )
    │   │   ├── authService.js
    │   │   ├── reservasService.js
    │   │   └── ... (y más servicios)
    │   ├── context/                # Context API
    │   │   └── AuthContext.jsx (+ funciones rol : isAdmin(), hasRole())
    │   ├── App.jsx (+ ruta /prediccion  + AdminRoute )
    │   └── main.jsx
    ├── package.json (+ recharts )
    ├── vite.config.js
    └── tailwind.config.js


 Logros Técnicos Destacables
1. Arquitectura Real de Microservicios

No es una simple aplicación monolítica dividida

Cada servicio tiene su propia base de datos (patrón Database per Service)

Comunicación asíncrona mediante HTTP REST

Servicios pueden escalar independientemente

2. Machine Learning en Producción

No es un notebook de Jupyter aislado

ML integrado en una aplicación web funcional

Pipeline completo: ingesta de datos → entrenamiento → predicción → visualización

Modelo reentrenable con datos actualizados

3. Sistema de Roles Robusto 

No es solo UI condicional

Control de acceso en múltiples capas:

JWT con claims de rol

Spring Security con @PreAuthorize

Frontend con AuthContext

Enum Role tipado en backend

AdminRoute para rutas protegidas

Validación tanto en creación como edición

4. Validación de Conflictos Inteligente 

No es solo validación de frontend

Queries JPQL optimizadas con índices

Lógica robusta: (Inicio1 < Fin2) AND (Fin1 > Inicio2)

Métodos separados para crear vs actualizar

Manejo apropiado de códigos HTTP (409 Conflict)

Mensajes de error descriptivos

5. Frontend Moderno y Profesional

No es un CRUD básico con Bootstrap

SPA con React Router y navegación fluida

Gráficos interactivos con Recharts

Estado global con Context API

Interceptores Axios para manejo automático de JWT

 UI condicional basada en roles

 Manejo de errores 403 y 409

6. Seguridad Implementada Correctamente

No es solo un login simple

JWT con firma HMAC y expiración

Spring Security con filtros personalizados

CORS configurado en múltiples capas

Contraseñas hasheadas con BCrypt (nunca en texto plano)

 Control de acceso basado en roles

 Protección de endpoints sensibles

7. DevOps y Automatización

No es solo "npm start" y "java -jar"

Docker Compose orquesta 4 servicios

Script de arranque automatizado

Health checks para garantizar disponibilidad

Volúmenes persistentes para datos

 Recursos de Aprendizaje

Prophet (Meta)

Documentación oficial: https://facebook.github.io/prophet/

Paper original: https://peerj.com/preprints/3190/

GitHub: https://github.com/facebook/prophet

Spring Boot

Documentación oficial: https://spring.io/projects/spring-boot

Spring Security: https://spring.io/projects/spring-security

 Method Security: https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html

Spring Data JPA: https://spring.io/projects/spring-data-jpa

React

Documentación oficial: https://react.dev/

React Router: https://reactrouter.com/

Recharts: https://recharts.org/

FastAPI

Documentación oficial: https://fastapi.tiangolo.com/

Tutorial: https://fastapi.tiangolo.com/tutorial/

Docker

Documentación oficial: https://docs.docker.com/

Docker Compose: https://docs.docker.com/compose/

 Historial de Versiones
v4.1.0 - 2025-01-XX  (VERSIÓN ACTUAL)
Nuevas Funcionalidades Críticas:

Sistema de Roles:

 Enum Role.java con ROLE_USER y ROLE_ADMIN

 Campo role en entidad User (VARCHAR(20), NOT NULL, default ROLE_USER)

 JwtService incluye rol como claim en el token

 SecurityConfig con @EnableMethodSecurity y restricciones por rol

 @PreAuthorize en endpoints sensibles (editar, eliminar, ML)

 AuthContext en React con funciones isAdmin() y hasRole()

 Componente AdminRoute.jsx para rutas protegidas

 UI condicional en Navbar.jsx (badge "ADMIN")

 UI condicional en Reservas.jsx (botones editar/eliminar)

Validación de Disponibilidad:

 Queries JPQL en ReservaRepository:

findConflictingSalaReservations()

findConflictingArticuloReservations()

 Validación con lógica: (Inicio1 < Fin2) AND (Fin1 > Inicio2)

 Excepciones ResponseStatusException con código 409 CONFLICT / 400 BAD REQUEST

 Métodos separados para crear vs actualizar (excluye propia reserva en updates)

 Interceptor Axios para capturar errores 409 y 403

 Mensajes de error descriptivos en frontend

 Validación tanto para salas como artículos

Mejoras de Seguridad:

 Protección multinivel (JWT + Spring Security + Frontend)

 Manejo apropiado de HTTP 403 Forbidden

 Manejo apropiado de HTTP 409 Conflict

Mejoras de UX:

 Feedback visual claro sobre permisos de usuario

 Mensajes de error específicos y accionables

 Prevención de acciones no permitidas desde UI

Mejoras de Documentación:

 Sección de Troubleshooting ampliada con problema de token viejo

 Checklist post-instalación para verificar configuración

 Guía detallada sobre roles y permisos

 Comandos de diagnóstico en consola del navegador

v2.0.0 - 2025-01-XX
Nuevas Funcionalidades:

 Análisis Predictivo con Machine Learning (Prophet)

 Microservicio Python ampliado con ML

 Base de datos PostgreSQL para analítica

 Gráficos interactivos con Recharts

 Endpoint de sincronización Java → Python

 Página de Análisis Predictivo en React

 Predicciones de ocupación para 30 días

 Intervalos de confianza en predicciones

 Script de arranque automatizado (arranque.ps1)
Mejoras Técnicas:

 Arquitectura de microservicios completa

 Separación de bases de datos (MySQL + PostgreSQL)

 Pipeline ML end-to-end funcional

 Manejo robusto de fechas (LocalDateTime → datetime)

 Documentación exhaustiva

v1.0.0 - 2024-12-XX
Funcionalidades Iniciales:

 Sistema de gestión de reservas CRUD

 Autenticación JWT con Spring Security

 Frontend React con Tailwind CSS

 Backend Java con Spring Boot

 Microservicio Python básico para estadísticas

 Base de datos MySQL

 Docker Compose para orquestación

 Documentación Swagger

 Conclusión
Este proyecto representa una implementación completa y funcional de un sistema de gestión de reservas con capacidades de Machine Learning predictivo y control de acceso robusto basado en roles, demostrando competencias en:

 Desarrollo Full-Stack moderno

 Arquitectura de microservicios

 Integración de Machine Learning en producción

 Seguridad web con JWT y control de acceso por roles 

 Validación de lógica de negocio (prevención de conflictos) 

 Visualización de datos interactiva

 DevOps con Docker

 Buenas prácticas de desarrollo

Nuevas capacidades destacadas (v2.1.0): 

Sistema de Roles Completo: Control de acceso diferenciado entre usuarios estándar y administradores, implementado en todas las capas del sistema

Validación de Conflictos: Prevención inteligente de reservas duplicadas con detección automática de superposiciones de horarios

Experiencia de Usuario Mejorada: Feedback visual claro sobre permisos y conflictos, con mensajes descriptivos y accionables

Documentación Profesional: Guías detalladas de troubleshooting y verificación post-instalación

El sistema es completamente funcional, escalable y está listo para ser presentado y defendido ante evaluadores académicos o técnicos.

Contribuciones
Este proyecto fue desarrollado como trabajo práctico académico por el equipo de estudiantes de Programación de Vanguardia en la Universidad de la Ciudad de Buenos Aires.

Contacto
Para consultas sobre el proyecto, contactar a cualquiera de los integrantes del equipo mencionados al inicio de este documento.

Licencia
Este proyecto es un trabajo académico desarrollado con fines educativos.

Agradecimientos
Universidad de la Ciudad de BuenosS Aires por la formación y recursos

Meta (Facebook) por el framework Prophet

Spring Team por Spring Boot y Spring Security

FastAPI Team por el framework moderno de Python

React Team por la librería de UI

Recharts Team por la librería de gráficos

Comunidad Open Source por las herramientas y documentación