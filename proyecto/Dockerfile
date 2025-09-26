FROM openjdk:17

WORKDIR /app
COPY . .

# Si tienes mvnw en el repo:
RUN chmod +x mvnw || true \
&& ./mvnw -q -DskipTests clean package

# Si NO tienes mvnw, comenta lo anterior y descomenta esto:
# RUN apt-get update && apt-get install -y maven && mvn -q -DskipTests clean package

# Copiamos/renombramos el jar para no depender del nombre exacto
RUN bash -lc 'cp target/*.jar app.jar'

EXPOSE 8080
CMD ["java","-jar","/app/app.jar"]