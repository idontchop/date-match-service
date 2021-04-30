FROM openjdk:11
WORKDIR /
#ENV MYSQL_HOST=mysql1
ADD target/{IMAGE}-{VERSION}.jar {IMAGE}-{VERSION}.jar
RUN mkdir -p /root/.ssh
CMD java -jar -Dspring.profiles.active=prod {IMAGE}-{VERSION}.jar

