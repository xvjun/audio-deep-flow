package com.xujun.model.req;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateServingRequest {
    private String modelHdfsPath;
    private Float cpu;
    private Integer memory;
    private Integer instance;

}
