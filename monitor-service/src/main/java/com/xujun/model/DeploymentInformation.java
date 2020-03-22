package com.xujun.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeploymentInformation {
    private String name;
    private String createTime;
    private String status;
    private Integer readyReplicas;
    private Integer replicas;
    private Long runTime;
}
