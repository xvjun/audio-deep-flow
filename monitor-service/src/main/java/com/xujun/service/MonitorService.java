package com.xujun.service;

import com.xujun.config.CommonEnvConfig;
import com.xujun.config.K8sEnvConfig;
import com.xujun.dao.MonitorMapper;
import com.xujun.model.*;
import com.xujun.model.mapper.BarSearchTypeByTimeInfo;
import com.xujun.model.mapper.LineLocationByTimeInfo;
import com.xujun.model.mapper.LineSearchTypeByTimeInfo;
import com.xujun.model.mapper.ScatterByAllInfo;
import com.xujun.response.Result;
import com.xujun.response.ResultCode;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.models.*;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class MonitorService {

    @Autowired
    private K8sService k8sService;

    @Autowired
    private MonitorMapper monitorMapper;

    @Value("${audio-deep-flow.k8s.namespace}")
    private String k8sNamespace;

    private static Logger logger = LoggerFactory.getLogger(MonitorService.class);

    private DateFormat df=new SimpleDateFormat(CommonEnvConfig.DATE_FORMAT);

    /**
     * 1.返回各个组件（包括动态服务）的状态和信息
     *  固定服务组件，job,serving,stream
     */

    public Result getNativeComponent(){
        List<DeploymentInformation> deploymentInformations = (List<DeploymentInformation>)getDeployStatus().getData();
        List<DeploymentInformation> deploys = new ArrayList<>();
        for (DeploymentInformation information : deploymentInformations) {
            for (String s : CommonEnvConfig.set) {
                if(information.getName().startsWith(s)){
                    deploys.add(information);
                }
            }
        }

        List<ServiceInformation> serviceInformationList = (List<ServiceInformation>)getSvcStatus().getData();
        List<ServiceInformation> svcs = new ArrayList<>();
        for (ServiceInformation serviceInformation : serviceInformationList) {
            for (String s : CommonEnvConfig.set) {
                if(serviceInformation.getName().startsWith(s)){
                    svcs.add(serviceInformation);
                }
            }
        }

        List<PodInformation> podInformationList = (List<PodInformation>)getPodStatus().getData();
        List<PodInformation> pods = new ArrayList<>();
        for (PodInformation podInformation : podInformationList) {
            for (String s : CommonEnvConfig.set) {
                if(podInformation.getName().startsWith(s)){
                    pods.add(podInformation);
                }
            }
        }

        Component rs = new Component();
        rs.setDeploymentInformationList(deploys);
        rs.setPodInformationList(pods);
        rs.setServiceInformationList(svcs);
        return Result.success(rs);
    }


    public Result getDynamicComponent(){
        List<DeploymentInformation> deploymentInformations = (List<DeploymentInformation>)getDeployStatus().getData();
        List<DeploymentInformation> deploys = new ArrayList<>();
        for (DeploymentInformation information : deploymentInformations) {
            boolean flag = false;
            for (String s : CommonEnvConfig.set) {
                if(information.getName().startsWith(s)){
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                deploys.add(information);
            }
        }

        List<ServiceInformation> serviceInformationList = (List<ServiceInformation>)getSvcStatus().getData();
        List<ServiceInformation> svcs = new ArrayList<>();
        for (ServiceInformation serviceInformation : serviceInformationList) {
            boolean flag = false;
            for (String s : CommonEnvConfig.set) {
                if(serviceInformation.getName().startsWith(s)){
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                svcs.add(serviceInformation);
            }
        }

        List<PodInformation> podInformationList = (List<PodInformation>)getPodStatus().getData();
        List<PodInformation> pods = new ArrayList<>();
        for (PodInformation podInformation : podInformationList) {
            boolean flag = false;
            for (String s : CommonEnvConfig.set) {
                if(podInformation.getName().startsWith(s)){
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                pods.add(podInformation);
            }
        }

        List<JobInformation> jobInformationList = (List<JobInformation>)getJobStatus().getData();

        Component rs = new Component();
        rs.setDeploymentInformationList(deploys);
        rs.setPodInformationList(pods);
        rs.setServiceInformationList(svcs);
        rs.setJobInformationList(jobInformationList);
        return Result.success(rs);
    }

    public Result getDeployStatus(){
        V1DeploymentList deploys = null;
        List<DeploymentInformation> deploymentList = new ArrayList<>();
        try {
            deploys =  k8sService.getDeployList();
        } catch (ApiException e) {
            e.printStackTrace();
            logger.error(" Failed to getDeployList");
            return Result.result(ResultCode.GET_K8S_DEPLOY_LIST_FAILED);
        }
        for (V1Deployment item : deploys.getItems()) {
            DeploymentInformation deploymentInformation = new DeploymentInformation();
            deploymentInformation.setName(item.getMetadata().getName());
            deploymentInformation.setReplicas(item.getStatus().getReplicas());
            deploymentInformation.setReadyReplicas(item.getStatus().getReadyReplicas());
            if(deploymentInformation.getReadyReplicas() == deploymentInformation.getReplicas()){
                deploymentInformation.setStatus(K8sEnvConfig.SUCCESS_STATUS);
            }else if(deploymentInformation.getReadyReplicas() > 0){
                deploymentInformation.setStatus(K8sEnvConfig.FAILED_STATUS);
            }else{
                deploymentInformation.setStatus(K8sEnvConfig.ERROR_STATUS);
            }
            DateTime dateTime = item.getMetadata().getCreationTimestamp();
            deploymentInformation.setRunTime((System.currentTimeMillis() - dateTime.toDate().getTime())/1000);
            deploymentInformation.setCreateTime(df.format(dateTime.toDate()));
            deploymentList.add(deploymentInformation);
        }
        return Result.success(deploymentList);
    }

    public Result getJobStatus(){
        V1JobList jobs = null;
        List<JobInformation> jobList = new ArrayList<>();
        try {
            jobs =  k8sService.getJobList();

        } catch (ApiException e) {
            e.printStackTrace();
            logger.error(" Failed to getJobList");
            return Result.result(ResultCode.GET_K8S_JOB_LIST_FAILED);
        }
        for (V1Job item : jobs.getItems()) {
            JobInformation jobInformation = new JobInformation();
            jobInformation.setName(item.getMetadata().getName());
            jobInformation.setCreateTime(df.format(item.getStatus().getStartTime().toDate()));
            Long times = item.getStatus().getCompletionTime().toDate().getTime() - item.getStatus().getStartTime().toDate().getTime();
            jobInformation.setRunTime(times/1000);
            if(item.getStatus().getSucceeded() >= 1){
                jobInformation.setStatus(K8sEnvConfig.JOB_SUCCESS_STATUS);
            }else{
                jobInformation.setStatus(K8sEnvConfig.JOB_FAILED_STATUS);
            }
            jobList.add(jobInformation);
        }
        return Result.success(jobList);
    }

    public Result getSvcStatus(){
        V1ServiceList svcs = null;
        List<ServiceInformation> serviceList = new ArrayList<>();
        try {
            svcs =  k8sService.getSvcList();

        } catch (ApiException e) {
            e.printStackTrace();
            logger.error(" Failed to getSvcList");
            return Result.result(ResultCode.GET_K8S_SVC_LIST_FAILED);
        }
        for (V1Service item : svcs.getItems()) {
            ServiceInformation serviceInformation = new ServiceInformation();
            serviceInformation.setName(item.getMetadata().getName());
            serviceInformation.setType(item.getSpec().getType());
            serviceInformation.setNodePort(item.getSpec().getPorts().get(0).getNodePort());
            serviceInformation.setTargetPort(item.getSpec().getPorts().get(0).getTargetPort().getIntValue());
            serviceInformation.setProtocol(item.getSpec().getPorts().get(0).getProtocol());
            serviceInformation.setCreateTime(df.format(item.getMetadata().getCreationTimestamp().toDate()));
            serviceInformation.setRunTime((System.currentTimeMillis()-item.getMetadata().getCreationTimestamp().toDate().getTime())/1000);
            serviceList.add(serviceInformation);
        }
        return Result.success(serviceList);
    }

    public Result getPodStatus(){
        V1PodList pods = null;
        List<PodInformation> podList = new ArrayList<>();
        try {
            pods =  k8sService.getPodList();

        } catch (ApiException e) {
            e.printStackTrace();
            logger.error(" Failed to getPodList");
            return Result.result(ResultCode.GET_K8S_POD_LIST_FAILED);
        }
        for (V1Pod item : pods.getItems()) {
            PodInformation podInformation = new PodInformation();
            podInformation.setName(item.getMetadata().getName());
            podInformation.setNodeName(item.getSpec().getNodeName());
            podInformation.setHostIP(item.getStatus().getHostIP());
            Long times = System.currentTimeMillis() - item.getStatus().getStartTime().toDate().getTime();
            podInformation.setRunTime(times/1000);
            podInformation.setCreateTime(df.format(item.getStatus().getStartTime().toDate()));
            podInformation.setRestartCount(item.getStatus().getContainerStatuses().get(0).getRestartCount());
            if(item.getStatus().getContainerStatuses().get(0).isReady()){
                podInformation.setStatus(K8sEnvConfig.SUCCESS_STATUS);
            }else{
                podInformation.setStatus(K8sEnvConfig.FAILED_STATUS);
            }

            podList.add(podInformation);
        }
        return Result.success(podList);
    }




    public Result BarSearchTypeByTime(String startTime, String endTime) {
        List<BarSearchTypeByTimeInfo> rs = monitorMapper.BarSearchTypeByTime(startTime,endTime);
        List<Integer> list = new ArrayList<>();
        Map<Integer,Integer> map = new HashMap<>();
        for (BarSearchTypeByTimeInfo r : rs) {
            map.put(r.getLabel(),r.getCount());
        }
        for (int i = 0; i < CommonEnvConfig.labelMap.size(); i++) {
            list.add(map.getOrDefault(i,0));
        }
        return Result.success(list);
    }

    public Result PieSearchTypeByTime(String startTime, String endTime) {
        List<BarSearchTypeByTimeInfo> rs = monitorMapper.BarSearchTypeByTime(startTime,endTime);
        List<Map> list = new ArrayList<>();
        Map<Integer,Integer> map = new HashMap<>();
        for (BarSearchTypeByTimeInfo r : rs) {
            map.put(r.getLabel(),r.getCount());
        }
        for (int i = 0; i < CommonEnvConfig.labelMap.size(); i++) {
            String name = CommonEnvConfig.labelMap.get(i);
            Integer value = map.getOrDefault(i,0);
            Map<String,Object> item = new HashMap<String,Object>(){{
                put("name",name);
                put("value",value);
            }};
            list.add(item);
        }
        return Result.success(list);
    }

    public Result LineLocationByTime(String startTime, String endTime) {
        List<LineLocationByTimeInfo> rs = monitorMapper.LineLocationByTime(startTime, endTime);
        List<List> list = new ArrayList<>();
        List<Integer> locationList = new ArrayList<>();
        Map<String,Integer> map = new HashMap<>();
        for (LineLocationByTimeInfo r : rs) {
            if(!locationList.contains(r.getLocation())){
                locationList.add(r.getLocation());
            }
            map.put(r.getLocation().toString()+r.getLabel().toString(),r.getCount());
        }
        locationList.sort((a,b) -> a-b);
        list.add(locationList);

        List<Integer> label1 = new ArrayList<>();
        List<Integer> label2 = new ArrayList<>();
        List<Integer> label3 = new ArrayList<>();
        List<Integer> labelAll = new ArrayList<>();

        for (int j = 0; j < locationList.size(); j++) {
            for (int i = 1; i < CommonEnvConfig.labelMap.size(); i++) {
                String key = locationList.get(j).toString() + String.valueOf(i);
                if(i == 1){
                    label1.add(map.getOrDefault(key,0));
                }else
                if(i == 2){
                    label2.add(map.getOrDefault(key,0));
                }else
                if(i == 3){
                    label3.add(map.getOrDefault(key,0));
                }
            }
            labelAll.add(label1.get(j)+label2.get(j)+label3.get(j));
        }
        list.add(label1);
        list.add(label2);
        list.add(label3);
        list.add(labelAll);

        return Result.success(list);
    }

    public Result LineSearchTypeByTime(String startTime, String endTime, Integer interval) {
        Integer intervalLength = 0;
        if(interval == 1){  //10min
            intervalLength=15;
        } else if(interval == 2){ //1小时
            intervalLength=13;
        } else if(interval == 3){ //天
            intervalLength=10;
        } else if(interval == 4){  //月
            intervalLength=7;
        }
        List<LineSearchTypeByTimeInfo> rs = monitorMapper.LineSearchTypeByTime(startTime, endTime, intervalLength);

        List<List> list = new ArrayList<>();
        List<String> timeList = new ArrayList<>();
        Map<String,Integer> map = new HashMap<>();
        for (LineSearchTypeByTimeInfo r : rs) {
            String time = r.getTime();
            if(interval == 1){  //10min
                time = time + "0:00";
            } else if(interval == 2){ //1小时
                time = time + ":00:00";
            }
            if(!timeList.contains(time)){
                timeList.add(time);
            }
            map.put(time+r.getLabel().toString(),r.getCount());
        }
        list.add(timeList);

        List<Integer> label1 = new ArrayList<>();
        List<Integer> label2 = new ArrayList<>();
        List<Integer> label3 = new ArrayList<>();
        List<Integer> labelAll = new ArrayList<>();

        for (int j = 0; j < timeList.size(); j++) {
            for (int i = 1; i < CommonEnvConfig.labelMap.size(); i++) {
                String key = timeList.get(j) + String.valueOf(i);
                if(i == 1){
                    label1.add(map.getOrDefault(key,0));
                }else
                if(i == 2){
                    label2.add(map.getOrDefault(key,0));
                }else
                if(i == 3){
                    Integer tp = map.getOrDefault(key,0);
                    label3.add(tp);
                }
            }
            labelAll.add(label1.get(j)+label2.get(j)+label3.get(j));
        }

        list.add(label1);
        list.add(label2);
        list.add(label3);
        list.add(labelAll);

        return Result.success(list);

    }

    public Result ScatterByAll(String startTime, String endTime) {
        List<ScatterByAllInfo> rs = monitorMapper.ScatterByAll(startTime, endTime);
        List<List> label0 = new ArrayList<>();
        List<List> label1 = new ArrayList<>();
        List<List> label2 = new ArrayList<>();
        List<List> label3 = new ArrayList<>();
        try {
            for (ScatterByAllInfo r : rs) {
                List<Object> item = new ArrayList<>();
                Long ts = df.parse(r.getTime()).getTime();

                item.add(r.getLocation());
                item.add(ts);
                item.add(r.getTime());
                item.add(r.getLabel());
                item.add(CommonEnvConfig.labelMap.getOrDefault(r.getLabel(),"其他"));
                if(r.getLabel() == 0){
                    label0.add(item);
                }else if(r.getLabel() == 1){
                    label1.add(item);
                }else if(r.getLabel() == 2){
                    label2.add(item);
                }else if(r.getLabel() == 3){
                    label3.add(item);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            logger.error("failed to format time ");
            return Result.result(ResultCode.FORMAT_DATE_FAILED);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("label0", label0);
        map.put("label1", label1);
        map.put("label2", label2);
        map.put("label3", label3);

        return Result.success(map);

    }
}
