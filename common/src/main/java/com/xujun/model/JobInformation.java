package com.xujun.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobInformation {
    private Integer jobId;
    private String jobName;
    private String startTime;
    private Integer hiddenLayers;
    private Integer layersSize;
    private Float learningRate;
    private Integer epochs;
    private Float dropoutRate;
    private Integer classSum;
    private Long dataLength;
    private Long time;
    private Integer dataShardSum;
    private Integer dataShardReadySum;
    private Integer isCompleted;
    private String rootPath;
    private Float cpu;
    private Integer memory;
}

