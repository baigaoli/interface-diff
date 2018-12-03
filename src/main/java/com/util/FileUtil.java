package com.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

/**
 * Created by likaisong on 2018/12/1.
 */
public final class FileUtil {

    private static Logger logger = LogManager.getLogger(FileUtil.class);

    private FileUtil() { }

    public static BufferedReader getBufferedReader(String fileName) {
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(fileName);
        } catch (FileNotFoundException var5) {
            logger.error("找不到文件：" + fileName);
        }

        BufferedReader bufferedReader = null;

        try {
            bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, "UTF-8"));
        } catch (UnsupportedEncodingException var4) {
            logger.error("文件格式不支持UTF-8：");
        }

        return bufferedReader;
    }

    public static boolean isExists(String filePath) {
        boolean isExist = false;
        File file = new File(System.getProperty("user.dir") + "/src/test/java/" + filePath);
        if (file.exists() && file.isFile()) {
            isExist = true;
        } else {
            isExist = false;
        }

        return isExist;
    }

    public static void deleteDirectory(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File[] files = file.listFiles();
                File[] var3 = files;
                int var4 = files.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    File myfile = var3[var5];
                    deleteDirectory(filePath + "/" + myfile.getName());
                }

                file.delete();
            }

        }
    }

    public static void createDirectory(String dir) {
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }

    }
}
