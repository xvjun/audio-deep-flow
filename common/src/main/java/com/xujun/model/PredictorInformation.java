package com.xujun.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PredictorInformation {

    private String id;
    private Integer location;
    private String time;
    private Integer label;

}
