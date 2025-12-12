# ETAPA 1: Construcción (Build)
# Usamos una imagen de Maven con Java 21 para compilar el proyecto
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Establecemos el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos solo el archivo de dependencias primero (para aprovechar la caché de Docker)
COPY pom.xml .

# Descargamos las dependencias (esto se guarda en caché si el pom.xml no cambia)
RUN mvn dependency:go-offline

# Copiamos el código fuente
COPY src ./src

# Compilamos el proyecto y creamos el JAR (saltando los tests para ir más rápido)
RUN mvn clean package -DskipTests

# ETAPA 2: Ejecución (Run)
# Usamos una imagen ligera de Java 21 solo para correr la app
FROM eclipse-temurin:21-jre-alpine

# Directorio de trabajo
WORKDIR /app

# Copiamos el JAR generado en la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto 8080 (donde corre Spring Boot)
EXPOSE 8080

# Comando para iniciar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]