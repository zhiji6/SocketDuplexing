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
 * @Description
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

    private void hearBeat(Socket client)throws IOException{
        new DataOutputStream(client.getOutputStream()).writeUTF(MessageParam.FilterToken.DETECT_TOKEN.toString());
    }

    private boolean isOnLine(Socket client){
        try{
            hearBeat(client);
        }catch (IOException e){
            return false;
        }
        return true;
    }

    public void detect(Map<T,Socket> clientMap){
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
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }


}
