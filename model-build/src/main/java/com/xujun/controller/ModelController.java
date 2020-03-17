package com.xujun.controller;


import com.xujun.response.Result;
import com.xujun.service.ModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/modelbuild")
public class ModelController {

    private static Logger logger = LoggerFactory.getLogger(ModelController.class);

    @Autowired
    ModelService modelService;

//     3.获得模型列表
//     * 4.删除model
//     * 5.下载model

    @GetMapping("/model/list/page")
    public Result getModelPage(@RequestParam("displayName") String displayName,
                              @RequestParam("offset") Integer offset,
                              @RequestParam("limit") Integer limit){
        return modelService.getModelPage(displayName, offset, limit);
    }

    @DeleteMapping("/model")
    public Result deleteModel(@RequestParam("modelId") Integer modelId){
        return modelService.deleteModelById(modelId);
    }

    @GetMapping("/download/model")
    public Object downloadModeById(@RequestParam("modelId") Integer modelId){

        return modelService.downloadModelById(modelId);
    }


}
