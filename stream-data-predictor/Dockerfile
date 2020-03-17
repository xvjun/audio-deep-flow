FROM java:8
MAINTAINER xujun 15604288825@163.com
ENV TZ=Asia/Shanghai
COPY target/stream-data-predictor-1.0-SNAPSHOT.jar /stream-data-predictor.jar

ENTRYPOINT ["java", "-jar", "/stream-data-predictor.jar"]