package com.util;

import com.constant.Constant;
import com.constant.HttpMethod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.*;
import java.util.*;

/**
 * Created by likaisong on 2018/11/28.
 */
public class DefineConfigUtil {
    public static Logger logger = LogManager.getLogger(DefineConfigUtil.class);
    public static Map<String, String> configMap = getProperties(Constant.CONFIG_Path);
    private static String logLevel = "ERROR";
    /**
     * 获取配置文件属性值
     * @param testConfigFileNane
     * @return
     */
    public static Map<String,String> getProperties(String testConfigFileNane) {
        Map<String, String> valueList = new HashMap<>();
        File file = new File(testConfigFileNane);
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
            return valueList;
        }
        Properties properties = new Properties();
        try {
            properties.load(in);
        } catch (IOException e) {
            logger.error(e.getMessage());
            return valueList;
        }
        Set<String> keySet = properties.stringPropertyNames();
        Iterator iterator = keySet.iterator();
        while (iterator.hasNext()){
            String key = (String) iterator.next();
            if (properties.getProperty(key) != null && properties.getProperty(key).length() > 0){
                valueList.put(key.toUpperCase(), properties.getProperty(key));
            }
        }
        return valueList;
    }

    /**
     * 删除字符串左右两边指定的字符串
     * @param src
     * @param subStr
     * @return
     */
    public static String trim(String src, String subStr){
        String tmpStr = null;
        if (src == null){
            return null;
        }
        if (subStr == null || subStr.length() == 0){
            return src;
        }
        tmpStr = src.trim();
        int subLen = subStr.length();
        int srcLen = src.length();
        if (subLen > srcLen){
            return tmpStr;
        }
        if (src.startsWith(subStr)){
            tmpStr = tmpStr.substring(subLen);
        }
        if (src.endsWith(subStr)){
            tmpStr = tmpStr.substring(0, srcLen - subLen);
        }
        return tmpStr;
    }

    /**
     * 获取接口请求类型
     * @param method
     * @return
     */
    public static HttpMethod getHTTPMethod(String method) {
        if (method != null){
            method = method.trim().toUpperCase();
        }else {
            method = String.valueOf(HttpMethod.GET);
        }
        return HttpMethod.valueOf(method);
    }

    /**
     * 获取json比较模式
     * @param compareMode
     * @return
     */
    public static JSONCompareMode getCompareMode(String compareMode) {
        if (compareMode != null){
            compareMode = compareMode.trim().toUpperCase();
        }else {
            compareMode = String.valueOf(JSONCompareMode.STRICT);
        }
        return JSONCompareMode.valueOf(compareMode);
    }

    /**
     * 获取日志级别，默认返回error
     * @return
     */
    public static String getLogLevel() {
        if (configMap.get("LOG") != null) {
            logLevel = (configMap.get("LOG")).toUpperCase();
        }

        return logLevel;
    }

    /**
     * 获取Host
     * @return
     */
    public static String getHost(){
        if (configMap.get("HOST") != null) {
            Constant.BASE_HOST = (configMap.get("HOST"));
        }

        return Constant.BASE_HOST;
    }

    public static String getValue(String key){
        return configMap.get(key);
    }

}
