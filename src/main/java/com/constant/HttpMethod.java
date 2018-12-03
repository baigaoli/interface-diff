package com.constant;

/**
 * Created by likaisong on 2018/11/29.
 */
public enum HttpMethod {
    /**
     * get方法
     */
    GET("GET"),
    /**
     * post方法
     */
    POST("POST"),
    /**
     * put方法
     */
    PUT("PUT"),
    /**
     * delete方法
     */
    DELETE("DELETE");

    private final String method;

    HttpMethod(String method) {
        this.method = method;
    }
}