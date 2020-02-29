package com.xujun.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServingInformation {
    private Integer servingId;
    private String servingName;
    private Float cpu;
    private Integer memory;
    private Integer instance;
    private String modelLocalPath;
    private Integer isCompleted;
    private String startTime;

}
