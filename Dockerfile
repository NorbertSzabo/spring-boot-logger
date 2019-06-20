FROM java:8
EXPOSE 8080
ADD /target/spring-boot-dknv-logger-0.0.1-SNAPSHOT.jar spring-boot-dknv-logger.jar
ENTRYPOINT ["java","-jar","spring-boot-dknv-logger.jar"]