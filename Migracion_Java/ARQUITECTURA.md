Arquitectura del Sistema de Reservas de Salas y Equipos

Vista Global con Machine Learning y Control de Acceso 🆕
mermaidgraph TB
    subgraph "Cliente - Navegador Web"
        U[Usuario Final]
    end
    
    subgraph "Frontend - Puerto 5173"
        R[React + Vite<br/>SPA con React Router]
        RC[React Components<br/>🆕 UI Condicional por Rol]
        AS[Axios Services<br/>🆕 Interceptor 403/409]
        AC[Auth Context<br/>🆕 isAdmin(), hasRole()]
    end
    
    subgraph "Microservicio de Analítica - Python"
        P1[FastAPI Server<br/>Puerto 8000]
        P2[Analytics Service]
        P3[ML Service<br/>Prophet]
        P4[Sync Service]
    end
    
    subgraph "Microservicio de Reservas - Java"
        subgraph "Security Layer"
            S[Spring Security Filter Chain<br/>JWT Validation + CORS<br/>🆕 Role Extraction]
        end
       
        subgraph "Controller Layer"
            B1[AuthController<br/>@CrossOrigin]
            B2[ReservaController<br/>+ sync-analytics<br/>🆕 @PreAuthorize ADMIN]
            B3[PersonaController]
            B4[SalaController]
            B5[ArticuloController]
        end
       
        subgraph "Service Layer"
            C[Business Logic Services]
            ANSERV[AnalyticsService🆕]
            VALSERV[🆕 Validación Conflictos<br/>detectarSolapamiento]
        end
       
        subgraph "Repository Layer"
            D[JPA Repositories<br/>🆕 + Queries JPQL Conflictos]
        end
    end
    
    subgraph "Data Layer"
        E[(MySQL 8.0<br/>Puerto 3307<br/>Reservas<br/>🆕 + campo role en User)]
        PG[(PostgreSQL 15<br/>Puerto 5432<br/>Analítica ML 🆕)]
    end
   
    U --> R
    R --> RC
    RC --> AS
    AS --> AC
   
    AS -- "GET /api/analytics/summary" --> P1
    AS -- "POST /api/analytics/sync<br/>POST /api/analytics/train<br/>GET /api/analytics/predictions 🆕" --> P1
   
    P1 --> P2
    P1 --> P3
    P1 --> P4
   
    P2 -- "HTTP Request<br/>GET /api/reservas" --> B2
    P4 -- "Guarda snapshots" --> PG
    P3 -- "Lee datos históricos<br/>Entrena Prophet<br/>Guarda predicciones" --> PG
   
    AS -- "HTTP Request<br/>+ JWT con Role 🆕<br/>Header: Authorization: Bearer ..." --> S
    AS -- "POST /api/reservas/sync-analytics 🆕" --> S
   
    S -- "Públicos: /api/auth/**" --> B1
    S -- "Token Válido + Role Check 🆕" --> B2 & B3 & B4 & B5
    S -- "Token Inválido o Sin Permisos 🆕" --> X{401/403}
    
    B1 & B2 & B3 & B4 & B5 --> C
    B2 --> ANSERV
    B2 --> VALSERV
    C --> D
    D --> E
    
    style R fill:#61dafb
    style RC fill:#61dafb
    style AS fill:#61dafb
    style AC fill:#61dafb
    style P1 fill:#c8e6c9
    style P2 fill:#c8e6c9
    style P3 fill:#ff9800
    style P4 fill:#c8e6c9
    style S fill:#ffcdd2
    style ANSERV fill:#fff59d
    style VALSERV fill:#ffab91
    style X fill:#f44336,color:#fff
    style E fill:#bbdefb
    style PG fill:#9575cd,color:#fff

1️⃣ Arquitectura Frontend React (Actualizada)
Estructura de Componentes con Análisis Predictivo y Control de Acceso 🆕
mermaidgraph TB
    subgraph "React Application - Puerto 5173"
        subgraph "Routing"
            RR[React Router<br/>BrowserRouter]
        end
       
        subgraph "Global State"
            CTX[AuthContext<br/>Provider<br/>🆕 + isAdmin<br/>🆕 + hasRole]
        end
       
        subgraph "Components"
            NAV[Navbar<br/>+ Link Análisis Predictivo 🆕<br/>+ Badge ADMIN 🆕]
            PRIV[PrivateRoute]
            ADMIN[🆕 AdminRoute<br/>Solo ROLE_ADMIN]
        end
       
        subgraph "Pages"
            HOME[Home]
            LOGIN[Login]
            REG[Register]
            RES[Reservas<br/>🆕 Botones Condicionales]
            ANA[Analytics<br/>Estadísticas Básicas]
            PRED[PredictiveAnalytics<br/>Gráficos ML 🆕<br/>Solo Admin 🆕]
        end
       
        subgraph "Services Layer"
            API[api.js<br/>Axios instance + interceptors<br/>🆕 Handle 403/409]
            AUTH[authService]
            RSERV[reservasService<br/>🆕 Manejo 409]
            SSERV[salasService]
            ASERV[articulosService]
            PSERV[personasService]
            ANSERV[analyticsService]
            PREDSERV[predictionService 🆕]
        end
       
        subgraph "Storage"
            LS[localStorage<br/>- token<br/>- user JSON<br/>🆕 Decodifica rol]
        end
       
        subgraph "Visualization"
            RECHARTS[Recharts 🆕<br/>Gráficos ML]
        end
    end
   
    subgraph "Backend APIs"
        JAVA[Java API<br/>:8080]
        PY[Python API<br/>:8000]
    end
   
    RR --> NAV
    RR --> HOME & LOGIN & REG
    RR --> PRIV
    RR --> ADMIN
    PRIV --> RES & ANA
    ADMIN --> PRED
   
    CTX --> NAV & PRIV & ADMIN & LOGIN & REG & RES & ANA & PRED
   
    LOGIN & REG --> AUTH
    RES --> RSERV & SSERV & ASERV & PSERV
    ANA --> ANSERV
    PRED --> PREDSERV
    PRED --> RECHARTS
   
    AUTH & RSERV & SSERV & ASERV & PSERV --> API
    ANSERV & PREDSERV --> API
   
    API --> LS
    API -- "HTTP + JWT con Role 🆕" --> JAVA
    ANSERV & PREDSERV -- "HTTP" --> PY
   
    style RR fill:#61dafb
    style CTX fill:#ffd54f
    style API fill:#ff6f00
    style LS fill:#9c27b0,color:#fff
    style JAVA fill:#fff9c4
    style PY fill:#c8e6c9
    style PRED fill:#ff9800
    style PREDSERV fill:#ff9800
    style RECHARTS fill:#ff5722,color:#fff
    style ADMIN fill:#f44336,color:#fff

2️⃣ Arquitectura Microservicio Python (Actualizada con ML)
Estructura Interna del Servicio de Analítica
mermaidgraph TB
    subgraph "FastAPI Application - Puerto 8000"
        MAIN[main.py<br/>Endpoints]
       
        subgraph "Database Layer"
            CONN[connection.py<br/>SQLAlchemy Engine]
            MODELS[models.py<br/>ReservaSnapshot<br/>Prediction]
        end
       
        subgraph "Services Layer"
            SYNC[sync_service.py<br/>SyncService]
            ML[ml_service.py<br/>MLService<br/>Prophet Integration]
        end
       
        subgraph "Utils"
            HELPERS[date_helpers.py]
        end
    end
   
    subgraph "PostgreSQL - Puerto 5432"
        PG[(analytics_db)]
        T1[reservas_snapshot]
        T2[predictions]
    end
   
    subgraph "Java API - Puerto 8080"
        JAVA[ReservasController]
    end
   
    MAIN --> SYNC
    MAIN --> ML
    MAIN --> HELPERS
   
    SYNC --> CONN
    ML --> CONN
    CONN --> MODELS
   
    MODELS --> PG
    PG --> T1
    PG --> T2
   
    SYNC -- "Recibe reservas<br/>de Java" --> T1
    ML -- "Lee snapshots<br/>Entrena Prophet<br/>Guarda predicciones" --> T1
    ML -- "Guarda<br/>predicciones" --> T2
   
    MAIN -- "GET /api/reservas" --> JAVA
   
    style MAIN fill:#c8e6c9
    style SYNC fill:#fff59d
    style ML fill:#ff9800
    style CONN fill:#90caf9
    style MODELS fill:#90caf9
    style PG fill:#9575cd,color:#fff
    style T1 fill:#b39ddb
    style T2 fill:#b39ddb

