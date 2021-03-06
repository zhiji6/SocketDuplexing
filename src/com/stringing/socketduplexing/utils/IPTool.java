package com.stringing.socketduplexing.utils;

import java.io.*;
import java.net.*;

public class IPTool {
    /**
     * 获取外网ip，用于非局域网模式下
     * @return 外网ip
     * @throws IOException
     */
    public static String getIp() throws IOException {
        String ip = "";
        final String IP_SOURCE = "http://120.79.88.254:8080/ipserver/IpPage";
        String line;
        URL url;
        HttpURLConnection urlConnection;
        BufferedReader in = null;
        try {
            url = new URL(IP_SOURCE);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedReader( new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));
            while((line=in.readLine())!=null){
                if(line.contains("<p>")){
                    ip = line.substring(line.indexOf("<p>") + 3, line.indexOf("</p>"));
                    break;
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return ip;
    }

    /**
     * 获取内网ip
     * @return 内网ip
     * @throws UnknownHostException
     */
    public static String getLocalIp() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }
}
