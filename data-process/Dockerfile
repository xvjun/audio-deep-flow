FROM java:8
MAINTAINER xujun 15604288825@163.com
ENV TZ=Asia/Shanghai
COPY target/data-process-1.0-SNAPSHOT.jar /data-process.jar

ENTRYPOINT ["java", "-jar", "/data-process.jar"]