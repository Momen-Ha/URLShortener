FROM maven:3.9.9-eclipse-temurin-17-alpine AS builder

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -DskipTests

# Stage 2
FROM eclipse-temurin:17-jre-alpine

# ensure the jar name ( can be defined in pom.xml )
COPY --from=builder /app/target/*.jar url-shortener-app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "url-shortener-app.jar"]



