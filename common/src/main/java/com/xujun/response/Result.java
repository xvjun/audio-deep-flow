package com.xujun.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private ResultCode resultCode;
    private Object data;

    public static Result success() {
        return Result.success(null);
    }

    public static Result success(Object data) {
        return Result.result(ResultCode.SUCCESS, data);
    }

    public static Result failure() {
        return Result.failure(null);
    }

    public static Result failure(Object data) {
        return Result.result(ResultCode.FAILURE, data);
    }

    public static Result result(ResultCode resultCode) {
        return Result.result(resultCode, null);
    }

    public static Result result(ResultCode resultCode, Object data) {
        return new Result(resultCode, data);
    }

}
