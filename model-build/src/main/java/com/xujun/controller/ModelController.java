package com.xujun.controller;

import com.xujun.model.req.CreateModelRequest;
import com.xujun.response.Result;
import com.xujun.service.ModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/modelbuild")
public class ModelController {

    private static Logger logger = LoggerFactory.getLogger(ModelController.class);

    @Autowired
    ModelService modelService;

    /**
     * 1.对模型的增删改查
     * 2.建模job
     * 3.任务的状态查看
     *
     */

    @PostMapping("/model")
    public Result createModel(@RequestBody CreateModelRequest createModelRequest) {
        return modelService.createModel(createModelRequest);
    }


    @PostMapping("/job/callback")
    public Result jobCallBack(@RequestBody Map<String, String> params) {
        if(params.get("job_id").isEmpty()){
            logger.error("job_id is not exist");
            return Result.failure("job_id is not exist");
        }else{
            modelService.jobCallBack(Integer.parseInt(params.get("job_id")));
        }
        return Result.success();
    }

}