3️⃣ Flujo de Autenticación con Sistema de Roles 🆕
Secuencia Completa desde Login hasta Acceso con Roles
mermaidsequenceDiagram
    participant U as Usuario
    participant FE as Frontend React
    participant AC as AuthContext
    participant AX as Axios Interceptor
    participant SC as Spring Security
    participant AUTH as AuthController
    participant JWT as JwtService
    participant US as UserService
    participant DB as MySQL

    Note over U,DB: FASE 1: LOGIN Y OBTENCIÓN DE TOKEN CON ROL
    
    U->>FE: Ingresa credenciales<br/>(email + password)
    FE->>AUTH: POST /api/auth/login<br/>{email, password}
    AUTH->>US: authenticate(credentials)
    US->>DB: SELECT * FROM users<br/>WHERE email = ?
    DB-->>US: User encontrado
    US->>US: Validar password (BCrypt)
    
    alt Credenciales válidas
        US->>JWT: generarToken(user)
        JWT->>JWT: Crear claims:<br/>{email, role, exp}
        Note over JWT: 🆕 Incluye ROLE_USER o ROLE_ADMIN
        JWT-->>AUTH: Token JWT firmado
        AUTH-->>FE: 200 OK + {token, user}
        FE->>AC: guardarToken(token, user)
        AC->>AC: Decodificar token<br/>Extraer role 🆕
        AC-->>FE: Usuario autenticado
        FE-->>U: Redirige a /reservas
    else Credenciales inválidas
        US-->>AUTH: Error autenticación
        AUTH-->>FE: 401 Unauthorized
        FE-->>U: "Credenciales incorrectas"
    end

    Note over U,DB: FASE 2: PETICIÓN A ENDPOINT PROTEGIDO

    U->>FE: Click "Crear Reserva"
    FE->>AX: POST /api/reservas + data
    AX->>AX: Agregar header:<br/>Authorization: Bearer {token}
    AX->>SC: HTTP Request con JWT
    
    SC->>SC: Extraer token del header
    SC->>JWT: validarToken(token)
    JWT->>JWT: Verificar firma<br/>Verificar expiración
    
    alt Token válido
        JWT->>JWT: Extraer claims<br/>🆕 Obtener role
        JWT-->>SC: Token válido + role
        SC->>SC: SecurityContextHolder<br/>setAuthentication(user, role)
        SC->>AUTH: Permitir acceso
        Note over AUTH: Endpoint sin restricciones<br/>@PreAuthorize no aplicable
        AUTH-->>FE: 200/201 + Response
        FE-->>U: "Reserva creada"
    else Token inválido o expirado
        JWT-->>SC: Token inválido
        SC-->>AX: 401 Unauthorized
        AX->>AC: Limpiar localStorage
        AX-->>U: Redirige a /login
    end

    Note over U,DB: FASE 3: PETICIÓN A ENDPOINT SOLO ADMIN

    U->>FE: Click "Editar Reserva"
    Note over FE: Solo visible si isAdmin() === true
    FE->>AX: PUT /api/reservas/{id}
    AX->>SC: HTTP Request + JWT
    SC->>JWT: validarToken(token)
    JWT-->>SC: Token válido + role
    
    SC->>SC: Verificar @PreAuthorize<br/>("hasRole('ADMIN')")
    
    alt Usuario tiene ROLE_ADMIN
        SC->>AUTH: Permitir acceso
        AUTH-->>FE: 200 OK + Updated
        FE-->>U: "Reserva actualizada"
    else Usuario NO tiene ROLE_ADMIN
        SC-->>AX: 403 Forbidden
        AX-->>FE: Error 403
        FE-->>U: "⛔ Acceso denegado:<br/>Solo administradores"
    end

4️⃣ Flujo de Validación de Disponibilidad 🆕
Secuencia de Detección de Conflictos de Reservas
mermaidsequenceDiagram
    participant U as Usuario
    participant FE as Frontend
    participant RC as ReservaController
    participant RS as ReservaService
    participant VS as 🆕 ValidaciónService
    participant RR as ReservaRepository
    participant DB as MySQL

    Note over U,DB: ESCENARIO: Usuario intenta crear reserva

    U->>FE: Completa formulario:<br/>Sala A, 15/11 10:00-12:00
    FE->>RC: POST /api/reservas<br/>{idSala: 1, inicio, fin}
    RC->>RS: crearReserva(dto)
    
    Note over RS,VS: PASO 1: Validación de Conflictos
    
    RS->>VS: validarDisponibilidad(dto)
    VS->>VS: Determinar tipo:<br/>¿Sala o Artículo?
    
    alt Es reserva de SALA
        VS->>RR: findConflictingSalaReservations<br/>(salaId, inicio, fin)
        Note over RR: Query JPQL:<br/>WHERE sala.id = :salaId<br/>AND inicio < :fin<br/>AND fin > :inicio
    else Es reserva de ARTÍCULO
        VS->>RR: findConflictingArticuloReservations<br/>(articuloId, inicio, fin)
        Note over RR: Query JPQL similar<br/>para artículos
    end
    
    RR->>DB: SELECT con índices<br/>en fechas + recurso
    DB-->>RR: Resultado query
    
    alt Hay conflictos (COUNT > 0)
        RR-->>VS: List<Reserva> conflictivas
        VS->>VS: Construir mensaje detallado
        VS-->>RS: ResponseStatusException<br/>HTTP 409 CONFLICT<br/>+ mensaje descriptivo
        RS-->>RC: Exception propagada
        RC-->>FE: 409 Conflict<br/>{message: "Sala A ya reservada<br/>entre 09:00-11:00"}
        FE->>FE: Interceptor Axios<br/>captura 409
        FE-->>U: Modal de error:<br/>"❌ Conflicto de Reserva<br/>Sala A ocupada 09:00-11:00<br/>Tu solicitud: 10:00-12:00"
        U->>FE: Ajusta horario a 14:00-16:00
        FE->>RC: POST /api/reservas (retry)
    else No hay conflictos
        RR-->>VS: Lista vacía
        VS-->>RS: Validación OK
        
        Note over RS,DB: PASO 2: Guardar Reserva
        
        RS->>DB: INSERT INTO reservas<br/>VALUES (...)
        DB-->>RS: Reserva guardada
        RS-->>RC: Reserva creada
        RC-->>FE: 201 Created + Reserva
        FE-->>U: "✅ Reserva creada<br/>exitosamente"
    end

    Note over U,DB: ESCENARIO: Admin edita reserva existente

    U->>FE: Click "Editar" (solo admin)
    FE->>RC: PUT /api/reservas/{id}<br/>{nuevo horario}
    RC->>RS: actualizarReserva(id, dto)
    RS->>VS: validarDisponibilidad(dto, id)
    
    Note over VS: IMPORTANTE: Excluir<br/>la propia reserva<br/>en la validación
    
    VS->>RR: findConflictingSalaReservations<br/>(salaId, inicio, fin, excludeId)
    Note over RR: Query JPQL:<br/>WHERE sala.id = :salaId<br/>AND inicio < :fin<br/>AND fin > :inicio<br/>AND id != :excludeId 🆕
    
    RR->>DB: SELECT (excluyendo id actual)
    DB-->>RR: Reservas conflictivas<br/>(sin incluir la propia)
    
    alt No hay conflictos con otras reservas
        RR-->>VS: Lista vacía
        VS-->>RS: OK
        RS->>DB: UPDATE reservas<br/>SET ... WHERE id = ?
        DB-->>RS: Updated
        RS-->>RC: Reserva actualizada
        RC-->>FE: 200 OK
        FE-->>U: "✅ Reserva actualizada"
    else Hay conflictos
        RR-->>VS: Reservas conflictivas
        VS-->>RS: 409 CONFLICT
        RS-->>RC: Exception
        RC-->>FE: 409 + mensaje
        FE-->>U: "❌ Conflicto detectado"
    end

