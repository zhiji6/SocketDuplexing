package utils;

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


    private static String currentTime(){
        return " " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}
