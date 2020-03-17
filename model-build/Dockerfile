FROM java:8
MAINTAINER xujun 15604288825@163.com
ENV TZ=Asia/Shanghai
COPY target/model-build-1.0-SNAPSHOT.jar /model-build.jar

ENTRYPOINT ["java", "-jar", "/model-build.jar"]