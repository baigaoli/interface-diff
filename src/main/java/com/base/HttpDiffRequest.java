package com.base;

import com.alibaba.fastjson.JSONObject;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.io.IOException;

/**
 * Created by likaisong on 2018/11/29.
 */
public class HttpDiffRequest extends HttpRequestService{
    public ResponseMap[] sendDiffRequest(HttpDiffMode httpDiffMode) throws IOException {
        switch (httpDiffMode.getMethod()){
            case GET:
                return getDiffRequest(httpDiffMode);
            case POST:
                return postDiffRequest(httpDiffMode);
            default:
                throw new AssertionError("Unsupported method: " + httpDiffMode.getMethod());
        }
    }

    /**
     * post请求
     * @param httpDiffMode
     * @return
     */
    private ResponseMap[] postDiffRequest(HttpDiffMode httpDiffMode) throws IOException {
        DiffApiService service = retrofit().create(DiffApiService.class);
        Call<ResponseBody> srcCall;
        Call<ResponseBody> desCall;
        if (httpDiffMode.isForm()){
            if (httpDiffMode.getHeader() != null){
                srcCall = service.postForm(httpDiffMode.getSrcUrl(), httpDiffMode.getBody(), httpDiffMode.getHeader());
                desCall = service.postForm(httpDiffMode.getDesUrl(), httpDiffMode.getBody(), httpDiffMode.getHeader());
            }else {
                srcCall = service.postForm(httpDiffMode.getSrcUrl(), httpDiffMode.getBody());
                desCall = service.postForm(httpDiffMode.getDesUrl(), httpDiffMode.getBody());
            }
        } else {
            if (httpDiffMode.getHeader() != null){
                srcCall = service.post(httpDiffMode.getSrcUrl(), httpDiffMode.getBody(), httpDiffMode.getHeader());
                desCall = service.post(httpDiffMode.getDesUrl(), httpDiffMode.getBody(), httpDiffMode.getHeader());
            }else {
                srcCall = service.post(httpDiffMode.getSrcUrl(), httpDiffMode.getBody());
                desCall = service.post(httpDiffMode.getDesUrl(), httpDiffMode.getBody());
            }
        }
        ResponseMap[] results = new ResponseMap[2];
        results[0] = sendRequest(srcCall);
        results[1] = sendRequest(desCall);
        return results;
    }

    /**
     * get请求
     * @param httpDiffMode
     * @return
     */
    private ResponseMap[] getDiffRequest(HttpDiffMode httpDiffMode) throws IOException {
        DiffApiService service = retrofit().create(DiffApiService.class);
        Call<ResponseBody> srcCall = null;
        Call<ResponseBody> desCall = null;
        if (httpDiffMode.getHeader() != null){
            srcCall = service.get(httpDiffMode.getSrcUrl(), httpDiffMode.getHeader());
            desCall = service.get(httpDiffMode.getDesUrl(), httpDiffMode.getHeader());
        }else {
            srcCall = service.get(httpDiffMode.getSrcUrl());
            desCall = service.get(httpDiffMode.getDesUrl());
        }
        ResponseMap[] results = new ResponseMap[2];
        results[0] = sendRequest(srcCall);
        results[1] = sendRequest(desCall);
        return results;
    }

    public interface DiffApiService{
        @GET
        Call<ResponseBody> get(@Url String url);

        @GET
        Call<ResponseBody> get(@Url String url, @HeaderMap JSONObject jsonOHeader);

        @POST
        Call<ResponseBody> post(@Url String url, @Body JSONObject jsonBpdy);

        @POST
        Call<ResponseBody> post(@Url String url, @Body JSONObject jsonBpdy, @HeaderMap JSONObject jsonOHeaders);

        @FormUrlEncoded
        @POST
        Call<ResponseBody> postForm(@Url String url, @FieldMap JSONObject jsonBody, @HeaderMap JSONObject jsonHeaders);

        @FormUrlEncoded
        @POST
        Call<ResponseBody> postForm(@Url String url, @FieldMap JSONObject jsonBody);

    }
}
