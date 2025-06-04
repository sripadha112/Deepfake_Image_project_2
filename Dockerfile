FROM eclipse-temurin:21-jdk
ADD target/project-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]