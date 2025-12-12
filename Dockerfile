# ==========================================================
# ETAPA 1: BUILD (Compilación y Generación del JAR)
# Usamos una imagen que tiene Maven y JDK
# ==========================================================
FROM maven:3.8.6-eclipse-temurin-17 AS build

# 1. Establece el directorio de trabajo
WORKDIR /app

# 2. Copia el archivo pom.xml para descargar dependencias primero
COPY pom.xml .

# 3. Descarga las dependencias (se aprovecha el cache)
RUN mvn dependency:go-offline

# 4. Copia el código fuente completo
COPY src ./src

# 5. Compila y empaqueta el proyecto, generando el JAR en target/
RUN mvn package -DskipTests

# ==========================================================
# ETAPA 2: RUNTIME (Ejecución Final)
# Usamos una imagen ligera para minimizar el tamaño
# ==========================================================
FROM eclipse-temurin:17-jdk-alpine

# 1. Establece el directorio de trabajo para la ejecución
WORKDIR /app

# 2. Copia SOLAMENTE el JAR compilado de la Etapa 1 (Build)
# El nombre del archivo JAR debe coincidir con el que genera Maven
COPY --from=build /app/target/ms-transacciones-0.0.1-SNAPSHOT.jar app.jar

# 3. El puerto por defecto de Spring Boot
EXPOSE 8080

# 4. Comando de inicio
ENTRYPOINT ["java", "-jar", "app.jar"]