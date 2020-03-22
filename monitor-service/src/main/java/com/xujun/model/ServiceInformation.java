package com.xujun.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceInformation {
    private String name;
    private Integer nodePort;
    private Integer targetPort;
    private String protocol;
    private String type;
    private String createTime;
    private Long runTime;
}
