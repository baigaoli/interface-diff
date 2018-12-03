package com.util;

import com.base.HttpDiffMode;
import com.base.ResponseMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by likaisong on 2018/11/30.
 */
public class DiffLog {
    private static final String DEFAULT_PATH = "/src/test/logs/difflog/".replace("/"
            , File.separator);
    private static final String DIFF_LOG_PATH = (System.getProperty("DIFF_LOG_PATH") != null)
            ? System.getProperty("DIFF_LOG_PATH") : DEFAULT_PATH;
    private static final String DIFF_LOG_DIR = System.getProperty("user.dir") + DIFF_LOG_PATH;
    private static Map<String, BufferedWriter> bufferedWriterMap = new HashMap<>();
    private static Map<String, BufferedWriter> databufferedWriterMap = new HashMap<>();
    private static Logger logger = LogManager.getLogger(DiffLog.class);

    static {
        FileUtil.createDirectory(DIFF_LOG_DIR);
    }

    public static void appendLine(BufferedWriter bw, String key, Object value) throws IOException {

        // 如果是异常时，打印栈信息,否则打印字符串
        if (value instanceof Exception) {
            Writer errString = new StringWriter();
            PrintWriter printWriter = new PrintWriter(errString);

            ((Exception) value).printStackTrace(printWriter);
            value = errString.toString();
        }

        bw.append("【" + key + "】: " + value + "\n");
        bw.flush();
    }

    public static String printSplitLine() {
        StringBuilder headerLog = new StringBuilder();

        for (int i = 0; i < 100; i++) {
            headerLog.append("-");
        }

        headerLog.append("\n");

        String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());

        headerLog.append("【Time】: " + dateString + "\n");

        return headerLog.toString();
    }

    /**
     * 结果对比失败时写日志
     * @param className
     * @param responseMaps
     * @param diffMode
     * @param result
     * @throws IOException
     */
    public synchronized static void writeErrorLog(String className, ResponseMap[] responseMaps, HttpDiffMode diffMode, String result) throws IOException {
        // 写失败的数据到指定文件中
        BufferedWriter dataWriter = getDataWriter(className);
        dataWriter.append(diffMode.toString() + ",\n");
        dataWriter.flush();

        // 写失败的日志
        BufferedWriter bufferedWriter = getLogWriter(className);
        bufferedWriter.append(printSplitLine());
        appendLine(bufferedWriter, "Data Index", String.valueOf(diffMode.getIndex()));

        if (diffMode.getBody() != null) {
            appendLine(bufferedWriter, "POST Body", diffMode.getBody().toJSONString());
        }

        appendLine(bufferedWriter, "Src " + diffMode.getMethod(), diffMode.getSrcUrl());
        appendLine(bufferedWriter, "Src Response", responseMaps[0].getResponseBody());
        appendLine(bufferedWriter, "Des " + diffMode.getMethod(), diffMode.getDesUrl());
        appendLine(bufferedWriter, "Des Response", responseMaps[1].getResponseBody());
        appendLine(bufferedWriter, "Diff Mode", diffMode.getCompareMode().toString());
        appendLine(bufferedWriter, "Diff Result", result);
        bufferedWriter.flush();
    }
    /**
     * 获取写失败数据文件的文件对象
     *
     * @param className
     * @return
     */
    public static BufferedWriter getDataWriter(String className) throws IOException {
        String fileName = DIFF_LOG_DIR + className.substring(className.lastIndexOf(".") + 1) + "_FailData_" + new SimpleDateFormat("yyMMddHHmmss").format(new Date()) + ".json";
        return getWriter(databufferedWriterMap, fileName, className);
    }

    /**
     * 获取日志的写文件对象
     *
     * @param className
     * @return
     */
    public static BufferedWriter getLogWriter(String className) throws IOException {
        String fileName = DIFF_LOG_DIR + className.substring(className.lastIndexOf(".") + 1)
                + "_" + new SimpleDateFormat("yyMMddHHmmss").format(new Date()) + ".log";
        return getWriter(bufferedWriterMap, fileName, className);
    }

    /**
     * 获取失败用例的日志文件对象
     *
     * @param bufferedWriterMap
     * @param fileName
     * @return
     */
    public synchronized static BufferedWriter getWriter(Map<String, BufferedWriter> bufferedWriterMap, String fileName, String className) throws IOException {
        BufferedWriter bufferedWriter = bufferedWriterMap.get(className);
        if (bufferedWriter == null) {
            File file = new File(fileName);
            FileWriter fw = null;
            try {
                if (file.exists()) {
                    file.delete();
                }
                file.createNewFile();
                fw = new FileWriter(file, true);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
            bufferedWriter = new BufferedWriter(fw);
            bufferedWriter.append("[");
            bufferedWriter.flush();
            bufferedWriterMap.put(className, bufferedWriter);
        }
        return bufferedWriter;
    }

    public static void closeErrorDataLog() {
        databufferedWriterMap.forEach((k, v) -> {
            try {
                v.append("]");
                v.flush();
                v.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
