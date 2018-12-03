package com.base;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.util.DataPtoviderUtil;
import com.util.DefineConfigUtil;
import com.util.DiffLog;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.DataProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by likaisong on 2018/11/28.
 */
public class DiffTestBase {
    public static String BASEDIR = System.getProperty("user.dir");
    public static String testPath = File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator;

    @DataProvider(name = "HttpDiffData", parallel = true)
    public Iterator<Object[]> httpDiffJsonData(){
        String className = this.getClass().getName();
        String jsonDataFileName = className.replace(".", File.separator).replace("testsuites", "data") + ".log";
        String testConfigFileNane = BASEDIR + testPath + className.replace(".", File.separator).replace("testsuites", "data") + ".properties";
        return getHttpData(BASEDIR + testPath + jsonDataFileName, testConfigFileNane);
    }

    /**
     * 获取测试数据
     * @param dataFileName
     * @param testConfigFileNane
     * @return
     */
    private Iterator<Object[]> getHttpData(String dataFileName, String testConfigFileNane) {
        //获取用例配置值
        Map<String, String> configMap = DefineConfigUtil.getProperties(testConfigFileNane);
        String srcHost = configMap.get("srcHost".toUpperCase());
        String desHost = configMap.get("desHost".toUpperCase());
        String excludePath = configMap.get("excludePaths".toUpperCase());
        String[] excludePaths = excludePath == null ? null : excludePath.split(";");
        String path = configMap.get("path".toUpperCase());
        String query = configMap.get("query".toUpperCase());
        String headers = configMap.get("headers".toUpperCase());
        String compareMode = configMap.get("compareMode".toUpperCase());
        String method = configMap.get("method".toUpperCase());
        boolean isForm = Boolean.valueOf(configMap.get("isForm".toUpperCase()));

        List<HttpDiffMode> diffModes = new ArrayList<>();

        List<Map<String, String>> dataList = DataPtoviderUtil.readDataFile(dataFileName);
        for (int i = 0; i < dataList.size(); i ++){
            String tmpmethod = dataList.get(i).get("method");
            method = tmpmethod != null && tmpmethod.trim().length() > 0 ? tmpmethod.trim() : method;

            String tmpPath = dataList.get(i).get("path");
            path = tmpPath != null && tmpPath.trim().length() > 0 ? tmpPath.trim() : path;

            String body = dataList.get(i).get("body");

            JSONObject header = JSONObject.parseObject(dataList.get(i).get("header"));
            if (header.containsKey("Host")){
                header.remove("Host");
            }
            if (header == null && headers != null){
                String[] headerArr = headers.split(",");
                for (String tmpHeader : headerArr){
                    String[] kv = tmpHeader.split(":");
                    header.put(kv[0], kv[1]);
                }
            }
            HttpDiffMode httpDiffMode = new HttpDiffMode(srcHost, desHost, path, header, method, query, body, compareMode, excludePaths, i + 1, getClass().getName(), isForm);
            diffModes.add(httpDiffMode);
        }
        //转换
        List<Object[]> httpDiffModes = new ArrayList<>();
        for (Object object: diffModes){
            httpDiffModes.add(new Object[]{object});
        }
        return httpDiffModes.iterator();
    }

    @AfterSuite
    public void closeErrorLog() {
        DiffLog.closeErrorDataLog();
    }
}
