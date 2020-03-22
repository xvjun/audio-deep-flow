package com.xujun.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CommonEnvConfig {
    public final static Integer IS_COMPLETED_SUCCESS = 1;
    public final static Integer IS_COMPLETED_UPLOADING = 0;
    public final static Integer IS_COMPLETED_FAILED = -1;
    public final static Integer IS_COMPLETED_RUNNING = 2;

    public final static Integer DATA_PROCESS_HDFS_HDFS_KAFKA_MODEL = 1;
    public final static Integer BUILD_MODEL_HDFS_LOCAL_KAFKA_MODEL = 2;
    public final static Integer SERVING_APP_HDFS_LOCAL_KAFKA_MODEL = 3;

    public final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public final static String DATA_HDFS_DIR = "/data";
    public final static String MODEL_HDFS_DIR = "/model";


    public final static Set<String> set = new HashSet<String>(){{
        add("build-model");
        add("data-process");
        add("hdfs-manager");
        add("model-app");
        add("monitor");
        add("stream-data");
        add("api-gateway");
        add("front");
    }};

    public final static Map<Integer,String> labelMap = new HashMap<Integer,String>(){{
       put(0,"安全");
       put(1,"可疑");
       put(2,"危险");
       put(3,"破坏");
    }};


}
