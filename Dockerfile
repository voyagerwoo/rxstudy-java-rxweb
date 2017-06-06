FROM java:8

COPY ./target/rxstudy-java-rxweb-0.0.1-SNAPSHOT.jar /target/application.jar
WORKDIR /target
CMD java -jar application.jar

EXPOSE 7070