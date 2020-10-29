FROM openjdk:11

# Install dependencies
RUN apt update
RUN apt install -y maven build-essential

# Building the JAR
RUN mkdir /build
WORKDIR /build
COPY pom.xml pom.xml
RUN mvn dependency:resolve
RUN mvn dependency:resolve-plugins
COPY src src
RUN mvn clean install spring-boot:repackage

# Create working directory
RUN mkdir /app
WORKDIR /app

# Move the latest build to the correct location
RUN mv /build/target/alexandria-0.0.1-SNAPSHOT.jar backend.jar

# Boot up the server
EXPOSE 8080
CMD ["java", "-server", "-XX:+UnlockExperimentalVMOptions", "-Xmx2G", "-XX:+UseG1GC", "-XX:+UseStringDeduplication", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "backend.jar"]
