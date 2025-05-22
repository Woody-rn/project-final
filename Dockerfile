#build project
FROM maven:3.9.9-sapmachine-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

#run project
FROM openjdk:17
LABEL authors="Nikitin R.N."
WORKDIR /app
COPY --from=build /app/target/jira-1.0.jar jira.jar
COPY resources ./resources
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "jira.jar"]