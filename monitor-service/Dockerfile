FROM java:8
MAINTAINER xujun 15604288825@163.com
ENV TZ=Asia/Shanghai
COPY target/monitor-service-1.0-SNAPSHOT.jar /monitor-service.jar

ENTRYPOINT ["java", "-jar", "/monitor-service.jar"]