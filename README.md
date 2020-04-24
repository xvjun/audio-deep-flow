基于kubernetes的实时音频信号智能预警平台的研究与实现
=

数据处理
-
  1.上传数据的方式有两种，本地上传和hdfs上传

  2.数据格式支持类型：wav,aif,mp3音频。同时支持已处理好的数据格式，
  包括列数128的小数和一个label
  
  3.如果是音频，需要将音频解析和归一化，写入hdfs


模型构建
- 

模型应用
- 

流式预估
-
监控服务
-
API geteway
-
kafka topic
-
  send-hdfsmanager
  
  hdfsmanager-send-dataprocess
  
  hdfsmanager-send-buildmodel
  
  hdfsmanager-send-servingapp
  
  default-send-predictor-front
  
  default-receiver-predictor-front



安装部署k8s集群参考
---
k8s实践(一)：Centos7.6部署k8s(v1.14.2)集群
https://blog.51cto.com/3241766/2405624

kubernetes v1.11.0 从代码编译到部署文档-部署
https://blog.csdn.net/qq_21816375/article/details/81983763

no matches for kind "Deployment" in version "extensions/v1beta1"
https://www.cnblogs.com/nnylee/p/11779653.html

Kubernetes-v1.17集群安装dashboard
https://blog.csdn.net/zz_aiytag/article/details/103874977/

kubernetes从harbor拉取镜像没有权限解决方法
https://blog.csdn.net/xukangkang1hao/article/details/80839834

步骤
-
  步骤：
  
  1.各机器ssh免密
  
  2.安装docker和k8s
  
  3.所有机器添加阿里云的docker login
  
  4.运行mysql，zk，kafka
  
  5.在kafka内创建所有topic
  
  6.检查yaml中的参数并修改
  
  7.运行k apply -f .
  
  8.访问http://ip:30090
  
