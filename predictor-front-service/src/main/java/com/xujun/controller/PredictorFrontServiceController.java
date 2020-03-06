package com.xujun.controller;


import com.xujun.config.PredictorEnvConfig;
import com.xujun.model.req.Request;
import com.xujun.response.Result;
import com.xujun.service.PredictorFrontService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/predictor")
public class PredictorFrontServiceController {

    @Autowired
    PredictorFrontService predictorFrontService;

    /**
     * 接收数据，解析格式，调用预估，写入mysql，返回预估结果
     */

    @PostMapping("/data")
    public Result sendData(@RequestBody Request request){

        return predictorFrontService.sendData(request, PredictorEnvConfig.FROM_HTTP);
    }

}
