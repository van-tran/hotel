FROM amazoncorretto:17-alpine
WORKDIR /home/application/java
RUN apk update && apk add bash

COPY build/libs/HotelsDataMerger-0.0.1-SNAPSHOT.jar hotels-server.jar

EXPOSE 8080

CMD [ "entrypoint.sh" ]
ENTRYPOINT ["java","-jar","/hotels-server.jar"]
