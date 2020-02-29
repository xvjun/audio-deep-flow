package com.xujun.config;

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
}
