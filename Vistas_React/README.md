Sistema de Reservas - Frontend React
Interfaz de Usuario del Sistema de Gestión de Reservas
Trabajo Práctico - Programación de Vanguardia
Universidad de la Ciudad de Buenos Aires - 2025

📋 Descripción
Aplicación web frontend desarrollada con React 18 y Vite que proporciona una interfaz moderna e intuitiva para el sistema de gestión de reservas. Incluye autenticación JWT, control de acceso basado en roles, validación de conflictos y visualización de análisis predictivo con Machine Learning.

✨ Características Principales
Gestión de Reservas

✅ Visualización de todas las reservas en tabla responsiva
✅ Creación de nuevas reservas con formulario validado
✅ 🆕 Edición de reservas (solo administradores)
✅ 🆕 Eliminación de reservas (solo administradores)
✅ 🆕 Validación automática de conflictos de horarios
✅ Asociación de personas, salas y artículos

Autenticación y Seguridad

✅ Registro de nuevos usuarios
✅ Inicio de sesión con JWT
✅ 🆕 Sistema de roles (USER, ADMIN)
✅ 🆕 UI condicional según permisos
✅ Rutas protegidas con PrivateRoute
✅ 🆕 Rutas exclusivas de admin con AdminRoute
✅ Cierre de sesión con limpieza de tokens

Estadísticas y Análisis

✅ Dashboard con métricas básicas
✅ Total de reservas
✅ Reservas por sala
✅ Reservas por artículo
✅ 🆕 Análisis Predictivo con ML (solo admin)
✅ 🆕 Gráficos interactivos con Recharts

Experiencia de Usuario

✅ SPA con navegación fluida (sin recargas)
✅ Hot Module Replacement (HMR) en desarrollo
✅ Diseño responsivo con Tailwind CSS
✅ 🆕 Badges visuales de rol (USER/ADMIN)
✅ 🆕 Mensajes de error específicos
✅ Indicadores de carga
✅ Manejo de errores amigable


🛠️ Tecnologías Utilizadas
TecnologíaVersiónPropósitoReact18.2.0Biblioteca de UIVite5.0.8Build tool y dev serverReact Router DOM6.8.1Enrutamiento SPAAxios1.6.5Cliente HTTPTailwind CSS3.4.17Framework de estilosRecharts 🆕2.xGráficos de MLContext APIBuilt-inGestión de estado global

🚀 Instalación y Ejecución
Prerrequisitos

Node.js 18+ instalado
npm 8.19+ instalado
Backend Java corriendo en http://localhost:8080
Backend Python corriendo en http://localhost:8000

Instalación
bash# 1. Navegar a la carpeta del frontend
cd Vistas_React

# 2. Instalar dependencias
npm install
Ejecución en Desarrollo
bash# Iniciar servidor de desarrollo con HMR
npm run dev
La aplicación estará disponible en: http://localhost:5173
Build para Producción
bash# Compilar para producción
npm run build

# Vista previa del build
npm run preview
Los archivos compilados estarán en la carpeta dist/.

📁 Estructura del Proyecto
Vistas_React/
│
├── public/                      # Archivos estáticos
│   └── vite.svg
│
├── src/
│   ├── components/              # Componentes reutilizables
│   │   ├── Footer.jsx           # Footer de la aplicación
│   │   ├── Navbar.jsx           # 🆕 Navbar con badge de rol
│   │   ├── PrivateRoute.jsx     # HOC para rutas autenticadas
│   │   └── AdminRoute.jsx       # 🆕 HOC para rutas solo admin
│   │
│   ├── context/                 # Context API
│   │   └── AuthContext.jsx      # 🆕 Estado global + isAdmin()
│   │
│   ├── pages/                   # Páginas principales
│   │   ├── Home.jsx             # Página de inicio
│   │   ├── Login.jsx            # Iniciar sesión
│   │   ├── Register.jsx         # Registro de usuario
│   │   ├── Reservas.jsx         # 🆕 CRUD con botones condicionales
│   │   ├── Analytics.jsx        # Estadísticas básicas
│   │   └── PredictiveAnalytics.jsx  # 🆕 ML solo admin
│   │
│   ├── services/                # Servicios de API
│   │   ├── api.js               # 🆕 Axios + interceptores 403/409
│   │   ├── authService.js       # Login y registro
│   │   ├── reservasService.js   # CRUD reservas
│   │   ├── personasService.js   # Gestión personas
│   │   ├── salasService.js      # Gestión salas
│   │   ├── articulosService.js  # Gestión artículos
│   │   ├── analyticsService.js  # Estadísticas básicas
│   │   └── predictionService.js # 🆕 Servicio ML
│   │
│   ├── App.jsx                  # 🆕 Componente raíz + rutas
│   ├── main.jsx                 # Entry point
│   └── index.css                # Estilos globales + Tailwind
│
├── .gitignore
├── eslint.config.js
├── index.html
├── package.json
├── package-lock.json
├── postcss.config.js
├── README.md                    # Este archivo
├── tailwind.config.js
└── vite.config.js

