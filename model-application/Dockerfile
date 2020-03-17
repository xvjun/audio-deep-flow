FROM java:8
MAINTAINER xujun 15604288825@163.com
ENV TZ=Asia/Shanghai
COPY target/model-application-1.0-SNAPSHOT.jar /model-application.jar

ENTRYPOINT ["java", "-jar", "/model-application.jar"]