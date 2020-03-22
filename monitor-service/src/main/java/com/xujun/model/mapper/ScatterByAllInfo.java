package com.xujun.model.mapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScatterByAllInfo {
    private Integer location;
    private String time;
    private Integer label;
}
