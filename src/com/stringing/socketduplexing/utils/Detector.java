package com.stringing.socketduplexing.utils;

import com.stringing.socketduplexing.message.constants.MessageParam;
import com.stringing.socketduplexing.server.impl.Server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Description 用户网络状态检测
 * @Author Stringing
 * @Date 2018/8/22 14:33
 */
public class Detector<T> {
    private static Detector ourInstance;
    private static ScheduledThreadPoolExecutor detectScheduler;
    private static ExecutorService detectExecutor;

    public static Detector getInstance(){
        if(ourInstance == null){
            synchronized (Detector.class){
                if(ourInstance == null){
                    ourInstance = new Detector();
                    detectScheduler = new ScheduledThreadPoolExecutor(10);
                    detectExecutor = Executors.newCachedThreadPool();
                }
            }
        }
        return ourInstance;
    }

    private Detector(){}

    /**
     * 发送检测数据的心跳方法
     * @param client 客户端
     * @throws IOException
     */
    private void hearBeat(Socket client)throws IOException{
        new DataOutputStream(client.getOutputStream()).writeUTF(MessageParam.FilterToken.DETECT_TOKEN.toString());
    }

    /**
     * 判断用户是否在线
     * @param client 客户端
     * @return true如果用户在线，否则false
     */
    private boolean isOnLine(Socket client){
        try{
            hearBeat(client);
        }catch (IOException e){
            return false;
        }
        return true;
    }

    /**
     * 周期性检测全体用户网络状态
     * @param clientMap 客户端map集
     * @param period 心跳周期
     */
    public void detect(Map<T,Socket> clientMap, long period){
        detectScheduler.scheduleAtFixedRate(()->{
            synchronized (Server.class) {
                clientMap.forEach((token, client) -> {
                    detectExecutor.submit(() -> {
                        if (!isOnLine(client)) {
                            System.out.println(token + " offline.");
                            clientMap.remove(token, client);
                            Logger.log("client " + token + " offline.");
                        }
                    });
                });
            }
        }, 0, period, TimeUnit.MILLISECONDS);
    }


}
