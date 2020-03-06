package com.xujun.controller;


import com.xujun.model.req.CreateStreamRequest;
import com.xujun.response.Result;
import com.xujun.service.StreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stream-data-predictor")
public class StreamController {

    @Autowired
    StreamService streamService;

    /**
     * 请求：servingName，cpu，memory，instance，kakfaaddress+topic, nodeport
     * streamInformation: streamId,streamName,startTime,(servingName，cpu，memory，instance，kakfaaddress+topic, nodeport)
     */

    @GetMapping("/stream/list/page")
    public Result getStreamPage(@RequestParam("displayName") String displayName,
                               @RequestParam("offset") Integer offset,
                               @RequestParam("limit") Integer limit){
        return streamService.getStreamPage(displayName, offset, limit);
    }

    @DeleteMapping("/stream")
    public Result deleteModel(@RequestParam("streamId") Integer streamId){
        return streamService.deleteStreamById(streamId);
    }

    @PostMapping("/stream")
    public Result createStream(@RequestBody CreateStreamRequest createStreamRequest){
        return streamService.createStream(createStreamRequest);
    }


}
