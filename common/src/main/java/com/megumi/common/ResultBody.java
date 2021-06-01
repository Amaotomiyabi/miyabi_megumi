package com.megumi.common;

/**
 * 2021/2/21
 * 统一的数据返回格式
 *
 * @author miyabi
 * @since 1.0
 */
public class ResultBody<T> {
    /**
     * 状态码
     */
    private String code = "200";

    /**
     * 提示信息
     */
    private String message = "操作成功";

    /**
     * 实际返回数据
     */
    private T data;

    public ResultBody(T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ResultBody() {
    }

    public ResultBody(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResultBody{" +
               "code='" + code + '\'' +
               ", message='" + message + '\'' +
               ", data=" + data +
               '}';
    }
}
