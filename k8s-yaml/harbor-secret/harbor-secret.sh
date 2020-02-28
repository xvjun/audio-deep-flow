#!/usr/bin/env bash

#当使用harbor作为远程仓库时，并且在仓库为私有时，需要获得harbor的认证，将生产的认证绑定到yaml中
k create secret docker-registry mysecret --namespace=micro-service --docker-server=172.27.160.10:8090 \
 --docker-username=xujun --docker-password=xj199804025511HL --docker-email=15604288825@163.com