package com.base;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.util.DefineConfigUtil;
import com.constant.HttpMethod;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by likaisong on 2018/11/29.
 */
public class HttpDiffMode {
    private String srcUrl;
    private String desUrl;
    private String path;
    private JSONObject header;
    private String query;
    private JSONObject body;
    private JSONCompareMode compareMode;
    private String[] excludePaths;
    private HttpMethod method;
    private int index;
    private String testName;
    private boolean isForm = false;

    /**
     * HttpDiffMode初始化
     * @param srcHost 目标url
     * @param desHost 测试url
     * @param path 请求uri
     * @param header 请求头
     * @param query 请求参数
     * @param body 请求body
     * @param compareMode json比较模式
     * @param excludePaths 不对比的json字段
     * @param method 请求方法类型
     * @param index 第几个请求
     * @param testName 测试方法名
     * @param isForm 是否表单提交
     */
    public HttpDiffMode(String srcHost, String desHost, String path, JSONObject header, String method, String query, String body, String compareMode, String[] excludePaths, int index, String testName, boolean isForm) {
        this.srcUrl = DefineConfigUtil.trim(srcHost, "/");
        this.desUrl = DefineConfigUtil.trim(desHost, "/");
        this.path = DefineConfigUtil.trim(path, "/");
        this.header = header;
        this.method = DefineConfigUtil.getHTTPMethod(method);
        this.query = DefineConfigUtil.trim(DefineConfigUtil.trim(query, "/"), "?");
        if (this.method == HttpMethod.POST){
            this.body = getJsonBody(body);
        }
        this.compareMode = DefineConfigUtil.getCompareMode(compareMode);
        this.excludePaths = excludePaths;
        buildUrl();
        this.index = index;
        this.testName = testName;
        this.isForm = isForm;
    }

    /**
     * 拼接url
     */
    private void buildUrl() {
        String uri = "";
        if (this.query != null && this.query.split("\\?").length > 1){
            this.path = this.query.split("\\?")[0];
            this.query = this.query.split("\\?")[1];
        }
        if (this.path != null && this.path.length() > 0){
            uri += "/" + this.path;
        }
        if (this.query != null && this.query.length() > 0){
            uri += "?" + this.query;
        }
        this.srcUrl += uri;
        this.desUrl += uri;
        URL url = null;
        try {
            url = new URL(this.desUrl);
        } catch (MalformedURLException e) {
            throw  new RuntimeException("url build faild " + e.getMessage());
        }
        this.path = url.getPath();
        this.query = url.getQuery();
    }

    /**
     * 获取请求body
     * @param body
     * @return
     */
    private JSONObject getJsonBody(String body) {
        JSONObject jsonBody = null;
        try {
            jsonBody = JSONObject.parseObject(body);
        }catch (Exception e){
            if (isForm){
                jsonBody = new JSONObject();
                String[] items = body.split("&");
                for (String item : items){
                    String[] kv = item.split("=");
                    jsonBody.put(kv[0], kv[1]);
                }
            }
        }
        return jsonBody;
    }

    public String getSrcUrl() {
        return srcUrl;
    }

    public String getDesUrl() {
        return desUrl;
    }

    public String getPath() {
        return path;
    }

    public JSONObject getHeader() {
        return header;
    }

    public String getQuery() {
        return query;
    }

    public JSONObject getBody() {
        return body;
    }

    public JSONCompareMode getCompareMode() {
        return compareMode;
    }

    public String[] getExcludePaths() {
        return excludePaths;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public int getIndex() {
        return index;
    }

    public String getTestName() {
        return testName;
    }

    public boolean isForm() {
        return isForm;
    }

    public void setSrcUrl(String srcUrl) {
        this.srcUrl = srcUrl;
    }

    public void setDesUrl(String desUrl) {
        this.desUrl = desUrl;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setHeader(JSONObject header) {
        this.header = header;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setBody(JSONObject body) {
        this.body = body;
    }

    public void setCompareMode(JSONCompareMode compareMode) {
        this.compareMode = compareMode;
    }

    public void setExcludePaths(String[] excludePaths) {
        this.excludePaths = excludePaths;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public void setForm(boolean form) {
        isForm = form;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
