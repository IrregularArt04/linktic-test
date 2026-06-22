# Etapa 1: Compilación con JDK 21 y Maven (asumiendo compatibilidad de comandos)
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Ejecuta la compilación omitiendo los tests para el despliegue inicial [cite: 10]
RUN mvn clean package -DskipTests

# Etapa 2: Imagen de ejecución ligera con Java 21
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# Exponer el puerto correspondiente (8081 para Productos, 8082 para Inventario)
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]