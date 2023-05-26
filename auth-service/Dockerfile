FROM openjdk:17
ADD target/*.jar auth-service
ENTRYPOINT ["java", "-jar","auth-service"]
EXPOSE 3333

COPY access-refresh-token-keys/* /access-refresh-token-keys/