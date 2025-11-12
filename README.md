# Sistema Inteligente de Gestión de Reservas

Sistema web de gestión de reservas con análisis predictivo mediante Machine Learning.

##  Stack Tecnológico

- **Frontend:** React 18 + Vite + Tailwind CSS
- **Backend:** Spring Boot 3 + Spring Security 6 + JWT
- **ML:** Python + FastAPI + Prophet
- **Base de Datos:** MySQL 8 + PostgreSQL 15
- **Infraestructura:** Docker Compose

##  Características Principales

-  Gestión completa de reservas (CRUD)
-  Sistema de roles (USER/ADMIN)
-  Validación automática de conflictos de horarios
-  Análisis predictivo con Machine Learning (Prophet)
-  API REST completamente documentada (Swagger/OpenAPI)
-  Arquitectura de microservicios
-  80%+ cobertura de tests automatizados

##  Módulos del Sistema

### Backend Java (Puerto 8080)
- Autenticación y autorización con JWT
- Gestión de reservas, salas, personas y artículos
- Validación de disponibilidad en tiempo real
- API REST con documentación Swagger

### Backend Python (Puerto 8000)
- Análisis predictivo con Prophet ML
- Predicciones de ocupación a 30 días
- Estadísticas avanzadas
- API FastAPI con documentación automática

### Frontend React (Puerto 5173)
- Interfaz moderna y responsive
- Dashboards diferenciados por rol
- Visualización de predicciones ML con gráficos interactivos
- Experiencia optimizada para móviles

##  Instalación y Ejecución

### Requisitos Previos
- Docker Desktop
- Node.js 18+
- Java 17+
- Maven

### Arranque Rápido
```bash
# 1. Clonar repositorio
git clone https://github.com/jduhalde/Sistema-de-reservas.git
cd Sistema-de-reservas

# 2. Levantar servicios con Docker
cd Migracion_Java
docker-compose up -d

# 3. Esperar 30-40 segundos e iniciar Frontend
cd ../Vistas_React
npm install
npm run dev
```

### Acceso al Sistema

- **Frontend:** http://localhost:5173
- **API Java:** http://localhost:8080
- **API Python:** http://localhost:8000
- **Swagger Docs:** http://localhost:8080/swagger-ui.html
- **FastAPI Docs:** http://localhost:8000/docs

### Usuarios de Prueba

- **Admin:** admin@test.com / admin123
- **Usuario:** prueba@test.com / 123456

##  Documentación

- [Arquitectura del Sistema](Migracion_Java/ARQUITECTURA.md)
- [Documentación API (Swagger)](http://localhost:8080/swagger-ui.html)
- [Documentación ML (FastAPI)](http://localhost:8000/docs)

##  Testing
```bash
# Tests Backend Java
cd Migracion_Java
mvn test

# Cobertura: 80%+
```

##  Arquitectura
```

  React Frontend 
   (Puerto 5173) 
 
 Spring   FastAPI 
  Boot    Python  
  (8080)  (8000)               
 
 MySQL   PostgreSQL 
 (3307)    (5432)   
 
```

##  Seguridad

- Autenticación JWT
- Passwords encriptados con BCrypt
- CORS configurado
- Validación en múltiples capas
- Control de acceso basado en roles

##  Contribuciones

Proyecto académico desarrollado para la materia Programación de Vanguardia.

##  Licencia

Proyecto académico - Uso libre

##  Autores

Katherine Monduela
Julio Duhalde
Silvina Perez Heredia
Gustavo Fernández
Guido Rearte

##  Institución

Universidad de la ciudad de Buenos Aires
Licenciatura en tecnologías digitales
Programación de Vanguardia - 2025
