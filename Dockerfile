# Usamos una imagen base ligera de Java 21
FROM eclipse-temurin:21-jdk-alpine

# Creamos un volumen temporal para logs o caché
VOLUME /tmp

# Copiamos el archivo .jar generado por Maven al contenedor
# Asegúrate de que tu build genera el jar en la carpeta target
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Exponemos el puerto del microservicio
EXPOSE 8083

# Comando para iniciar la aplicación
ENTRYPOINT ["java","-jar","/app.jar"]