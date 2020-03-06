package com.xujun.model.req;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Request {

    private String id;
    private Integer location;
    private Long ts;
    private List<Double> featureList;

}

// id,location,ts,[feature],label
// 1234,19850,1569439815273,[256],0

// id,location,label,ts
// 1234,29330,0,1569439815273
