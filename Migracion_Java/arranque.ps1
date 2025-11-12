# ============================================
# SCRIPT DE ARRANQUE - Sistema de Reservas con ML
# ============================================

Write-Host "=== SISTEMA DE RESERVAS CON ANÁLISIS PREDICTIVO ML ===" -ForegroundColor Cyan
Write-Host ""

# Verificar Docker Desktop
Write-Host "1. Verificando Docker Desktop..." -ForegroundColor Yellow
$dockerRunning = docker info 2>&1 | Select-String "Server Version"
if (-not $dockerRunning) {
    Write-Host "(ERROR) Docker Desktop no está corriendo. Por favor inicia Docker Desktop primero." -ForegroundColor Red
    exit 1
}
Write-Host "(OK) Docker Desktop está corriendo" -ForegroundColor Green
Write-Host ""

# Limpiar contenedores anteriores
Write-Host "2. Limpiando contenedores anteriores..." -ForegroundColor Yellow
docker-compose down -v
Write-Host "(OK) Contenedores anteriores eliminados" -ForegroundColor Green
Write-Host ""

# Construir y levantar servicios
Write-Host "3. Construyendo y levantando servicios..." -ForegroundColor Yellow
Write-Host "    - MySQL (puerto 3307)" -ForegroundColor Cyan
Write-Host "    - PostgreSQL (puerto 5432)" -ForegroundColor Cyan
Write-Host "    - Java Spring Boot (puerto 8080)" -ForegroundColor Cyan
Write-Host "    - Python FastAPI (puerto 8000)" -ForegroundColor Cyan
docker-compose up --build -d
Write-Host "(OK) Servicios levantados" -ForegroundColor Green
Write-Host ""

# Esperar a que MySQL esté healthy
Write-Host "4. Esperando a que MySQL esté listo..." -ForegroundColor Yellow
$maxAttempts = 40
$attempt = 0
do {
    $attempt++
    $health = docker inspect --format='{{.State.Health.Status}}' reservas-mysql 2>$null
    if ($health -eq "healthy") {
        Write-Host "(OK) MySQL reporta estado healthy" -ForegroundColor Green
        break
    }
    Write-Host "    Intento $attempt/$maxAttempts - MySQL: $health" -ForegroundColor Gray
    Start-Sleep -Seconds 2
} while ($attempt -lt $maxAttempts)

if ($attempt -ge $maxAttempts) {
    Write-Host "(ERROR) MySQL no respondió a tiempo" -ForegroundColor Red
    exit 1
}

# Espera adicional para asegurar que MySQL está realmente listo
Write-Host "    Esperando 10 segundos adicionales para asegurar estabilidad..." -ForegroundColor Gray
Start-Sleep -Seconds 10
Write-Host ""

# Cargar datos de prueba
Write-Host "5. Cargando datos de prueba en MySQL..." -ForegroundColor Yellow

$sqlScript = @"
-- Especificar codificacion UTF8 para MySQL
SET NAMES 'utf8';
SET CHARACTER SET 'utf8';

INSERT INTO personas (nombre, email, telefono) VALUES
('Juan Pérez', 'juan.perez@example.com', '555-0001'),
('María García', 'maria.garcia@example.com', '555-0002'),
('Carlos López', 'carlos.lopez@example.com', '555-0003'),
('Ana Martínez', 'ana.martinez@example.com', '555-0004'),
('Pedro Sánchez', 'pedro.sanchez@example.com', '555-0005');

INSERT INTO salas (nombre, capacidad, disponible, ubicacion) VALUES
('Sala A', 20, 1, 'Edificio Principal - Piso 1'),
('Sala B', 15, 1, 'Edificio Principal - Piso 2'),
('Sala C', 30, 1, 'Edificio Anexo - Piso 1'),
('Auditorio', 100, 1, 'Edificio Principal - Planta Baja');

INSERT INTO articulos (nombre, categoria, descripcion, disponible) VALUES
('Proyector HD', 'Electrónica', 'Proyector de alta definición 1080p', 1),
('Laptop Dell', 'Informática', 'Laptop Dell Inspiron 15', 1),
('Micrófono inalámbrico', 'Audio', 'Sistema de micrófono profesional', 1),
('Pizarra digital', 'Material didáctico', 'Pizarra interactiva táctil', 1),
('Cámara web 4K', 'Electrónica', 'Cámara web para videoconferencias', 1);

