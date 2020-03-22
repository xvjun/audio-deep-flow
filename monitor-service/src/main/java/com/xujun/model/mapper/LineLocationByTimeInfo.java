package com.xujun.model.mapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LineLocationByTimeInfo {
    private Integer location;
    private Integer label;
    private Integer count;
}
