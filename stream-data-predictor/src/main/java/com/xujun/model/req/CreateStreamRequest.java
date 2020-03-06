package com.xujun.model.req;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateStreamRequest {
    private String servingName;
    private Float cpu;
    private Integer memory;
    private Integer instance;
    private String kafkaAddress;
    private String receiverTopics;
    private String sendTopics;
    private Integer nodePort;
}
