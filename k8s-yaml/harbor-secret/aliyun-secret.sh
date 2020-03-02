#!/usr/bin/env bash

#当使用harbor作为远程仓库时，并且在仓库为私有时，需要获得harbor的认证，将生产的认证绑定到yaml中
k create secret docker-registry aliyunsecret --namespace=audio-deep-flow --docker-server=registry.cn-hangzhou.aliyuncs.com \
 --docker-username=hadoopdlnu --docker-password=xj199804025511 --docker-email=15604288825@163.com