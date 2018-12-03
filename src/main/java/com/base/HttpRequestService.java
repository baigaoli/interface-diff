package com.base;

import com.constant.Constant;
import com.util.DefineConfigUtil;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by likaisong on 2018/11/29.
 */
public class HttpRequestService {
    public Logger logger = LogManager.getLogger(this.getClass());
    protected static String BASETESTURL = null;
    protected static Retrofit retrofit = null;
    protected static Map<String, HTTPClient> clientMap = new HashMap<String, HTTPClient>();

    protected synchronized Retrofit retrofit(){
        if (BASETESTURL == null){
            BASETESTURL = DefineConfigUtil.getHost();
        }
        if (retrofit == null || this.getClient() == null){
            retrofit = (new Retrofit.Builder()).baseUrl(BASETESTURL)
                    .client(this.createOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    /**
     * 带baseUrl
     * @param host
     * @return
     */
    public Retrofit retrofit(String host) {
        return (new Retrofit.Builder()).baseUrl(host).client(this.createOkHttpClient()).addConverterFactory(GsonConverterFactory.create()).build();
    }

    private synchronized OkHttpClient createOkHttpClient() {
        synchronized(clientMap) {
            this.registClient(this.initClient());
        }

        okhttp3.OkHttpClient.Builder builder = new okhttp3.OkHttpClient.Builder();
        setProxy(builder);
        Iterator iterator = this.getClient().getInterceptorList().iterator();

        while(iterator.hasNext()) {
            Interceptor interceptor = (Interceptor)iterator.next();
            builder.addInterceptor(interceptor);
        }

        HttpLoggingInterceptor httpLoggingInterceptor = sethttpLoggingInterceptor();

        OkHttpClient okHttpClient = builder.addInterceptor(httpLoggingInterceptor)
                .readTimeout(Long.valueOf(DefineConfigUtil.getValue(Constant.READ_TIMEOUT)), TimeUnit.SECONDS)
                .connectTimeout(Long.valueOf(DefineConfigUtil.getValue(Constant.CONNECT_TIMEOUT)), TimeUnit.SECONDS)
                .writeTimeout(Long.valueOf(DefineConfigUtil.getValue(Constant.WRITE_TIMEOUT)), TimeUnit.SECONDS)
                .sslSocketFactory(SSLSocktTruestClient.getSSLSocketFactory())
                .hostnameVerifier(SSLSocktTruestClient.getHostnameVerifier())
                .build();
        return okHttpClient;
    }

    /**
     * 设置http日志级别
     * @return
     */
    private HttpLoggingInterceptor sethttpLoggingInterceptor() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        String logLevel = DefineConfigUtil.getLogLevel();
        if (logLevel.toUpperCase().equals("DEBUG")){
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        }else {
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }
        return httpLoggingInterceptor;
    }

    private void registClient(HTTPClient httpClient) {
        clientMap.put(this.getClass().getName(), httpClient);
    }

    /**
     * 设置代理
     * @param builder
     */
    private void setProxy(OkHttpClient.Builder builder) {
        String proxyAddress = DefineConfigUtil.getValue(Constant.PROXY);
        if (proxyAddress != null){
            String address = proxyAddress.split(":")[0];
            int port = proxyAddress.length() > 1 ? Integer.parseInt(proxyAddress.split(":")[1]) : 8888;
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(address, port));
            builder.proxy(proxy);
        }
    }

    private HTTPClient initClient() {
        HTTPClient client = clientMap.get(this.getClass().getName());
        if (client == null) {
            client = new HTTPClient();
        }
        return client;
    }

    /**
     * 发送请求
     * @param call
     * @return
     */
    protected ResponseMap sendRequest(Call<ResponseBody> call) throws IOException {
        ResponseMap responseMap = this.getClient().sendRequest(call);
        return responseMap;
    }

    private HTTPClient getClient() {
        return clientMap.get(this.getClass().getName());
    }

}
