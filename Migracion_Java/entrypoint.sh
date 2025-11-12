#!/bin/sh
# entrypoint.sh (CORREGIDO)

# Salir inmediatamente si un comando falla
set -e

# Variables de entorno (ya definidas en docker-compose.yml)
DB_HOST="reservas-mysql"
DB_USER="reservas_user"
DB_PASSWORD="reservas123"

echo "Esperando a que MySQL inicie..."

# Bucle hasta que MySQL esté listo.
# Usamos "default-mysql-client" que instalamos en el Dockerfile
until mysqladmin ping -h"$DB_HOST" -u"$DB_USER" -p"$DB_PASSWORD" --silent; do
    echo "MySQL no está listo... esperando 1 segundo."
    sleep 1
done

echo "MySQL iniciado correctamente."

# --- ¡LA LÍNEA MÁGICA Y CRÍTICA! ---
# Esta línea le dice al script que, una vez terminado,
# ejecute el comando "CMD" del Dockerfile (es decir, "java -jar app.jar")
exec "$@"