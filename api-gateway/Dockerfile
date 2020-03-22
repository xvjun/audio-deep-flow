FROM java:8
MAINTAINER xujun 15604288825@163.com
ENV TZ=Asia/Shanghai
COPY target/api-gateway-1.0-SNAPSHOT.jar /api-gateway.jar

ENTRYPOINT ["java", "-jar", "/api-gateway.jar"]