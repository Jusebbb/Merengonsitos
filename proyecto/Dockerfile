FROM openjdk:17
WORKDIR /app
COPY . /app
RUN chmod +x mvnw
RUN ./mvnw clean install -DskipTests
EXPOSE 8080
CMD ["java", "-jar", "target/proyecto-0.0.1-SNAPSHOT.jar"]
