FROM openjdk:17
ADD target/*.jar messaging-service
ENTRYPOINT ["java", "-jar","messaging-service"]
EXPOSE 3336

COPY access-refresh-token-keys/* /access-refresh-token-keys/