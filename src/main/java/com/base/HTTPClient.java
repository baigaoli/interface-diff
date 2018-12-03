package com.base;


import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by likaisong on 2018/11/29.
 */
public class HTTPClient {
    public Logger logger = LogManager.getLogger(this.getClass());
    protected List<Interceptor> interceptorList = new ArrayList();
    private Headers headers;

    public HTTPClient(){
        addInterceptor(new HeaderInterceptor());
    }

    public ResponseMap sendRequest(Call<ResponseBody> call) throws IOException {
        Response response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return this.sendRequestAfter(response);
    }

    public ResponseMap sendRequestAfter(Response<ResponseBody> response) throws IOException{
        String url = response.raw().request().url().toString();
        ResponseBody responseBody ;
        if (response.isSuccessful()){
            responseBody = response.body();
        }else {
            responseBody = response.errorBody();
        }
        return new ResponseMap(response.headers(), response.code(), url, responseBody.string());
    }

    public void addInterceptor(Interceptor interceptor) {
        interceptorList.add(interceptor);
    }

    public List<Interceptor> getInterceptorList() {
        return this.interceptorList;
    }

}