🔑 Componentes Principales
1. AuthContext (Estado Global) 🆕
Maneja autenticación y autorización en toda la aplicación.
Funciones principales:
javascriptconst {
  user,        // Usuario actual {email, role}
  token,       // JWT token
  loading,     // Estado de carga
  login,       // (token, userData) => void
  logout,      // () => void
  isAdmin,     // () => boolean  🆕
  hasRole      // (role) => boolean  🆕
} = useContext(AuthContext);
Ubicación: src/context/AuthContext.jsx
Uso:
jsxfunction MiComponente() {
  const { isAdmin } = useContext(AuthContext);
  
  return (
    <div>
      {isAdmin() && <button>Editar</button>}
    </div>
  );
}

2. Navbar 🆕
Barra de navegación con links condicionales según rol.
Características:

Muestra email del usuario
Badge visual de rol:

🟡 Amarillo "ADMIN" para administradores
🔵 Azul "USER" para usuarios estándar


Link "Análisis Predictivo" solo visible para admin
Botón "Cerrar Sesión"

Ubicación: src/components/Navbar.jsx

3. PrivateRoute
Higher-Order Component que protege rutas que requieren autenticación.
Comportamiento:

Si usuario autenticado → Renderiza children
Si NO autenticado → Redirige a /login

Ubicación: src/components/PrivateRoute.jsx
Uso en App.jsx:
jsx<Route 
  path="/reservas" 
  element={<PrivateRoute><Reservas /></PrivateRoute>} 
/>

4. AdminRoute 🆕
HOC que protege rutas exclusivas de administradores.
Comportamiento:

Si usuario es ADMIN → Renderiza children
Si NO es admin → Redirige a /reservas

Ubicación: src/components/AdminRoute.jsx
Uso en App.jsx:
jsx<Route 
  path="/prediccion" 
  element={<AdminRoute><PredictiveAnalytics /></AdminRoute>} 
/>

5. Reservas 🆕
Página principal de gestión de reservas con CRUD completo.
Características:

Tabla responsiva con todas las reservas
Formulario modal para crear/editar
Botones "Editar" y "Eliminar" solo para admin
Validación automática de conflictos
Manejo de errores 409 CONFLICT

Ubicación: src/pages/Reservas.jsx
UI Condicional:
jsx{isAdmin() && (
  <button onClick={() => handleEdit(reserva)}>
    Editar
  </button>
)}

{!isAdmin() && (
  <span className="text-gray-400">Solo lectura</span>
)}

6. PredictiveAnalytics 🆕
Página de análisis predictivo con Machine Learning (solo admin).
Características:

Botón "Sincronizar y Entrenar Modelo"
Gráfico interactivo con Recharts
Línea de predicción de ocupación
Área de intervalo de confianza
Métricas de predicciones generadas

Ubicación: src/pages/PredictiveAnalytics.jsx
Gráfico:
jsx<LineChart data={predictions}>
  <XAxis dataKey="fecha" />
  <YAxis />
  <Tooltip />
  <Line type="monotone" dataKey="prediccion" stroke="#8884d8" />
  <Area dataKey="limiteInferior" fill="#82ca9d" opacity={0.3} />
  <Area dataKey="limiteSuperior" fill="#82ca9d" opacity={0.3} />
</LineChart>

🌐 Rutas de la Aplicación
RutaComponenteTipoRequiere AdminDescripción/HomePública❌Página de inicio/loginLoginPública❌Iniciar sesión/registerRegisterPública❌Registro de usuario/reservasReservasProtegida❌Gestión de reservas/analyticsAnalyticsProtegida❌Estadísticas básicas/prediccion 🆕PredictiveAnalyticsProtegida✅Análisis ML (solo admin)

🔌 Servicios de API
api.js - Cliente Axios Configurado 🆕
Configuración base:
javascriptconst api = axios.create({
  baseURL: 'http://localhost:8080',
  headers: { 'Content-Type': 'application/json' }
});
Interceptor de Request:

Agrega automáticamente JWT en header Authorization: Bearer {token}
Lee token desde localStorage

Interceptor de Response: 🆕

401 Unauthorized: Limpia localStorage y redirige a /login
403 Forbidden: Muestra alert "No tienes permisos"
409 Conflict: Captura y propaga mensaje de conflicto de reservas

Ubicación: src/services/api.js

authService.js
Funciones:

register(email, password) → Registrar nuevo usuario
login(email, password) → Iniciar sesión, retorna {token, email, role}
logout() → Limpiar localStorage

Ubicación: src/services/authService.js

reservasService.js 🆕
Funciones:

