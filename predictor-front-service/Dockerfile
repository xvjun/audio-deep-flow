FROM java:8
MAINTAINER xujun 15604288825@163.com
ENV TZ=Asia/Shanghai
COPY target/predictor-front-service-1.0-SNAPSHOT.jar /predictor-front-service.jar

ENTRYPOINT ["java", "-jar", "/predictor-front-service.jar"]