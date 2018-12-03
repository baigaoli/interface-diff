package com.util;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.base.HttpDiffMode;
import com.base.ResponseMap;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.Predicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareResult;

import java.io.IOException;

/**
 * Created by likaisong on 2018/11/30.
 */
public class DiffAssert {
    public static Logger logger = LogManager.getLogger(DiffAssert.class);

    public static void assertResponseEquals(ResponseMap[] responseMaps, HttpDiffMode httpDiffMode) throws JSONException, IOException {
        Configuration configuration = Configuration.builder().build();

        if (responseMaps[0].getStatusCode() != responseMaps[1].getStatusCode()){
            DiffLog.writeErrorLog(httpDiffMode.getTestName(), responseMaps, httpDiffMode, "Expected HTTP Code " + responseMaps[0].getStatusCode() + " but get " + responseMaps[1].getStatusCode());
            throw new AssertionError("Expected HTTP Code " + responseMaps[0].getStatusCode() + " but get " + responseMaps[1].getStatusCode());
        }
        Object srcJson = str2JsonObject(responseMaps[0].getResponseBody());
        Object desJson = str2JsonObject(responseMaps[1].getResponseBody());
        if (srcJson instanceof String || desJson instanceof String){
            if (srcJson instanceof String && desJson instanceof String && ((String) srcJson).equalsIgnoreCase((String) desJson)){
                return;
            }
            DiffLog.writeErrorLog(httpDiffMode.getTestName(), responseMaps, httpDiffMode, "Response body not equals");
            throw new AssertionError("Response body not equals");
        }

        if (httpDiffMode.getExcludePaths() != null && srcJson != null && desJson != null){
            for (int i = 0; i < httpDiffMode.getExcludePaths().length; i++){
                JsonPath jsonPath = JsonPath.compile(httpDiffMode.getExcludePaths()[i], new Predicate[0]);
                try {
                    srcJson = jsonPath.delete(srcJson, configuration);
                    desJson = jsonPath.delete(desJson, configuration);
                } catch (PathNotFoundException e) {
                    logger.error(httpDiffMode.getExcludePaths()[i] + " not found");
                    continue;
                }
            }
        }
        JSONCompareResult result = JSONCompare.compareJSON(JSONObject.toJSONString(srcJson, SerializerFeature.WriteMapNullValue),
                JSONObject.toJSONString(desJson, SerializerFeature.WriteMapNullValue), httpDiffMode.getCompareMode());
        if (result.failed()){
            JSONObject assertMsg= new JSONObject();
            assertMsg.put("srcResponse", srcJson);
            assertMsg.put("desResponse", desJson);
            String resultStr = getDiffResult(result);
            DiffLog.writeErrorLog(httpDiffMode.getTestName(), responseMaps, httpDiffMode, resultStr);
            throw new AssertionError(assertMsg.toJSONString() + "\n" + resultStr);
        }
    }

    /**
     * 获取对比结果
     * @param result
     * @return
     */
    private static String getDiffResult(JSONCompareResult result) {
        StringBuilder sb = new StringBuilder("\n");
        String[] errFiled = result.toString().split(";");
        for (int i = 0; i < errFiled.length; i ++){
            sb.append("\tDiff" + (i + 1) + ": " + errFiled[i] + "\n");
        }
        return sb.toString();
    }

    /**
     * string转JsonObject
     * @param jsonStr
     * @return
     */
    private static Object str2JsonObject(String jsonStr) {
        Object object = null;
        if (jsonStr != null){
            object = JSONObject.parse(jsonStr);
        }
        return object;
    }

}
