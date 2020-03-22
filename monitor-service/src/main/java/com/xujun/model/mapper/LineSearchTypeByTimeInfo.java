package com.xujun.model.mapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LineSearchTypeByTimeInfo {
    private String time;
    private Integer label;
    private Integer count;
}
