FROM openjdk:17
ADD target/*.jar pouet-service
ENTRYPOINT ["java", "-jar","pouet-service"]
EXPOSE 3335

COPY access-refresh-token-keys/* /access-refresh-token-keys/