INSERT INTO reservas (fecha_hora_inicio, fecha_hora_fin, id_persona, id_sala, id_articulo) VALUES
('2024-12-03 09:00:00', '2024-12-03 11:00:00', 1, 1, 1),
('2024-12-03 14:00:00', '2024-12-03 16:00:00', 2, 2, NULL),
('2024-12-08 10:00:00', '2024-12-08 12:00:00', 3, 1, 2),
('2024-12-08 15:00:00', '2024-12-08 17:00:00', 1, 3, 1),
('2024-12-13 09:00:00', '2024-12-13 11:00:00', 4, 2, 3),
('2024-12-13 13:00:00', '2024-12-13 15:00:00', 2, 4, NULL),
('2024-12-18 08:00:00', '2024-12-18 10:00:00', 5, 1, 1),
('2024-12-18 11:00:00', '2024-12-18 13:00:00', 3, 2, 2),
('2024-12-18 14:00:00', '2024-12-18 16:00:00', 1, 3, NULL),
('2024-12-23 09:00:00', '2024-12-23 11:00:00', 2, 1, 1),
('2024-12-23 12:00:00', '2024-12-23 14:00:00', 4, 2, 3),
('2024-12-23 15:00:00', '2024-12-23 17:00:00', 5, 4, NULL),
('2024-12-27 10:00:00', '2024-12-27 12:00:00', 1, 1, 2),
('2024-12-27 14:00:00', '2024-12-27 16:00:00', 3, 3, 1),
('2024-12-28 09:00:00', '2024-12-28 11:00:00', 2, 2, NULL),
('2024-12-28 13:00:00', '2024-12-28 15:00:00', 4, 1, 3),
('2024-12-29 10:00:00', '2024-12-29 12:00:00', 5, 3, 1),
('2024-12-29 14:00:00', '2024-12-29 16:00:00', 1, 4, NULL),
('2025-01-02 09:00:00', '2025-01-02 11:00:00', 3, 1, 2),
('2025-01-03 10:00:00', '2025-01-03 12:00:00', 2, 2, 1);

-- NOTA: Se eliminó la creación automática de admin.
-- Se debe crear manualmente desde la APP (Registro)
-- y luego actualizar el ROL manualmente.
"@

# --- INICIO DE LA LÓGICA (docker cp + Get-Content) ---
$tempSqlFile = "temp_data.sql"

# 1. Guardar el script SQL en un archivo local (host) con codificación UTF-8
try {
    $sqlScript | Out-File -FilePath $tempSqlFile -Encoding utf8 -ErrorAction Stop
    Write-Host "    - Archivo temporal local creado." -ForegroundColor Gray
} catch {
    Write-Host "(ERROR) No se pudo crear el archivo temporal $tempSqlFile." -ForegroundColor Red
    exit 1
}

# 2. Ejecutar el script SQL *dentro* del contenedor (Método Get-Content)
$success = $false
try {
    Write-Host "    - Ejecutando script SQL (método Get-Content)..." -ForegroundColor Gray
    # Este método es el más robusto contra la corrupción de '$'
    Get-Content $tempSqlFile | docker exec -i reservas-mysql mysql -ureservas_user -preservas123 reservas_db
    
    if ($LASTEXITCODE -eq 0) {
        $success = $true
        Write-Host "(OK) Datos de prueba cargados exitosamente" -ForegroundColor Green
        Write-Host "    - 5 personas"
        Write-Host "    - 4 salas"
        Write-Host "    - 5 artículos"
        Write-Host "    - 20 reservas"
    } else {
        Write-Host "(ERROR) Falló la ejecución del script SQL dentro del contenedor." -ForegroundColor Red
    }
} catch {
    Write-Host "(ERROR) Falló el comando 'docker exec'." -ForegroundColor Red
}

# 3. Limpiar el archivo temporal
Write-Host "    - Limpiando archivo temporal..." -ForegroundColor Gray
if (Test-Path $tempSqlFile) {
    Remove-Item $tempSqlFile # Borrar el local
}
# --- FIN DE LA NUEVA LÓGICA ---


if (-not $success) {
    Write-Host "(ADVERTENCIA) No se pudieron cargar los datos automáticamente" -ForegroundColor Yellow
    Write-Host "    Puedes cargarlos manualmente después con:" -ForegroundColor Gray
    Write-Host "    docker exec -it reservas-mysql mysql -ureservas_user -preservas1as_db" -ForegroundColor Gray
}
Write-Host ""

