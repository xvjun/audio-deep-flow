package com.xujun.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StreamInformation {

    private Integer streamId;
    private String streamName;
    private String startTime;
    private String servingName;
    private Float cpu;
    private Integer memory;
    private Integer instance;
    private String kafkaAddress;
    private String receiverTopics;
    private String sendTopics;
    private Integer nodePort;
    private String httpAddress;

}

