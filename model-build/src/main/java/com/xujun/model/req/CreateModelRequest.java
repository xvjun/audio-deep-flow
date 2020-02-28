package com.xujun.model.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateModelRequest {
    private List<String> dataHdfsPathList;
    private int hiddenLayers;
    private int layersSize;
    private float learningRate;
    private int epochs;
    private float dropoutRate;
    private int classSum;
    private float cpu;
    private int memory;

}
