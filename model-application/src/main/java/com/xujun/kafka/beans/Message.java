package com.xujun.kafka.beans;

import lombok.Data;

import java.util.Map;


@Data
public class Message {
    Integer model;
    Map<String, String> dataMap;
    Object object;
}
