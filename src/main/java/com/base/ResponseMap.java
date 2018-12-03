package com.base;

import com.google.gson.Gson;
import okhttp3.Headers;

/**
 * Created by likaisong on 2018/11/29.
 */
public class ResponseMap {
    private Headers headers;
    private int statusCode;
    private String url;
    private String responseBody;

    public ResponseMap(Headers headers, int statusCode, String url, String responseBody) {
        this.headers = headers;
        this.statusCode = statusCode;
        this.url = url;
        this.responseBody = responseBody;
    }

    public void setHeaders(Headers headers) {
        this.headers = headers;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public Headers getHeaders() {
        return headers;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getUrl() {
        return url;
    }

    public String getResponseBody() {
        return responseBody;
    }

    @Override
    public String toString() {
        return (new Gson()).toJson(this);
    }
}
