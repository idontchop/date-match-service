FROM openjdk:11
WORKDIR /
#ENV IMAGE=port
#ENV VERSION=${VERSION}
#ENV MYSQL_HOST=mysql1
ARG IMAGE
ARG VERSION
ADD target/$IMAGE-$VERSION.jar $IMAGE-$VERSION.jar
RUN mkdir -p /root/.ssh
CMD java -jar -Dspring.profiles.active=prod $IMAGE-$VERSION.jar

