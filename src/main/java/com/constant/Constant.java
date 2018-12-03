package com.constant;

import java.io.File;

/**
 * Created by likaisong on 2018/11/29.
 */
public class Constant {
    public static String BASE_HOST = "http://api-phx.meituan.com";
    public static String BASEDIR = System.getProperty("user.dir");
    public static String CONFIG_Path = BASEDIR + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "config.properties";
    public static final String READ_TIMEOUT = "READ_TIMEOUT";
    public static final String CONNECT_TIMEOUT = "CONNECT_TIMEOUT";
    public static final String WRITE_TIMEOUT = "WRITE_TIMEOUT";
    public static final String HOST = "HOST";
    public static final String PROXY = "PROXY";


    public static void main(String[] args){
        System.out.println(CONFIG_Path);
    }

}
