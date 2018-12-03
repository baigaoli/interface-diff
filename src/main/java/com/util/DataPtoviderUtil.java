package com.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by likaisong on 2018/11/29.
 */
public class DataPtoviderUtil {
    public static Logger logger = LogManager.getLogger(DataPtoviderUtil.class);
    /**
     * 文件转字符串
     * @param fileName
     */
    public static String readToString(String fileName){
        String encoding = "utf-8";
        File file = new File(fileName);
        long fileLength = file.length();
        byte[] content = new byte[(int) fileLength];
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            in.read(content);
        } catch (FileNotFoundException e) {
            logger.error("file read faild "+ e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
        try {
            return new String(content, encoding);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    /**
     * 读测试数据
     * @param fileName
     * @return
     */
    public static List<Map<String, String>> readDataFile(String fileName) {
        BufferedReader reader = FileUtil.getBufferedReader(fileName);
        List<Map<String, String>> dataList = new ArrayList<>();
        String line;
        try {
            while((line = reader.readLine())!=null){
                String[] lineArr = line.split("\t");
                Map<String, String> dataMap = new HashMap<>();
                dataMap.put("method",lineArr[0]);
                dataMap.put("path",lineArr[1]);
                dataMap.put("body",lineArr[2]);
                dataMap.put("header",lineArr[3]);
                dataList.add(dataMap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return dataList;
    }




    public static void main(String[] args){
        System.out.println(DataPtoviderUtil.readDataFile("/Users/likaisong/IdeaProjects/interface-diff/src/test/java/httpDiff/data/InterfaceTest.log").toString());
    }
}
