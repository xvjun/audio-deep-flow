package com.xujun.controller;

import com.xujun.model.req.CreateModelRequest;
import com.xujun.response.Result;
import com.xujun.service.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/modelbuild")
public class JobController {

    private static Logger logger = LoggerFactory.getLogger(JobController.class);

    @Autowired
    JobService jobService;

    /**
     *
     *
     * 1.获得job列表，ok、
     * 2.停止job
     * 2.1 删除job，ok
     * 3.获得模型列表
     * 4.删除model
     * 5.下载model
     */

    @PostMapping("/job")
    public Result createJob(@RequestBody CreateModelRequest createModelRequest) {
        return jobService.createModel(createModelRequest);
    }


    @PostMapping("/job/callback")
    public Result jobCallBack(@RequestBody Map<String, String> params) {
        if(params.get("job_id").isEmpty()){
            logger.error("job_id is not exist");
            return Result.failure("job_id is not exist");
        }else{
            jobService.jobCallBack(Integer.parseInt(params.get("job_id")));
        }
        return Result.success();
    }

    @GetMapping("/job/list/page")
    public Result getJobPage(@RequestParam("displayName") String displayName,
                              @RequestParam("offset") Integer offset,
                              @RequestParam("limit") Integer limit){
        return jobService.getJobPage(displayName, offset, limit);
    }

    @DeleteMapping("/job")
    public Result deleteJob(@RequestParam("jobId") Integer jobId){
        return jobService.deleteJobById(jobId);
    }




}
