package com.xujun.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataInformation {
    private Integer dataId;
    private String dataName;
    private String hdfsPath;
    private Long length;
    private Long capacity;
    private String importTime;
    private Integer isCompleted;
}
