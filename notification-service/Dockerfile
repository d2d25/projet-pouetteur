FROM openjdk:17
ADD target/*.jar notification-service
ENTRYPOINT ["java", "-jar","notification-service"]
EXPOSE 3337

COPY access-refresh-token-keys/* /access-refresh-token-keys/