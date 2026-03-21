package com.yx198.helloserver3.common;

public class Result<T> {
    private Integer code;
    private String msg;
    private T data;

    // 无参构造
    public Result() {
    }

    // 全参构造
    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    // 静态工厂方法：成功回调
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.code = ResultCode.SUCCESS.getCode();
        result.msg = ResultCode.SUCCESS.getMsg();
        result.data = data;
        return result;
    }

    // 静态工厂方法：成功回调（自定义消息）
    public static <T> Result<T> success(String msg, T data) {
        Result<T> result = new Result<>();
        result.code = ResultCode.SUCCESS.getCode();
        result.msg = msg;
        result.data = data;
        return result;
    }

    // 静态工厂方法：失败回调
    public static <T> Result<T> error(ResultCode resultCode) {
        Result<T> result = new Result<>();
        result.code = resultCode.getCode();
        result.msg = resultCode.getMsg();
        result.data = null;
        return result;
    }

    // 静态工厂方法：失败回调（自定义消息）
    public static <T> Result<T> error(ResultCode resultCode, String msg) {
        Result<T> result = new Result<>();
        result.code = resultCode.getCode();
        result.msg = msg;
        result.data = null;
        return result;
    }

    // Getter 和 Setter
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}