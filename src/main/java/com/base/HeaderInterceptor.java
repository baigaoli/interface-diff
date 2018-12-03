package com.base;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by likaisong on 2018/11/30.
 */
public class HeaderInterceptor implements Interceptor {
    private Headers headers;
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        builder.header("Connection", "close");
        if (this.headers != null) {
            Iterator iterator = this.headers.names().iterator();

            while(iterator.hasNext()) {
                String name = (String)iterator.next();
                builder.removeHeader(name).addHeader(name, this.headers.get(name));
            }
        }

        Response response = chain.proceed(builder.build());
        return response;
    }
}
