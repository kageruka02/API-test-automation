FROM maven:3.9.6-eclipse-temurin-17

# working directory in the container
WORKDIR /app

# copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:resolve -q

# copy the rest of the project
COPY src/ src/

# run tests when container starts
CMD ["mvn", "clean", "test"]