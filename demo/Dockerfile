FROM adoptopenjdk/openjdk11:latest

RUN mkdir /opt/app

COPY . /opt/app

RUN chmod -R 777 /opt/app/mvnw

WORKDIR /opt/app

RUN ./mvnw clean package

CMD [ "java", "-jar", "/opt/app/target/demo-0.0.1-SNAPSHOT.jar" ]