5️⃣ Modelo de Datos Completo (Con Roles y Auditoría)
Esquema de Bases de Datos Actualizado
mermaiderDiagram
    %% MySQL - Microservicio de Reservas
    PERSONA ||--o{ RESERVA : "realiza"
    SALA ||--o{ RESERVA : "es reservada en"
    ARTICULO ||--o{ RESERVA : "es reservado en"
    USER }|--|| PERSONA : "puede ser"

    PERSONA {
        bigint id PK
        string nombre
        string email UK
        string telefono
    }

    USER {
        bigint id PK
        string email UK
        string password "Hashed BCrypt"
        string role "🆕 ROLE_USER/ADMIN"
    }

    SALA {
        bigint id PK
        string nombre
        int capacidad
        boolean disponible
        string ubicacion
    }

    ARTICULO {
        bigint id PK
        string nombre
        string categoria
        string descripcion
        boolean disponible
    }

    RESERVA {
        bigint id PK
        bigint id_persona FK
        bigint id_sala FK
        bigint id_articulo FK
        datetime fecha_hora_inicio
        datetime fecha_hora_fin
    }

    %% PostgreSQL - Microservicio de Analítica
    RESERVA_SNAPSHOT {
        bigint id PK
        bigint reserva_id
        datetime fecha_hora_inicio
        datetime fecha_hora_fin
        bigint sala_id
        string sala_nombre
        bigint articulo_id
        string articulo_nombre
        bigint persona_id
        datetime synced_at
    }

    PREDICTION {
        bigint id PK
        datetime fecha
        float prediccion
        float limite_inferior
        float limite_superior
        string modelo_version
        datetime created_at
    }

    RESERVA ||..o{ RESERVA_SNAPSHOT : "sincroniza a"
    RESERVA_SNAPSHOT ||--o{ PREDICTION : "genera"
Detalles del Modelo USER con Roles 🆕
sql-- Tabla: users (MySQL)
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,  -- BCrypt hash
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER',  -- 🆕 Campo nuevo
    persona_id BIGINT,
    CONSTRAINT fk_user_persona FOREIGN KEY (persona_id) 
        REFERENCES personas(id) ON DELETE SET NULL,
    CONSTRAINT chk_role CHECK (role IN ('ROLE_USER', 'ROLE_ADMIN'))
);

-- Índices para performance
CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_user_role ON users(role);  -- 🆕 Para queries por rol
Queries JPQL para Validación de Conflictos 🆕
java// ReservaRepository.java

// Query para detectar conflictos en SALAS
@Query("SELECT COUNT(r) FROM Reserva r " +
       "WHERE r.sala.id = :salaId " +
       "AND r.fechaHoraInicio < :fin " +
       "AND r.fechaHoraFin > :inicio")
Long countConflictingSalaReservations(
    @Param("salaId") Long salaId,
    @Param("inicio") LocalDateTime inicio,
    @Param("fin") LocalDateTime fin
);

// Query para detectar conflictos en ARTÍCULOS
@Query("SELECT COUNT(r) FROM Reserva r " +
       "WHERE r.articulo.id = :articuloId " +
       "AND r.fechaHoraInicio < :fin " +
       "AND r.fechaHoraFin > :inicio")
Long countConflictingArticuloReservations(
    @Param("articuloId") Long articuloId,
    @Param("inicio") LocalDateTime inicio,
    @Param("fin") LocalDateTime fin
);

// Query para UPDATE (excluir la propia reserva)
@Query("SELECT COUNT(r) FROM Reserva r " +
       "WHERE r.sala.id = :salaId " +
       "AND r.id != :excludeId " +
       "AND r.fechaHoraInicio < :fin " +
       "AND r.fechaHoraFin > :inicio")
Long countConflictingSalaReservationsExcluding(
    @Param("salaId") Long salaId,
    @Param("excludeId") Long excludeId,
    @Param("inicio") LocalDateTime inicio,
    @Param("fin") LocalDateTime fin
);
Lógica de Solapamiento:
Dos reservas se solapan si:
(Inicio1 < Fin2) AND (Fin1 > Inicio2)

Ejemplo visual:
Reserva Existente: [====09:00--------11:00====]
Intento 1:              [====10:00--------12:00====]  ❌ CONFLICTO
Intento 2:                                  [====14:00--16:00====]  ✅ OK

6️⃣ Despliegue Docker Compose Completo
Arquitectura de Contenedores Actualizada
mermaidgraph TB
    subgraph "Desarrollo Local"
        subgraph "Node.js + Vite Dev Server"
            FE[React Application<br/>Puerto: 5173<br/>HMR Habilitado<br/>🆕 UI Condicional]
        end
    end

    subgraph "Docker Compose Orchestration"
       
        subgraph "Container: reservas-mysql"
            DB[(MySQL 8.0<br/>Puerto: 3306<br/>Expuesto: 3307<br/>🆕 + campo role)]
        end
       
        subgraph "Container: postgres-analytics 🆕"
            PG[(PostgreSQL 15<br/>Puerto: 5432)]
        end
       
        subgraph "Container: reservas-app"
            J1[Spring Boot App<br/>Puerto: 8080<br/>🆕 + Roles<br/>🆕 + Validación]
            J2[Tomcat Server]
            J3[entrypoint.sh<br/>Wait for MySQL]
        end
       
        subgraph "Container: analitica-app"
            P1[FastAPI App<br/>Puerto: 8000<br/>+ Prophet ML 🆕]
            P2[Uvicorn Server]
        end
       
    end
   
    subgraph "Volumes"
        V1[mysql-data<br/>Persistencia MySQL]
        V2[postgres-data<br/>Persistencia PostgreSQL 🆕]
    end
   
    subgraph "Networks"
        N1[migracion_java_default<br/>Red interna Docker]
        N2[Host Network<br/>Comunicación con host]
    end
   
    FE -.->|http://localhost:8080| J2
    FE -.->|http://localhost:8000| P2
   
    J3 -->|Health Check| DB
    J1 --> J2
    J2 -->|JDBC Connection| DB
    J2 -->|RestTemplate| P2
    P1 --> P2
    P2 -.->|HTTP Request<br/>http://reservas-app:8080| J2
    P2 -->|JDBC Connection| PG
   
    DB -.-> V1
    PG -.-> V2
    J2 & P2 & DB & PG -.-> N1
    FE -.-> N2
    J2 & P2 -.-> N2
   
    style FE fill:#61dafb
    style DB fill:#bbdefb
    style PG fill:#9575cd,color:#fff
    style J1 fill:#fff9c4
    style J2 fill:#fff9c4
    style P1 fill:#c8e6c9
    style P2 fill:#ff9800
    style V1 fill:#f8bbd0
    style V2 fill:#ce93d8
    style N1 fill:#e1bee7
    style N2 fill:#e1bee7

7️⃣ Flujo de Comunicación Completo con ML y Roles
mermaidflowchart LR
    A[Usuario en<br/>Navegador]
   
    subgraph "Puerto 5173"
        B[React + Vite<br/>Axios + Auth<br/>🆕 Context con Roles]
    end
   
    subgraph "Puerto 8000"
        C[Python FastAPI<br/>+ Prophet ML]
    end
   
    subgraph "Puerto 8080"
        D[Java Spring Boot<br/>CORS habilitado<br/>🆕 Roles + Validación]
        E[Spring Security<br/>JWT Validation<br/>🆕 Role Verification]
    end
   
    subgraph "Puerto 3307 → 3306"
        F[(MySQL<br/>Reservas<br/>🆕 + role)]
    end
   
    subgraph "Puerto 5432"
        G[(PostgreSQL<br/>Analítica)]
    end
   
    A -->|Interacción UI| B
   
    B -->|1. POST /api/auth/login<br/>Credenciales| E
    E -->|2. Si OK, genera JWT<br/>🆕 + claim role| D
    D -->|3. Devuelve token| B
    B -->|4. Guarda en localStorage<br/>🆕 Decodifica role| B
   
    B -->|5. GET /api/reservas<br/>Header: Bearer token| E
    E -->|6. Valida JWT<br/>🆕 Extrae role| D
    D -->|7. Query DB| F
    F -->|8. Datos| D
    D -->|9. JSON Response| B
   
    B -->|10. GET /api/analytics/summary| C
    C -->|11. GET /api/reservas| E
    E -->|12. Acceso público| D
    D -->|13. Query DB| F
    F -->|14. Datos| D
    D -->|15. JSON| C
    C -->|16. Análisis procesado| B
   
    B -->|17. POST /api/reservas/sync-analytics 🆕<br/>JWT con ROLE_ADMIN| E
    E -->|18. Valida JWT<br/>🆕 Verifica @PreAuthorize| D
    
    D -->|19. Query MySQL| F
    F -->|20. Reservas| D
    D -->|21. POST /api/analytics/sync| C
    C -->|22. Guarda snapshots| G
   
    B -->|23. POST /api/analytics/train 🆕| C
    C -->|24. Lee snapshots| G
    G -->|25. Datos históricos| C
    C -->|26. Entrena Prophet<br/>Genera predicciones| C
    C -->|27. Guarda predicciones| G
    G -->|28. OK| C
    C -->|29. JSON con predicciones| B
   
    B -->|30. Renderiza gráfico ML| A

    B -->|31. POST /api/reservas<br/>🆕 Con validación| E
    E -->|32. Valida JWT| D
    D -->|33. Validar conflictos 🆕| F
    
    F -->|34a. Si conflicto| D
    D -->|35a. 409 CONFLICT| B
    B -->|36a. Mensaje error| A
    
    F -->|34b. Si OK| D
    D -->|35b. INSERT reserva| F
    F -->|36b. Success| D
    D -->|37b. 201 Created| B
    B -->|38b. Confirmación| A
   
    style A fill:#fff
    style B fill:#61dafb
    style C fill:#ff9800
    style D fill:#fff9c4
    style E fill:#ffcdd2
    style F fill:#bbdefb
    style G fill:#9575cd,color:#fff

8️⃣ Endpoints del Sistema Actualizados con Seguridad
Frontend React (Vite Dev Server) - Puerto 5173
Rutas de la Aplicación:
RutaTipoRequiere AuthRequiere AdminDescripción/Pública❌ No❌ NoPágina de inicio/loginPública❌ No❌ NoIniciar sesión/registerPública❌ No❌ NoRegistro de usuario/reservasProtegida✅ Sí❌ NoGestión de reservas/analyticsProtegida✅ Sí❌ NoEstadísticas básicas/prediccion 🆕Protegida✅ Sí✅ SíAnálisis Predictivo ML
Microservicio Java (Spring Boot) - Puerto 8080
Autenticación (Públicos - Sin JWT):
EndpointMétodoDescripciónRetorno/api/auth/registerPOSTRegistrar nuevo usuarioJWT + User (role: USER)/api/auth/loginPOSTIniciar sesiónJWT + User (con role) 🆕
Ejemplo Response Login: 🆕
json{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "admin@test.com",
  "role": "ROLE_ADMIN"
}
Gestión de Recursos (Protegidos - Requieren JWT):
Accesibles para TODOS los usuarios autenticados (USER + ADMIN):
EndpointMétodoAnotaciónDescripción/api/reservasGET@PreAuthorize("hasAnyRole('USER','ADMIN')")Listar todas las reservas/api/reservasPOST@PreAuthorize("hasAnyRole('USER','ADMIN')")Crear nueva reserva 🆕 + validación/api/reservas/{id}GET@PreAuthorize("hasAnyRole('USER','ADMIN')")Obtener reserva específica/api/personasGET@PreAuthorize("hasAnyRole('USER','ADMIN')")Listar personas/api/salasGET@PreAuthorize("hasAnyRole('USER','ADMIN')")Listar salas/api/articulosGET@PreAuthorize("hasAnyRole('USER','ADMIN')")Listar artículos
Solo para ADMINISTRADORES: 🆕
EndpointMétodoAnotaciónDescripciónError si no admin/api/reservas/{id}PUT@PreAuthorize("hasRole('ADMIN')")Actualizar reserva 🆕 + validaciónHTTP 403/api/reservas/{id}DELETE@PreAuthorize("hasRole('ADMIN')")Eliminar reservaHTTP 403/api/reservas/sync-analyticsPOST@PreAuthorize("hasRole('ADMIN')")Sincronizar con PythonHTTP 403
Ejemplo de Implementación: 🆕
java@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    // Accesible para USER y ADMIN
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<ReservaDTO>> listarReservas() {
        return ResponseEntity.ok(reservaService.listarTodas());
    }

    // Accesible para USER y ADMIN (con validación de conflictos)
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> crearReserva(@RequestBody ReservaDTO dto) {
        try {
            // 🆕 Validación automática de conflictos
            ReservaDTO created = reservaService.crearReserva(dto);
            return ResponseEntity.status(201).body(created);
        } catch (ResponseStatusException e) {
            // 🆕 Manejo de HTTP 409 CONFLICT
            return ResponseEntity.status(e.getStatusCode())
                .body(Map.of("error", e.getReason()));
        }
    }

    // 🆕 SOLO ADMINISTRADORES
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> actualizarReserva(
            @PathVariable Long id, 
            @RequestBody ReservaDTO dto) {
        try {
            ReservaDTO updated = reservaService.actualizarReserva(id, dto);
            return ResponseEntity.ok(updated);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                .body(Map.of("error", e.getReason()));
        }
    }

    // 🆕 SOLO ADMINISTRADORES
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> eliminarReserva(@PathVariable Long id) {
        reservaService.eliminarReserva(id);
        return ResponseEntity.noContent().build();
    }

    // 🆕 SOLO ADMINISTRADORES - Sincronización con ML
    @PostMapping("/sync-analytics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> sincronizarAnalytics() {
        try {
            Map<String, Object> result = reservaService.sincronizarConPython();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Error en sincronización"));
        }
    }
}
Microservicio Python (FastAPI) - Puerto 8000
Analítica (Público - Sin autenticación directa):
EndpointMétodoDescripciónAcceso/api/analytics/summaryGETResumen estadístico de reservasPúblico/api/analytics/sync 🆕POSTRecibir snapshot de reservas desde JavaInvocado por Java/api/analytics/train 🆕POSTEntrenar modelo Prophet y generar prediccionesFrontend (vía Java)/api/analytics/predictions 🆕GETObtener predicciones ML guardadasFrontend
Nota de Seguridad:

Aunque los endpoints Python son técnicamente públicos, el control de acceso principal está en los endpoints Java que los invocan. Para producción, se recomienda implementar autenticación entre microservicios (API Keys, OAuth2, mTLS).


9️⃣ Capas de Seguridad: Defensa en Profundidad
Arquitectura de Seguridad Multinivel 🆕
mermaidgraph TB
    subgraph "Capa 1: Frontend (Primera Línea)"
        UI[UI Condicional]
        UI1[Ocultar botones según rol]
        UI2[AdminRoute component]
        UI3[AuthContext.isAdmin]
        
        UI --> UI1
        UI --> UI2
        UI --> UI3
    end
    
    subgraph "Capa 2: Interceptor HTTP"
        AX[Axios Interceptor]
        AX1[Agregar JWT automático]
        AX2[Capturar 401/403]
        AX3[Capturar 409 conflictos]
        
        AX --> AX1
        AX --> AX2
        AX --> AX3
    end
    
    subgraph "Capa 3: Spring Security Filter"
        SF[Security Filter Chain]
        SF1[Validar firma JWT]
        SF2[Verificar expiración]
        SF3[Extraer claims + role]
        SF4[Poblar SecurityContext]
        
        SF --> SF1
        SF --> SF2
        SF --> SF3
        SF --> SF4
    end
    
    subgraph "Capa 4: Method Security"
        MS[@PreAuthorize]
        MS1[hasRole ADMIN]
        MS2[hasAnyRole USER,ADMIN]
        MS3[Evaluación en runtime]
        
        MS --> MS1
        MS --> MS2
        MS --> MS3
    end
    
    subgraph "Capa 5: Service Layer"
        SL[Business Logic]
        SL1[Validación de conflictos]
        SL2[Reglas de negocio]
        SL3[ResponseStatusException]
        
        SL --> SL1
        SL --> SL2
        SL --> SL3
    end
    
    subgraph "Capa 6: Database Constraints"
        DC[DB Constraints]
        DC1[Foreign Keys]
        DC2[CHECK constraints]
        DC3[Índices únicos]
        
        DC --> DC1
        DC --> DC2
        DC --> DC3
    end
    
    UI1 -.->|Bypass intento| AX
    AX -.->|Petición maliciosa| SF
    SF -.->|Sin JWT válido| MS
    MS -.->|Sin rol correcto| SL
    SL -.->|Datos inválidos| DC
    
    style UI fill:#61dafb
    style AX fill:#ff6f00
    style SF fill:#ffcdd2
    style MS fill:#f44336,color:#fff
    style SL fill:#fff59d
    style DC fill:#bbdefb
Matriz de Control de Acceso 🆕
AcciónUSERADMINValidación AdicionalVer reservas✅✅-Crear reserva✅✅🔍 Validación conflictosEditar reserva❌✅🔍 Validación conflictos + permisosEliminar reserva❌✅-Ver estadísticas básicas✅✅-Análisis Predictivo ML❌✅-Sincronizar datos ML❌✅-Entrenar modelo Prophet❌✅-

🔟 Tecnologías Utilizadas por Capa (Actualizada)
CapaTecnologíaPuertoFunciónNuevas Capacidades 🆕FrontendReact 18 + Vite 55173Interfaz de usuario SPA+ UI condicional por rolRoutingReact Router DOM 6-Navegación del lado del cliente+ AdminRoute componentHTTP ClientAxios-Peticiones HTTP con interceptores+ Manejo 403/409State ManagementReact Context API-Estado global de autenticación+ isAdmin(), hasRole()StylesTailwind CSS 3-Framework de estilos utility-first-Visualización ML 🆕Recharts-Gráficos de predicciones-AnalíticaPython 3.9+ + FastAPI8000Análisis de datos de reservas-Machine Learning 🆕Prophet (Meta)-Predicciones de series temporales-Base Analítica 🆕PostgreSQL 155432Almacenamiento de ML-ORM Python 🆕SQLAlchemy-Persistencia analítica-API RESTJava 17 + Spring Boot 3.2.08080Gestión de reservas y recursos+ Sistema de rolesSeguridadSpring Security 6 + JWT-Autenticación y autorización+ @EnableMethodSecurity<br/>+ @PreAuthorize<br/>+ Role claimsValidaciónSpring Validation-Validación de datos+ Validación conflictos<br/>+ Queries JPQLCORS@CrossOrigin + CorsFilter-Permitir peticiones desde React-PersistenciaSpring Data JPA + Hibernate-ORM y acceso a datos+ Queries de conflictosBase de DatosMySQL 8.0.343307→3306Almacenamiento persistente+ Campo role en UserOrquestaciónDocker Compose-Gestión de contenedores backend-Dev ServerVite + Node.js-Hot Module Replacement (HMR)-

1️⃣1️⃣ Ventajas de la Arquitectura con ML y Seguridad Robusta
Frontend Moderno

✅ SPA con React: Experiencia de usuario fluida sin recargas
✅ Vite Dev Server: Desarrollo rápido con HMR instantáneo
✅ Componentización: Código reutilizable y mantenible
✅ Recharts: Visualización de datos ML profesional 🆕
✅ UI condicional por rol: Seguridad desde la interfaz 🆕

Backend Robusto con Microservicios

✅ Separación de bases de datos: MySQL para transacciones, PostgreSQL para analítica
✅ Escalabilidad independiente: Python puede escalar según demanda de análisis
✅ Tecnologías específicas: Java para lógica crítica, Python para ML
✅ Arquitectura de microservicios real: Comunicación REST entre servicios

Seguridad Multinivel 🆕

✅ JWT con claims de rol: Autenticación + Autorización en un token
✅ Spring Security @PreAuthorize: Control de acceso declarativo
✅ Validación en múltiples capas: Frontend, Interceptor, Spring Security, Service
✅ Enum Role tipado: Previene errores de typo en roles
✅ Separación de privilegios: Principio de mínimo privilegio aplicado

Validación de Lógica de Negocio 🆕

✅ Prevención de conflictos: Evita reservas duplicadas automáticamente
✅ Queries optimizadas: Índices en fechas para detección rápida
✅ Mensajes descriptivos: Usuario entiende exactamente el problema
✅ HTTP status codes apropiados: 409 CONFLICT para colisiones
✅ Separación crear/actualizar: Lógica diferente según contexto

Machine Learning Integrado 🆕

✅ Prophet (Meta): Framework especializado en series temporales
✅ Predicciones reales: Análisis de ocupación futura basado en histórico
✅ Intervalos de confianza: Límites superior e inferior de predicciones
✅ Reentrenamiento simple: Modelo se actualiza con nuevos datos
✅ Acceso controlado: Solo administradores pueden usar ML

Integración

✅ API REST: Comunicación estándar entre todos los servicios
✅ Interceptores Axios: JWT agregado automáticamente
✅ Manejo de errores: 401 Unauthorized redirige al login
✅ Manejo 403 Forbidden: Usuario informado de falta de permisos 🆕
✅ Manejo 409 Conflict: Usuario informado de colisión específica 🆕
✅ Sincronización automática: Datos fluyen de MySQL a PostgreSQL


1️⃣2️⃣ Flujo de Seguridad Completo (Actualizado)
Proceso de Autenticación y Autorización Paso a Paso
1. Registro con Rol por Defecto 🆕
Usuario completa formulario 
→ React envía POST /api/auth/register
→ Java hashea password con BCrypt 
→ Java asigna ROLE_USER por defecto 🆕
→ Java guarda en MySQL (email, password_hash, role)
→ Java genera JWT con claim role 🆕
→ React guarda token + decodifica role 🆕
2. Login con Extracción de Rol 🆕
Usuario ingresa credenciales 
→ React envía POST /api/auth/login
→ Java valida con AuthenticationManager 
→ Java extrae role del User 🆕
→ JwtService genera JWT con claims: {email, role, exp} 🆕
→ React guarda en localStorage 
→ AuthContext decodifica token y extrae role 🆕
→ React muestra UI según rol (badge admin, botones) 🆕
3. Petición a Endpoint Protegido (Usuario Estándar)
Usuario USER hace click "Ver Reservas"
→ React hace GET /api/reservas
→ Axios Interceptor agrega: Authorization: Bearer {token}
→ Spring Security Filter valida JWT 
→ JwtService extrae role del token: ROLE_USER 🆕
→ SecurityContext poblado con authorities 🆕
→ @PreAuthorize("hasAnyRole('USER','ADMIN')") evalúa: TRUE ✅
→ Endpoint permite acceso
→ Respuesta 200 OK con datos
4. Petición a Endpoint Solo Admin (Usuario Estándar) 🆕
Usuario USER hace click "Editar" (botón oculto, pero puede llamar API)
→ React hace PUT /api/reservas/{id}
→ Axios Interceptor agrega JWT
→ Spring Security Filter valida JWT
→ JwtService extrae role: ROLE_USER
→ @PreAuthorize("hasRole('ADMIN')") evalúa: FALSE ❌
→ Spring Security lanza AccessDeniedException
→ Respuesta 403 FORBIDDEN
→ Axios Interceptor captura 403
→ Frontend muestra: "⛔ Acceso denegado: Solo administradores"
5. Petición a Endpoint Solo Admin (Administrador) 🆕
Usuario ADMIN hace click "Eliminar"
→ React hace DELETE /api/reservas/{id}
→ Axios Interceptor agrega JWT con role ADMIN
→ Spring Security Filter valida JWT
→ JwtService extrae role: ROLE_ADMIN
→ @PreAuthorize("hasRole('ADMIN')") evalúa: TRUE ✅
→ Endpoint permite acceso
→ Service elimina reserva
→ Respuesta 204 NO CONTENT
6. Token Expirado
Usuario intenta acción después de 24h
→ SecurityFilter detecta expiración 
→ Devuelve 401 Unauthorized 
→ Interceptor de Response detecta 401 
→ Limpia localStorage 
→ Redirige a login
→ Muestra: "Sesión expirada. Por favor inicie sesión nuevamente."
7. Logout
Usuario click en logout 
→ AuthContext llama authService.logout() 
→ Elimina token y user de localStorage 
→ Redirige a login
8. Creación de Reserva con Validación de Conflictos 🆕
Usuario completa formulario de reserva
→ React hace POST /api/reservas {sala, inicio, fin}
→ Axios agrega JWT
→ Spring Security valida + autoriza (USER o ADMIN pueden crear)
→ ReservaService llama validarDisponibilidad() 🆕
→ ReservaRepository ejecuta query JPQL de conflictos 🆕
  ├─ Si COUNT > 0: 
  │   → Lanza ResponseStatusException(409, "Sala X reservada 09:00-11:00")
  │   → Controller retorna 409 CONFLICT
  │   → Axios Interceptor captura 409 🆕
  │   → Frontend muestra modal con detalles del conflicto
  └─ Si COUNT = 0:
      → Service inserta reserva en BD
      → Respuesta 201 CREATED
      → Frontend muestra confirmación

1️⃣3️⃣ Diagramas de Secuencia Detallados
Diagrama 1: Flujo Completo de Creación de Reserva con Validación 🆕
mermaidsequenceDiagram
    actor U as Usuario
    participant FE as Frontend React
    participant AX as Axios
    participant SC as Spring Security
    participant RC as ReservaController
    participant RS as ReservaService
    participant VS as ValidaciónService
    participant RR as ReservaRepository
    participant DB as MySQL

    Note over U,DB: Usuario intenta crear reserva: Sala A, 15/11 10:00-12:00

    U->>FE: Completa formulario + Submit
    FE->>FE: Validación básica UI<br/>(campos requeridos, formato fechas)
    
    alt Validación UI fallida
        FE-->>U: Mensajes de error en formulario
    else Validación UI OK
        FE->>AX: POST /api/reservas<br/>{idPersona, idSala, inicio, fin}
        AX->>AX: Agregar header<br/>Authorization: Bearer {JWT}
        AX->>SC: HTTP Request
        
        SC->>SC: Validar JWT<br/>Extraer role
        
        alt Token inválido/expirado
            SC-->>AX: 401 Unauthorized
            AX->>FE: Interceptor captura
            FE->>FE: Limpiar localStorage
            FE-->>U: Redirige a /login
        else Token válido
            SC->>RC: Request autorizado
            RC->>RS: crearReserva(dto)
            
            Note over RS,VS: VALIDACIÓN DE CONFLICTOS
            
            RS->>VS: validarDisponibilidad(dto)
            VS->>VS: ¿Es Sala o Artículo?
            
            alt Es Sala
                VS->>RR: findConflictingSala<br/>(salaId, inicio, fin)
                RR->>DB: SELECT COUNT(*)<br/>WHERE sala_id = ?<br/>AND inicio < :fin<br/>AND fin > :inicio
            else Es Artículo
                VS->>RR: findConflictingArticulo<br/>(articuloId, inicio, fin)
                RR->>DB: SELECT COUNT(*) similar
            end
            
            DB-->>RR: Resultado
            
            alt Hay conflicto (COUNT > 0)
                RR-->>VS: COUNT = 1 (conflicto detectado)
                VS->>VS: Construir mensaje:<br/>"Sala A ya reservada<br/>entre 09:00-11:00"
                VS-->>RS: throw ResponseStatusException<br/>(409, mensaje)
                RS-->>RC: Exception propagada
                RC->>RC: @ExceptionHandler procesa
                RC-->>AX: 409 CONFLICT<br/>{error: mensaje}
                AX->>AX: Interceptor captura 409
                AX-->>FE: Error 409 + mensaje
                FE->>FE: Mostrar modal de conflicto<br/>con detalles
                FE-->>U: "❌ Conflicto:<br/>Sala A reservada 09:00-11:00<br/>Tu solicitud: 10:00-12:00"
            else No hay conflicto
                RR-->>VS: COUNT = 0
                VS-->>RS: Validación OK
                RS->>DB: INSERT INTO reservas
                DB-->>RS: Reserva creada (ID generado)
                RS-->>RC: ReservaDTO creada
                RC-->>AX: 201 CREATED<br/>{reserva}
                AX-->>FE: Success
                FE->>FE: Actualizar lista de reservas
                FE-->>U: "✅ Reserva creada exitosamente"
            end
        end
    end
Diagrama 2: Flujo de Edición Restringida a Admin 🆕
mermaidsequenceDiagram
    actor U as Usuario
    participant FE as Frontend
    participant AC as AuthContext
    participant AX as Axios
    participant SC as Spring Security
    participant RC as ReservaController
    participant RS as ReservaService
    participant DB as MySQL

    Note over U,DB: Escenario 1: Usuario estándar intenta editar

    U->>FE: Click "Editar" (si visible)
    FE->>AC: isAdmin()
    AC-->>FE: false

    alt Botón NO visible (UI correcta)
        FE-->>U: Botón no renderizado
    else Bypass UI (llamada directa API)
        U->>FE: Llamada API manual/forzada
        FE->>AX: PUT /api/reservas/{id}<br/>{datos actualizados}
        AX->>SC: Request + JWT (ROLE_USER)
        SC->>SC: Validar JWT<br/>Extraer role: USER
        SC->>SC: Evaluar @PreAuthorize<br/>("hasRole('ADMIN')")
        SC->>SC: Resultado: FALSE ❌
        SC-->>AX: 403 FORBIDDEN
        AX->>AX: Interceptor captura 403
        AX-->>FE: Error 403
        FE-->>U: "⛔ Acceso denegado:<br/>Solo administradores pueden<br/>editar reservas"
    end

    Note over U,DB: Escenario 2: Administrador edita reserva

    U->>FE: Login como admin
    FE->>AC: Guardar user con role ADMIN
    U->>FE: Click "Editar" (visible)
    FE->>AC: isAdmin()
    AC-->>FE: true ✅
    FE->>FE: Mostrar formulario edición
    U->>FE: Modifica datos + Submit
    FE->>AX: PUT /api/reservas/{id}
    AX->>SC: Request + JWT (ROLE_ADMIN)
    SC->>SC: Validar JWT<br/>Extraer role: ADMIN
    SC->>SC: Evaluar @PreAuthorize<br/>("hasRole('ADMIN')")
    SC->>SC: Resultado: TRUE ✅
    SC->>RC: Request autorizado
    RC->>RS: actualizarReserva(id, dto)
    
    Note over RS,DB: Validación de conflictos<br/>(excluyendo propia reserva)
    
    RS->>RS: validarDisponibilidad(dto, id)
    RS->>DB: SELECT COUNT(*)<br/>WHERE ... AND id != :id
    
    alt Sin conflictos
        DB-->>RS: COUNT = 0
        RS->>DB: UPDATE reservas<br/>SET ... WHERE id = ?
        DB-->>RS: Updated
        RS-->>RC: ReservaDTO actualizada
        RC-->>AX: 200 OK
        AX-->>FE: Success
        FE-->>U: "✅ Reserva actualizada"
    else Con conflictos
        DB-->>RS: COUNT > 0
        RS-->>RC: 409 CONFLICT
        RC-->>AX: Error 409
        AX-->>FE: Conflicto
        FE-->>U: "❌ Conflicto detectado"
    end

1️⃣4️⃣ Justificaciones Técnicas Actualizadas
1. ¿Por qué separar las bases de datos?

MySQL para Reservas: Optimizado para transacciones ACID, integridad referencial
PostgreSQL para Analítica: Superior para queries analíticos complejos, tipos de datos avanzados
Principio de Microservicios: Cada servicio posee y controla sus propios datos
Escalabilidad: Cada base de datos puede escalar independientemente según su carga

2. ¿Por qué Prophet para ML?

Diseñado específicamente para series temporales por Meta (Facebook)
Robusto con datos faltantes y outliers
Detecta automáticamente estacionalidad (diaria, semanal, anual)
No requiere tuning complejo de hiperparámetros
Proporciona intervalos de confianza automáticamente
Usado en producción por empresas Fortune 500

3. ¿Por qué Sistema de Roles en JWT? 🆕

Sin consultas adicionales: Role viaja en token, sin hits a BD
Stateless: Backend no mantiene sesiones, escala horizontalmente
Verificación rápida: Spring Security evalúa @PreAuthorize en memoria
Seguridad: Firma digital previene modificación del rol
Estándar industria: JWT claims es patrón ampliamente adoptado

4. ¿Por qué @PreAuthorize en lugar de @Secured? 🆕
@PreAuthorize (usado en este proyecto):
java@PreAuthorize("hasRole('ADMIN') and #id == principal.id")

✅ SpEL (Spring Expression Language): Lógica compleja
✅ Acceso a parámetros del método
✅ Evaluación pre-invocación del método
✅ Más flexible y poderoso

@Secured (alternativa más simple):
java@Secured("ROLE_ADMIN")

⚠️ Solo roles simples, sin lógica
⚠️ No accede a parámetros del método
⚠️ Menos expresivo

Decisión: @PreAuthorize para máxima flexibilidad futura
5. ¿Por qué Validación de Conflictos en Backend? 🆕
Alternativa 1: Solo Frontend

❌ Bypasseable con llamadas directas a API
❌ Ventana de race condition (dos usuarios simultáneos)
❌ No es confiable para integridad de datos

Alternativa 2: Solo Base de Datos (constraints)

⚠️ Complejo implementar lógica de solapamiento con CHECK constraints
⚠️ Mensajes de error crípticos para el usuario
⚠️ Difícil personalizar por tipo de recurso

Solución Implementada: Validación en Service Layer ✅

✅ Seguridad: No bypasseable desde frontend
✅ Transaccional: Dentro de transacción de BD
✅ Queries optimizadas: Índices en fechas (O(log n))
✅ Mensajes claros: Control total del mensaje al usuario
✅ Flexible: Lógica diferente para crear vs actualizar
✅ Testeable: Fácil escribir tests unitarios

Algoritmo Utilizado:
java// Detectar solapamiento de intervalos temporales
boolean hayConflicto = (inicio1 < fin2) AND (fin1 > inicio2);

// Ejemplos:
[09:00 -------- 11:00]  Existente
         [10:00 -------- 12:00]  Nuevo → CONFLICTO ✅
                         [14:00 -- 16:00]  Nuevo → OK ❌
6. ¿Por qué Recharts para Visualización? 🆕
Alternativas consideradas:

Chart.js: Más popular, pero basado en Canvas (no React-friendly)
D3.js: Muy potente, pero curva de aprendizaje empinada
Victory: Bueno, pero más pesado

Por qué Recharts:

✅ Componentes nativos de React: <LineChart>, <Area>, etc.
✅ Declarativo: Similar a JSX, fácil de leer
✅ Responsivo: Se adapta automáticamente al tamaño
✅ Tooltips interactivos: Funcionalidad out-of-the-box
✅ Áreas de confianza: Soporta <Area> para intervalos de Prophet
✅ Performance: Renderizado optimizado con React
✅ Documentación: Ejemplos claros y comunidad activa

Ejemplo de uso:
jsx<LineChart data={predictions}>
  <XAxis dataKey="fecha" />
  <YAxis />
  <Tooltip />
  <Line type="monotone" dataKey="prediccion" stroke="#8884d8" />
  <Area dataKey="limiteInferior" stroke="#82ca9d" fill="#82ca9d" />
</LineChart>
7. ¿Por qué AdminRoute Component? 🆕
Problema sin AdminRoute:
jsx// ❌ Lógica duplicada en cada página
function PredictiveAnalytics() {
  const { isAdmin } = useContext(AuthContext);
  if (!isAdmin()) return <Navigate to="/reservas" />;
  // ... resto del componente
}
Solución con AdminRoute:
jsx// ✅ DRY (Don't Repeat Yourself)
<Route 
  path="/prediccion" 
  element={<AdminRoute><PredictiveAnalytics /></AdminRoute>} 
/>
Ventajas:

✅ Reutilizable: Un solo componente para todas las rutas admin
✅ Centralizado: Cambios en lógica de autorización en un lugar
✅ Consistente: Mismo comportamiento en todas las páginas
✅ Testeable: Fácil escribir tests para autorización
✅ Extensible: Fácil agregar más roles (MANAGER, VIEWER, etc.)

8. ¿Por qué Axios Interceptors?
Alternativa 1: Agregar token manualmente en cada petición
javascript// ❌ Repetitivo y propenso a errores
fetch('/api/reservas', {
  headers: {
    'Authorization': `Bearer ${localStorage.getItem('token')}`
  }
});
Solución con Interceptors:
javascript// ✅ Automático en todas las peticiones
api.interceptors.request.use(config => {
  config.headers.Authorization = `Bearer ${localStorage.getItem('token')}`;
  return config;
});
Ventajas:

✅ DRY: Código no repetido
✅ Centralizado: Manejo de errores en un lugar
✅ Automático: Token agregado sin recordarlo
✅ Manejo global de 401/403: Logout automático en token expirado

9. ¿Por qué Docker Compose?
Alternativas:

Manual: Instalar MySQL, PostgreSQL, Java, Python localmente
Kubernetes: Demasiado complejo para desarrollo local

Ventajas de Docker Compose:

✅ Reproducible: Funciona igual en todos los sistemas
✅ Un comando: docker-compose up levanta todo
✅ Aislamiento: No contamina el sistema host
✅ Path to production: Fácil migración a Kubernetes
✅ Networking: Contenedores se comunican automáticamente
✅ Volúmenes: Datos persisten entre reinicios

10. ¿Por qué separar en Microservicios?
Monolito (alternativa):
Java App
├── Lógica de Reservas
├── Lógica de Analítica
└── Machine Learning (¿en Java?)
Microservicios (implementado):
Java Service (Puerto 8080)
└── Lógica de Reservas + Seguridad

Python Service (Puerto 8000)
└── Analítica + ML con Prophet
Ventajas:

✅ Tecnología apropiada: Python para ML, Java para lógica de negocio
✅ Escalabilidad independiente: Python puede escalar sin afectar Java
✅ Despliegue independiente: Cambios en ML no requieren rebuild de Java
✅ Bases de datos especializadas: MySQL transaccional, PostgreSQL analítica
✅ Equipos especializados: Data Scientists en Python, Backend en Java


1️⃣5️⃣ Patrones de Diseño Implementados
1. Repository Pattern (Spring Data JPA)
Problema: Acoplamiento entre lógica de negocio y acceso a datos
Solución:
javapublic interface ReservaRepository extends JpaRepository<Reserva, Long> {
    @Query("SELECT COUNT(r) FROM Reserva r WHERE ...")
    Long countConflictingSalaReservations(...);
}
Ventajas:

Abstracción del acceso a datos
Queries reutilizables
Fácil cambiar implementación (JPA → MyBatis)

2. Service Layer Pattern
Problema: Lógica de negocio mezclada con Controllers
Solución:
java@Service
public class ReservaService {
    public ReservaDTO crearReserva(ReservaDTO dto) {
        validarDisponibilidad(dto);  // Lógica de negocio
        // ...
    }
}
Ventajas:

Separación de responsabilidades
Reutilización de lógica
Transacciones manejadas por @Transactional

3. DTO Pattern (Data Transfer Objects)
Problema: Exponer entidades JPA directamente en API
Solución:
javapublic class ReservaDTO {
    private Long id;
    private PersonaDTO persona;
    private LocalDateTime fechaInicio;
    // ... solo lo necesario
}
Ventajas:

Control de qué datos se exponen
Evita lazy loading exceptions
Desacopla modelo interno de API

4. Interceptor Pattern (Axios)
Problema: Lógica repetitiva en cada petición HTTP
Solución:
javascriptapi.interceptors.request.use(config => {
  // Agregar JWT automáticamente
  config.headers.Authorization = `Bearer ${token}`;
  return config;
});
Ventajas:

Cross-cutting concerns centralizados
Código no repetido

5. Context Pattern (React)
Problema: Prop drilling (pasar props por muchos niveles)
Solución:
jsx<AuthContext.Provider value={{user, login, logout, isAdmin}}>
  <App />
</AuthContext.Provider>
Ventajas:

Estado global accesible
No pasar props innecesariamente

6. Higher-Order Component Pattern (PrivateRoute, AdminRoute)
Problema: Autorización repetida en cada página
Solución:
jsxfunction AdminRoute({ children }) {
  const { isAdmin } = useContext(AuthContext);
  return isAdmin() ? children : <Navigate to="/reservas" />;
}
Ventajas:

Reutilización de lógica de autorización
Composición de componentes


1️⃣6️⃣ Mejores Prácticas Implementadas
Seguridad

✅ Contraseñas hasheadas con BCrypt (nunca texto plano)
✅ JWT con firma HMAC256 (previene modificación)
✅ Expiración de tokens (24 horas)
✅ CORS configurado correctamente
✅ @PreAuthorize en endpoints sensibles
✅ Validación en múltiples capas (UI, API, Service)
✅ SQL Injection prevenido (JPA PreparedStatements)

Código Limpio

✅ Nombres descriptivos (no x, temp, data)
✅ Funciones pequeñas (una responsabilidad)
✅ DRY (Don't Repeat Yourself)
✅ Comentarios en lugares complejos (queries JPQL)
✅ Manejo de excepciones consistente

Performance

✅ Índices en columnas de búsqueda (email, role, fecha_hora_inicio)
✅ Queries optimizadas (COUNT en lugar de SELECT *)
✅ Lazy Loading en JPA (solo carga lo necesario)
✅ Connection pooling (HikariCP por defecto en Spring Boot)
✅ Recharts con renderizado optimizado

Mantenibilidad

✅ Separación de capas (Controller, Service, Repository)
✅ DTOs separados de entidades
✅ Configuración externalizada (application.properties)
✅ Versionado en Git
✅ README.md exhaustivo

Testing (Preparado para)

✅ Servicios con inyección de dependencias (fácil mockear)
✅ Queries JPQL testeables
✅ Lógica de validación aislada
✅ Componentes React sin lógica de negocio


1️⃣7️⃣ Limitaciones y Mejoras Futuras
Limitaciones Actuales
ÁreaLimitaciónImpactoAutenticaciónSin refresh tokensToken expira a las 24h, requiere re-loginMLModelo simpleNo considera factores externos (vacaciones, eventos)Seguridad MicroserviciosPython sin autenticaciónCualquiera puede llamar endpoints Python directamenteValidaciónSolo detecta conflictosNo valida horarios de negocio (8am-6pm)TestingSin tests automatizadosRegresiones no detectadas automáticamenteMonitoreoSin métricasDifícil detectar problemas en producción
Roadmap de Mejoras
Corto Plazo (1-2 meses):

Refresh Tokens

Implementar endpoint /api/auth/refresh
Token de acceso: 15 min, Refresh token: 7 días
Mejorar experiencia de usuario


Tests Automatizados

Tests unitarios con JUnit/Mockito (Java)
Tests de integración con TestContainers
Tests frontend con Vitest
Objetivo: >80% cobertura


Validaciones de Negocio

Horario de atención (8am - 6pm)
Duración mínima/máxima de reservas
Capacidad máxima de sala



Mediano Plazo (3-6 meses):

Autenticación entre Microservicios

API Keys para Python
OAuth2 Client Credentials Flow
mTLS para producción


Modelo ML Mejorado

Incorporar variables externas (clima, eventos)
Múltiples modelos (ARIMA, LSTM)
A/B testing de modelos


Roles Granulares

ROLE_MANAGER: Puede ver reportes pero no editar
ROLE_VIEWER: Solo lectura
Permisos por recurso (sala específica)



Largo Plazo (6-12 meses):

Migración a Kubernetes

Helm charts para despliegue
Auto-scaling según carga
Health checks y liveness probes


Monitoreo y Observabilidad

Prometheus + Grafana para métricas
ELK Stack para logs centralizados
Jaeger para distributed tracing


CI/CD Pipeline

GitHub Actions / GitLab CI
Tests automatizados en cada commit
Despliegue automático a staging/producción




1️⃣8️⃣ Casos de Uso Completos
Caso de Uso 1: Usuario Estándar Crea Reserva con Conflicto
Actores: Usuario (ROLE_USER)
Precondiciones:

Usuario autenticado
Existe reserva: Sala A, 15/11 09:00-11:00

Flujo Principal:

Usuario hace clic en "Nueva Reserva"
Selecciona:

Persona: María González
Sala: Sala A
Fecha: 15/11/2024
Hora inicio: 10:00
Hora fin: 12:00


Usuario hace clic en "Crear"
Sistema valida JWT (válido ✅)
Sistema valida disponibilidad
Sistema detecta conflicto: (09:00 < 12:00) AND (11:00 > 10:00) = TRUE
Sistema responde HTTP 409 CONFLICT
Frontend muestra modal:

   ❌ Conflicto de Reserva
   
   La Sala A ya está reservada:
   - Reserva existente: 15/11 09:00 - 11:00 (Juan Pérez)
   - Tu solicitud: 15/11 10:00 - 12:00
   
   Por favor, selecciona otro horario o recurso.

Usuario ajusta horario: 14:00 - 16:00
Sistema valida nuevamente: sin conflictos
Sistema crea reserva
Frontend muestra: "✅ Reserva creada exitosamente"

Postcondiciones:

Reserva creada en BD
Usuario ve nueva reserva en lista


Caso de Uso 2: Usuario Estándar Intenta Eliminar (Acceso Denegado)
Actores: Usuario (ROLE_USER)
Precondiciones:

Usuario autenticado con ROLE_USER

Flujo Principal:

Usuario navega a lista de reservas
Usuario ve reservas pero NO ve botones "Editar" ni "Eliminar"
Usuario intenta llamar API directamente (burlar UI):

bash   curl -X DELETE http://localhost:8080/api/reservas/1 \
     -H "Authorization: Bearer {token_con_ROLE_USER}"

Spring Security Filter valida JWT (válido ✅)
Spring Security extrae role: ROLE_USER
@PreAuthorize("hasRole('ADMIN')") evalúa: FALSE ❌
Spring Security lanza AccessDeniedException
Sistema responde HTTP 403 FORBIDDEN
Mensaje: "Acceso denegado: Solo administradores pueden eliminar reservas"

Postcondiciones:

Reserva NO eliminada
Usuario informado de falta de permisos


Caso de Uso 3: Administrador Usa Análisis Predictivo
Actores: Administrador (ROLE_ADMIN)
Precondiciones:

Usuario autenticado con ROLE_ADMIN
Existen al menos 10 reservas históricas

Flujo Principal:

Admin hace clic en "Análisis Predictivo" (visible solo para admin)
Sistema verifica role: ROLE_ADMIN ✅
Sistema muestra página de predicciones
Admin hace clic en "🚀 Sincronizar y Entrenar Modelo"
Frontend hace POST /api/reservas/sync-analytics
Java verifica @PreAuthorize("hasRole('ADMIN')") ✅
Java obtiene 20 reservas de MySQL
Java envía POST /api/analytics/sync a Python
Python guarda snapshot en PostgreSQL
Frontend hace POST /api/analytics/train a Python
Python lee datos históricos de PostgreSQL
Python entrena modelo Prophet (15-30 segundos)

Detecta estacionalidad semanal
Identifica tendencias
Genera 30 predicciones
Calcula intervalos de confianza (80%)


Python guarda predicciones en PostgreSQL
Python responde con JSON de predicciones
Frontend renderiza gráfico con Recharts:

Línea azul: Predicción de ocupación
Área celeste: Intervalo de confianza
Tooltips interactivos con fechas y valores


Sistema muestra métricas:

"30 predicciones generadas"
"Ocupación promedio estimada: 2.3 reservas/día"
"Modelo utilizado: Prophet ML"



Postcondiciones:

Admin visualiza ocupación futura
Admin puede tomar decisiones informadas
Datos ML guardados para consulta posterior

Flujo Alternativo 3A: Usuario Estándar Intenta Acceder

En paso 2, sistema detecta ROLE_USER
AdminRoute redirige a /reservas
Usuario ve mensaje: "Acceso denegado: Solo administradores"


1️⃣9️⃣ Glosario Técnico
TérminoDefiniciónJWT (JSON Web Token)Token de autenticación autónomo que contiene claims (email, role, expiración) firmado digitalmenteClaimsInformación contenida en el JWT (subject, role, issued at, expiration)BCryptAlgoritmo de hashing para contraseñas con salt automático y ajuste de complejidadCORS (Cross-Origin Resource Sharing)Mecanismo que permite peticiones HTTP desde un origen diferente al del servidorDTO (Data Transfer Object)Objeto que transporta datos entre capas sin lógica de negocioJPA (Java Persistence API)Estándar de Java para mapeo objeto-relacional (ORM)JPQL (Java Persistence Query Language)Lenguaje de consultas orientado a objetos similar a SQLProphetFramework de ML de Meta para predicción de series temporales con estacionalidadRechartsLibrería de gráficos para React basada en componentes declarativosSPA (Single Page Application)Aplicación web que carga una sola página HTML y actualiza dinámicamenteHMR (Hot Module Replacement)Técnica de Vite que actualiza módulos sin recargar la página completaMicroservicioArquitectura donde la aplicación se divide en servicios independientes y desplegables@PreAuthorizeAnotación de Spring Security para controlar acceso antes de ejecutar métodoSpring Security Filter ChainSerie de filtros que procesan peticiones HTTP (autenticación, autorización, CORS)Axios InterceptorFunción que intercepta peticiones/respuestas HTTP para procesamiento centralizadoContext APISistema de React para compartir estado global sin prop drillingRace ConditionSituación donde dos procesos compiten por el mismo recurso simultáneamenteSolapamiento de IntervalosDos intervalos temporales se superponen cuando (Inicio1 < Fin2) AND (Fin1 > Inicio2)

2️⃣0️⃣ Referencias y Recursos
Documentación Oficial

Spring Boot: https://spring.io/projects/spring-boot
Spring Security: https://spring.io/projects/spring-security

Method Security: https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html


Spring Data JPA: https://spring.io/projects/spring-data-jpa
React: https://react.dev/
React Router: https://reactrouter.com/
Vite: https://vitejs.dev/
Axios: https://axios-http.com/
Recharts: https://recharts.org/
FastAPI: https://fastapi.tiangolo.com/
Prophet: https://facebook.github.io/prophet/

Paper: https://peerj.com/preprints/3190/


Docker: https://docs.docker.com/
Docker Compose: https://docs.docker.com/compose/

Artículos y Tutoriales

JWT Best Practices: https://datatracker.ietf.org/doc/html/rfc8725
Spring Security Architecture: https://spring.io/guides/topicals/spring-security-architecture
React Security: https://cheatsheetseries.owasp.org/cheatsheets/React_Security_Cheat_Sheet.html
Microservices Patterns: https://microservices.io/patterns/

Libros Recomendados

Spring Security in Action - Laurențiu Spilcă
Designing Data-Intensive Applications - Martin Kleppmann
Clean Code - Robert C. Martin
Building Microservices - Sam Newman


Conclusión
Este documento describe una arquitectura completa de sistema de gestión de reservas con las siguientes características destacadas:
Logros Técnicos ✅

Arquitectura de Microservicios Real: No es un monolito dividido, sino servicios verdaderamente independientes
Machine Learning en Producción: Prophet integrado con pipeline completo de datos
Seguridad Robusta Multinivel: JWT + @PreAuthorize + validación en capas
Validación Inteligente de Negocio: Prevención automática de conflictos de reservas
Experiencia de Usuario Profesional: UI condicional, mensajes claros, gráficos interactivos

Innovaciones Implementadas 🆕

Sistema de roles con control de acceso diferenciado
Validación de disponibilidad con queries JPQL optimizadas
Análisis predictivo restringido a administradores
Manejo específico de errores 403 y 409
Componente AdminRoute para rutas protegidas

Preparado para Producción 🚀

Contenerización con Docker Compose
Separación de bases de datos transaccional y analítica
Queries optimizadas con índices
Manejo robusto de errores
Documentación exhaustiva

El sistema está listo para ser presentado, evaluado y desplegado.

Versión del Documento: 2.1.0
Última Actualización: Octubre de 2025
Autores: Equipo de Desarrollo - Universidad de la Ciudad de Buenos Aires