# Esperar a que Java termine de iniciar
Write-Host "6. Esperando a que Java Spring Boot termine de iniciar..." -ForegroundColor Yellow
Start-Sleep -Seconds 20
Write-Host "(OK) Servicios backend listos" -ForegroundColor Green
Write-Host ""

# Verificar estado de contenedores
Write-Host "7. Verificando estado de contenedores..." -ForegroundColor Yellow
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
Write-Host ""

# Levantar React
Write-Host "8. Iniciando React Frontend..." -ForegroundColor Yellow
Set-Location ..\Vistas_React
Start-Process powershell -ArgumentList "-NoExit", "-Command", "npm run dev"
Set-Location ..\Migracion_Java
Write-Host "(OK) React iniciado en otra ventana" -ForegroundColor Green
Write-Host ""

# Resumen final
Write-Host "=== SISTEMA LISTO ===" -ForegroundColor Green
Write-Host ""
Write-Host "URLs disponibles:" -ForegroundColor Cyan
Write-Host "    - React:      http://localhost:5173" -ForegroundColor White
Write-Host "    - Java API:   http://localhost:8080/api" -ForegroundColor White
Write-Host "    - Python:     http://localhost:8000" -ForegroundColor White
Write-Host "    - Swagger:    http://localhost:8080/swagger-ui.html" -ForegroundColor White
Write-Host ""
Write-Host "Credenciales MySQL:" -ForegroundColor Cyan
Write-Host "    - Usuario: reservas_user" -ForegroundColor White
Write-Host "    - Password: reservas123" -ForegroundColor White
Write-Host "    - Puerto: 3307" -ForegroundColor White
Write-Host ""
Write-Host "Credenciales PostgreSQL:" -ForegroundColor Cyan
Write-Host "    - Usuario: analytics_user" -ForegroundColor White
Write-Host "    - Password: analytics123" -ForegroundColor White
Write-Host "    - Puerto: 5432" -ForegroundColor White
Write-Host ""
Write-Host "--- IMPORTANTE: Pasos para crear usuarios ---" -ForegroundColor Yellow
Write-Host ""
Write-Host "Este script YA NO crea usuarios. Debes crearlos desde la app." -ForegroundColor White
Write-Host ""
Write-Host " 1. CREAR USUARIO NORMAL:" -ForegroundColor Cyan
Write-Host "    - Abre http://localhost:5173" -ForegroundColor White
Write-Host "    - Ve a 'Register'." -ForegroundColor White
Write-Host "    - Regístrate con 'prueba@test.com' / '123456'." -ForegroundColor White
Write-Host "    - Inicia sesión y verifica que puedes ver las 20 reservas." -ForegroundColor White
Write-Host ""
Write-Host " 2. CREAR USUARIO ADMIN (para tu video):" -ForegroundColor Cyan
Write-Host "    - Cierra sesión." -ForegroundColor White
Write-Host "    - Ve a 'Register' OTRA VEZ." -ForegroundColor White
Write-Host "    - Regístrate con 'admin@test.com' / 'admin123'." -ForegroundColor White
Write-Host "    - (Fallará si intentas iniciar sesión ahora, es normal)." -ForegroundColor White
Write-Host "    - Abre una NUEVA terminal de PowerShell (deja esta corriendo)." -ForegroundColor White
Write-Host "    - Pega este ÚNICO comando y presiona Enter:" -ForegroundColor White
Write-Host ""
Write-Host '    docker exec -it reservas-mysql mysql -ureservas_user -preservas123 reservas_db -e "UPDATE users SET role=''ROLE_ADMIN'' WHERE email=''admin@test.com'';"' -ForegroundColor Green
Write-Host ""
Write-Host "    - ¡LISTO! Ahora inicia sesión con 'admin@test.com' / 'admin123'." -ForegroundColor White
Write-Host "    - El login de admin funcionará mientras no vuelvas a ejecutar 'docker-compose down -v'." -ForegroundColor White
Write-Host ""
Write-Host "Comandos útiles:" -ForegroundColor Yellow
Write-Host "    - Detener todo: docker-compose down" -ForegroundColor White
Write-Host "    - Reiniciar limpio: .\arranque.ps1" -ForegroundColor White
Write-Host ""