FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw

RUN ./mvnw dependency:go-offline -B

COPY src ./src

RUN ./mvnw clean package -DskipTests

# Используем переменную PORT
CMD ["sh", "-c", "java -Dserver.port=$PORT -jar target/agma-0.0.1-SNAPSHOT.jar"]