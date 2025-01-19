# Build Stage
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

COPY mvnw .
COPY .mvn/ .mvn/

COPY pom.xml .
COPY src ./src


# Debug: List files in src/main/resources and build the application
RUN ls -la src/main/resources && chmod +x mvnw && ./mvnw clean package -DskipTests





# Building Image Stage
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/emrs-0.0.1.jar /app/emrs-0.0.1.jar
EXPOSE 8080
CMD ["java", "-jar", "/app/emrs-0.0.1.jar"]