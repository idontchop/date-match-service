FROM openjdk:12
WORKDIR /
ENV MYSQL_HOST=mysql1
ADD target/portfolioChat-0.0.1-SNAPSHOT.jar portfolioChat-0.0.1-SNAPSHOT.jar
RUN mkdir -p /root/.ssh
CMD java -jar -Dspring.profiles.active=prod portfolioChat-0.0.1-SNAPSHOT.jar

