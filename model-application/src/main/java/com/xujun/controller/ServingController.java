package com.xujun.controller;


import com.xujun.model.req.CreateServingRequest;
import com.xujun.response.Result;
import com.xujun.service.ServingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/modelapp")
public class ServingController {

    private static Logger logger = LoggerFactory.getLogger(ServingController.class);

    @Autowired
    ServingService servingService;

    /**
     *
     * 1.启动模型服务，掉kafka
     * 2.删除模型服务，删除k8s的deploy，删除本地目录
     */

    @PostMapping("/serving")
    public Result createServing(@RequestBody CreateServingRequest createServingRequest){
        return servingService.createServing(createServingRequest);
    }

    @DeleteMapping("/serving")
    public Result deleteServing(@RequestParam("servingId") Integer servingId){
        return servingService.deleteServingById(servingId);
    }

}