getReservas() → Listar todas las reservas
createReserva(data) → Crear nueva reserva (valida conflictos)
updateReserva(id, data) → Actualizar reserva (solo admin)
deleteReserva(id) → Eliminar reserva (solo admin)

Manejo de Conflictos:
javascripttry {
  await createReserva(data);
} catch (error) {
  if (error.status === 409) {
    // Mostrar mensaje específico del backend
    alert(error.message);
  }
}
Ubicación: src/services/reservasService.js

predictionService.js 🆕
Funciones:

syncReservas() → Sincronizar reservas con Python (solo admin)
trainModel() → Entrenar modelo Prophet
getPredictions() → Obtener predicciones guardadas

Ubicación: src/services/predictionService.js

🎨 Estilos con Tailwind CSS
Configuración
Tailwind está configurado para escanear todos los archivos .jsx:
javascript// tailwind.config.js
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {},
  },
  plugins: [],
}
Clases Utility Más Usadas
Layout:

container mx-auto p-8 - Contenedor centrado con padding
flex justify-between items-center - Flexbox con espacio entre elementos
grid grid-cols-3 gap-4 - Grid de 3 columnas

Botones:

bg-blue-500 hover:bg-blue-600 - Botón azul con hover
bg-green-500 hover:bg-green-600 - Botón verde
bg-red-500 hover:bg-red-600 - Botón rojo (eliminar)

Tablas:

min-w-full - Tabla ancho completo
border-t hover:bg-gray-50 - Filas con hover

Badges: 🆕
jsx// Badge ADMIN (amarillo)
<span className="bg-yellow-100 text-yellow-800 px-2 py-1 rounded">
  ADMIN
</span>

// Badge USER (azul)
<span className="bg-blue-100 text-blue-800 px-2 py-1 rounded">
  USER
</span>

🔐 Sistema de Autenticación
Flujo de Login

Usuario ingresa credenciales en /login
Frontend llama POST /api/auth/login
Backend valida y devuelve: {token, email, role}
Frontend guarda en localStorage:

javascript   localStorage.setItem('token', token);
   localStorage.setItem('user', JSON.stringify({email, role}));

AuthContext actualiza estado global
Frontend redirige a /reservas

Persistencia de Sesión
Al cargar la aplicación, AuthContext verifica localStorage:
javascriptuseEffect(() => {
  const storedToken = localStorage.getItem('token');
  const storedUser = localStorage.getItem('user');
  
  if (storedToken && storedUser) {
    setToken(storedToken);
    setUser(JSON.parse(storedUser));
  }
  setLoading(false);
}, []);
Verificación de Rol 🆕
javascript// En AuthContext
const isAdmin = () => {
  return user?.role === 'ROLE_ADMIN';
};

const hasRole = (role) => {
  return user?.role === role;
};
Cierre de Sesión
javascriptconst logout = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('user');
  setToken(null);
  setUser(null);
};

🐛 Troubleshooting
Problema: "Cannot GET /" en navegación
Causa: Vite dev server no maneja correctamente rutas de React Router.
Solución: Siempre navega desde http://localhost:5173/, no recargues la página en rutas anidadas.

Problema: Usuario admin no muestra privilegios 🆕
Síntomas:

Login exitoso con admin@test.com
NO aparece badge "ADMIN"
NO aparece link "Análisis Predictivo"

Causa: Token JWT viejo en localStorage sin rol actualizado.
Solución:
javascript// En DevTools (F12) → Console
localStorage.clear();
location.reload();
// Luego hacer login nuevamente

Problema: Error CORS al llamar backend
Síntomas:
Access to XMLHttpRequest at 'http://localhost:8080' from origin 
'http://localhost:5173' has been blocked by CORS policy
Causa: Backend no tiene CORS configurado o no incluye localhost:5173.
Solución: Verificar que backend Java tenga:
java@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})

Problema: Token expirado (401 Unauthorized)
Síntomas: Redirigido a login después de algunas horas.
Causa: Token JWT expira a las 24 horas.
Solución: Normal, hacer login nuevamente. Para evitarlo en producción, implementar refresh tokens.

Problema: Mensaje "Error al guardar la reserva" genérico
Síntomas: Intento crear reserva con conflicto pero mensaje no específico.
Causa: Interceptor de Axios no captura error 400/409 correctamente.
Solución: Verificar que api.js tenga el interceptor actualizado con manejo de 409.

Problema: Gráficos ML no se muestran 🆕
Causa: Recharts no instalado.
Solución:
bashnpm install recharts
npm run dev

📜 Scripts Disponibles
ComandoDescripciónnpm run devInicia servidor de desarrollo en puerto 5173npm run buildCompila para producción en carpeta dist/npm run previewVista previa del build de producciónnpm run lintEjecuta ESLint para detectar problemas

