package com.stringing.socketduplexing.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private static final String DEFAULT_LOG_PATH = "./";
    private static final String LOG_NAME = "server.log";

    private static PrintWriter pw;

    private Logger(){}

    /**
     * 日志记录方法
     * @param detail 要记录的一条内容
     */
    public synchronized static void log(String detail){
        try {
            pw = new PrintWriter(new FileOutputStream(DEFAULT_LOG_PATH + LOG_NAME, true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        pw.write(currentTime() + ": " + detail + "\r\n");
        pw.flush();
        pw.close();
    }


    /**
     * 每条日志内容都默认会加上当前系统时间
     * @return 当前时间，格式年月日时分秒
     */
    private static String currentTime(){
        return " " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}
