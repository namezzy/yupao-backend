FROM maven:3.5-jdk-8-alpine as builder

# Copy local code to the container image.
WORKDIR /app
COPY pom.xml .
COPY src ./src
COPY target/yupao-backend-0.0.1-SNAPSHOT.jar yupao-backend-0.0.1-SNAPSHOT.jar

# Build a release artifact.
# RUN mvn package

# Run the web service on container startup.
CMD ["java","-jar","yupao-backend-0.0.1-SNAPSHOT.jar"]