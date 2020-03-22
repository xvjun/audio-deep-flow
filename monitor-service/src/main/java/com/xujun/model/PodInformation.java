package com.xujun.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PodInformation {
    private String name;
    private String nodeName;
    private String hostIP;
    private Long runTime;
    private String createTime;
    private String status;
    private Integer restartCount;
}
