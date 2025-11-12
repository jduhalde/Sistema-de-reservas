# Usamos una imagen de Java 17
FROM eclipse-temurin:17-jre-jammy

# Instalamos mysql-client para el script de health check
RUN apt-get update && apt-get install -y default-mysql-client

# Establecemos el directorio de trabajo
WORKDIR /app

# --- ¡VOLVEMOS A LA RUTA ORIGINAL Y CORRECTA! ---
COPY target/gestion-reservas-1.0.0.jar app.jar
# --- FIN DE LA CORRECCIÓN ---

# Copiamos el script de entrypoint
COPY entrypoint.sh .

# Damos permisos de ejecución al script
RUN chmod +x entrypoint.sh

# Exponemos el puerto
EXPOSE 8080

# Comando para ejecutar la app (lo toma el entrypoint)
CMD ["java", "-jar", "app.jar"]


