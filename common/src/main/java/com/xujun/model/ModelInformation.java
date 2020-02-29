package com.xujun.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModelInformation {

    private Integer modelId;
    private String modelName;
    private String hdfsPath;
    private Long tock;
    private String lossStr;
    private String accuracyStr;
    private String valLossStr;
    private String valAccuracyStr;
    private Float accuracy;
    private Float loss;
    private Float valAccuracy;
    private Float valLoss;
    private String completeTime;
    private Integer hiddenLayers;
    private Integer layersSize;
    private Float learningRate;
    private Integer epochs;
    private Float dropoutRate;
    private Integer classSum;

}