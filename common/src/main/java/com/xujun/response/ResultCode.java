package com.xujun.response;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ResultCode {
    SUCCESS(200, "Success"),
    WAITING_DATA_UPLOAD(201, "Waiting data upload"),

    FAILURE(300, "Failure"),
    MISSING_PARAMS(301, "Missing params"),
    MISSING_AUDIO(302, "Missing audio"),
    UPLOAD_CODE_FAILED(303, "Failed to upload code"),
    UPLOAD_HDFS_FAILED(304, "Failed to upload hdfs"),
    UPLOAD_MYSQL_FAILED(305, "Failed to upload mysql"),
    DATA_NOT_EXIST_IN_MYSQL(306, "data not exist in mysql"),
    DELETE_HDFS_FAILED(307, "Failed to delete hdfs"),
    DATA_LIST_IS_NULL(308, "dataList is null"),
    KAFKA_SEND_FAILED(309, "kafak send failed"),
    CREATE_MODEL_CONF_FAILED(310, "create model.conf failed"),
    CREATE_K8S_JOB_FAILED(311, "Failed to create k8s job"),
    CREATE_FILE_FAILED(312, "Failed to create file"),
    READ_FILE_FAILED(313, "Failed to read file"),
    ;

    private Integer code;
    private String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
