FROM amazoncorretto:17-alpine
RUN apk update && apk add bash

COPY build/libs/hotel-server.jar hotel-server.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/hotel-server.jar"]
