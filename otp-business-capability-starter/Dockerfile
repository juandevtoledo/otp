FROM openjdk:8-jdk-alpine
WORKDIR /opt
ADD build/libs/otp-business-capability-starter-*SNAPSHOT.jar /opt/app.jar
ADD build/resources/main/newrelic/newrelic.jar /opt/newrelic.jar
RUN sh -c 'touch /opt/app.jar'
ENTRYPOINT ["java", "-javaagent:newrelic.jar", "-Dloader.path=/opt", "-Djava.security.egd=file:/dev/./urandom","-jar","app.jar"]
