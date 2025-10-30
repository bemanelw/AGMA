FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Копируем Maven wrapper и pom.xml
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw

# Скачиваем зависимости (кешируем этот слой)
RUN ./mvnw dependency:go-offline -B

# Копируем исходный код
COPY src ./src

# Собираем приложение (JAR создастся автоматически!)
RUN ./mvnw clean package -DskipTests

# Запускаем приложение
CMD ["java", "-jar", "target/*.jar"]