FROM openjdk:8 as runtime
FROM gradle:4.8.1-jdk8 as builder
#Avoid issue Failed to load native library 'libnative-platform.so' for Linux amd64
USER root
WORKDIR /home/gradle
COPY . .
RUN gradle build
RUN mv build/libs/*.jar app.jar

FROM openjdk:8 as runtime
WORKDIR /workspace
COPY --from=builder /home/gradle/app.jar /workspace/app.jar
CMD ["java", "-jar", "app.jar"]
EXPOSE 8080
