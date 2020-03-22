package com.xujun.controller;


import com.xujun.response.Result;
import com.xujun.service.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/k8smonitor")
public class K8sController {

    /**
     * 1.返回各个组件（包括动态服务）的状态和信息
     *  固定服务组件，job,serving,stream
     */

    @Autowired
    private MonitorService monitorService;

    @GetMapping("/status/deploy")
    public Result getDeployStatus(){
        return monitorService.getDeployStatus();
    }

    @GetMapping("/status/job")
    public Result getJobStatus(){
        return monitorService.getJobStatus();
    }

    @GetMapping("/status/svc")
    public Result getSvcStatus(){
        return monitorService.getSvcStatus();
    }

    @GetMapping("/status/pod")
    public Result getPodStatus(){
        return monitorService.getPodStatus();
    }

    @GetMapping("/status/nativecomponent")
    public Result getNativeComponent(){
        return monitorService.getNativeComponent();
    }

    @GetMapping("/status/dynamiccomponent")
    public Result getDynamicComponent(){
        return monitorService.getDynamicComponent();
    }




}