🔧 Variables de Entorno (Opcional)
Para cambiar la URL del backend sin modificar código:
Crear archivo .env en la raíz:
bashVITE_API_URL=http://localhost:8080
VITE_PYTHON_API_URL=http://localhost:8000
Usar en api.js:
javascriptconst api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080'
});
Nota: Vite solo reconoce variables que empiezan con VITE_.

🚀 Despliegue
Build para Producción
bashnpm run build
Esto genera carpeta dist/ con:

HTML, CSS, JS minificados
Assets optimizados
Sourcemaps para debugging

Deploy en Netlify/Vercel

Subir código a GitHub
Conectar repositorio en Netlify/Vercel
Configurar:

Build command: npm run build
Publish directory: dist
Environment variables:

VITE_API_URL=https://tu-backend-java.com
VITE_PYTHON_API_URL=https://tu-backend-python.com





Configurar Rewrites (SPA)
Crear public/_redirects (Netlify) o vercel.json:
json{
  "rewrites": [
    { "source": "/(.*)", "destination": "/index.html" }
  ]
}
Esto asegura que todas las rutas de React Router funcionen en producción.

🤝 Integración con Backend
URLs de Backend
ServicioURL DesarrolloPropósitoJava APIhttp://localhost:8080Reservas, Auth, CRUDPython APIhttp://localhost:8000Analítica, ML
Endpoints Consumidos
Java (Puerto 8080):

POST /api/auth/register - Registro
POST /api/auth/login - Login
GET /api/reservas - Listar reservas
POST /api/reservas - Crear reserva (valida conflictos)
PUT /api/reservas/:id - Actualizar (solo admin)
DELETE /api/reservas/:id - Eliminar (solo admin)
POST /api/reservas/sync-analytics - Sincronizar ML (solo admin)
GET /api/personas - Listar personas
GET /api/salas - Listar salas
GET /api/articulos - Listar artículos

Python (Puerto 8000):

GET /api/analytics/summary - Estadísticas básicas
POST /api/analytics/sync - Recibir snapshot de reservas
POST /api/analytics/train - Entrenar modelo Prophet
GET /api/analytics/predictions - Obtener predicciones


📚 Recursos de Aprendizaje
Documentación Oficial

React: https://react.dev/
Vite: https://vitejs.dev/
React Router: https://reactrouter.com/
Tailwind CSS: https://tailwindcss.com/
Axios: https://axios-http.com/
Recharts: https://recharts.org/

Tutoriales Recomendados

React Basics: https://react.dev/learn
React Router Tutorial: https://reactrouter.com/en/main/start/tutorial
Tailwind CSS Tutorial: https://tailwindcss.com/docs/utility-first


🎓 Notas para Desarrollo
Hot Module Replacement (HMR)
Vite proporciona HMR instantáneo:

Cambios en componentes → Actualización sin perder estado
Cambios en CSS → Actualización instantánea
Cambios en vite.config.js → Requiere reinicio

Debugging
React DevTools:
bash# Instalar extensión de navegador
# Chrome: https://chrome.google.com/webstore (buscar "React Developer Tools")
Console.log estratégico:
javascript// Ver estado de autenticación
console.log('User:', JSON.parse(localStorage.getItem('user')));
console.log('Token:', localStorage.getItem('token'));

// Verificar rol
const { isAdmin } = useContext(AuthContext);
console.log('Es admin?', isAdmin());

✅ Checklist de Funcionalidades
Autenticación

 Registro de usuarios
 Login con JWT
 Logout
 Persistencia de sesión
 Rutas protegidas
 🆕 Sistema de roles
 🆕 UI condicional por rol

Reservas

 Listar reservas
 Crear reserva
 🆕 Editar reserva (solo admin)
 🆕 Eliminar reserva (solo admin)
 🆕 Validación de conflictos
 🆕 Mensajes de error específicos

Estadísticas

 Dashboard básico
 Total de reservas
 Reservas por sala
 Reservas por artículo

Análisis Predictivo 🆕

 Página exclusiva para admin
 Sincronización de datos
 Entrenamiento de modelo ML
 Gráficos interactivos
 Intervalos de confianza


🏆 Logros del Frontend

✅ SPA Moderna: Navegación fluida sin recargas
✅ Seguridad Multinivel: JWT + Roles + UI Condicional
✅ UX Profesional: Mensajes claros, feedback visual, badges de rol
✅ Responsive: Funciona en desktop, tablet y móvil
✅ Performance: HMR instantáneo, builds optimizados
✅ Integración ML: Gráficos de predicciones con Recharts
✅ Código Limpio: Componentes reutilizables, separación de responsabilidades


📝 Licencia
Este proyecto es un trabajo académico desarrollado con fines educativos.

👥 Autores
Equipo de Desarrollo - Universidad de la Ciudad de Buenos Aires

Versión: 2.1.0
Última Actualización: Octubre de 2025