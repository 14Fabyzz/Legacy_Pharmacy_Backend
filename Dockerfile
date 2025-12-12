# Java 17 (versión ligera)
FROM eclipse-temurin:17-jdk-alpine

# 2. Directorio de trabajo dentro del contenedor
WORKDIR /app

# 3. Copiamos el JAR generado.
# Asegúrate de que el nombre coincida con el que sale en tu carpeta 'target'
COPY target/ms-transacciones-0.0.1-SNAPSHOT.jar app.jar

# 4. Exponemos el puerto 8080
EXPOSE 8080

# 5. Comando para iniciar la aplicación
ENTRYPOINT ["java", "-Duser.timezone=America/Bogota", "-jar", "app.jar"]