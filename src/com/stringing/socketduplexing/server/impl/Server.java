package com.stringing.socketduplexing.server.impl;

import com.stringing.socketduplexing.server.ConfServer;
import com.stringing.socketduplexing.server.GenericServer;
import com.stringing.socketduplexing.thread.ReadThread;
import com.stringing.socketduplexing.message.constants.MessageParam;
import com.stringing.socketduplexing.message.queue.GenericQueue;
import com.stringing.socketduplexing.utils.Detector;
import com.stringing.socketduplexing.utils.Logger;
import com.stringing.socketduplexing.utils.SocketConfiguration;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 服务端对象
 */
public class Server implements GenericServer, ConfServer {
    private static ServerSocket serverSocket;
    private static volatile Map<String, Socket> clientMap = new ConcurrentHashMap<>();
    private static ReadThread readThread;
    private GenericQueue messageQueue;
    private boolean isClient;
    private boolean isLAN;
    //用来调度用户连接线程的执行器
    private ExecutorService ioExecutor;
    //用来调度服务端消息发送线程的执行器
    private ExecutorService sendExecutor;
    //用来检测客户端网络状态
    private Detector<String> detector;
    //服务端配置
    private SocketConfiguration conf;

    public Server(GenericQueue messageQueue, SocketConfiguration conf){
        this.messageQueue = messageQueue;
        isClient = false;
        this.conf = conf;
        this.isLAN = conf.isServerLAN();
        ioExecutor = Executors.newFixedThreadPool(conf.getThreadNum());
        sendExecutor = Executors.newCachedThreadPool();
        detector = Detector.getInstance();
    }

    @Override
    public void listenWithConf() {
        startListening(conf.getServerPort());
    }

    /**
     * 持续监听某端口
     * 一有用户连接便启动读线程
     * 并将此次连接的用户存入clientMap
     * clientMap说明参考WriteThread中run方法说明
     */
    @Override
    public void startListening(int port) {
        Socket client;
        try {
            serverSocket = new ServerSocket(port);
            Logger.log("Server started ip: " + serverSocket.getInetAddress().getHostAddress() + " port: " + serverSocket.getLocalPort());
        } catch (IOException e) {
            e.printStackTrace();
            Logger.log("Server started failed ip: " + serverSocket.getInetAddress().getHostAddress() + " port: " + serverSocket.getLocalPort());
        }
        //首先开启一个广播线程
        startBroadcasting();
        //网络状态实时监测线程
        detectClientStatus();
        while(true) {
            try {
                client = serverSocket.accept();
            }catch (IOException e){
                e.printStackTrace();
                //服务端已经关闭了的话则结束监听
                ioExecutor.shutdownNow();
                sendExecutor.shutdownNow();
                break;
            }
                System.out.println("Client connecting...");
                readThread = new ReadThread(client, clientMap, null, isClient, true, this);
                ioExecutor.submit(readThread);
            }
    }

    private void detectClientStatus(){
        detector.detect(clientMap, conf.getDetectPeriod());
    }

    /**
     * 关闭服务端
     * 关闭服务端的读线程
     */
    @Override
    public void serverShutDown() {
        if(serverSocket != null) {
            try {
                Logger.log("Server closed ip: " + serverSocket.getInetAddress().getHostAddress() + " port: " + serverSocket.getLocalPort());
                serverSocket.close();
                ioExecutor.shutdownNow();
                sendExecutor.shutdownNow();
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
                Logger.log("Problem occurred while shutting down server ip: " + serverSocket.getInetAddress().getHostAddress() + " port: " + serverSocket.getLocalPort());
            }
        }
    }


    /**
     * 向用户广播消息
     * 通常用于一对多的通信
     * 一对一通信则优先选择单播
     * 用多线程保证用户同时收到广播
     */
    @Override
    public void broadcast(String content) {
        synchronized (Server.class) {
            clientMap.forEach((k, v) -> {
                sendExecutor.submit(() -> {
                    try {
                        new DataOutputStream(v.getOutputStream()).writeUTF(content);
                    } catch (IOException e) {
                        Logger.log("Client removed " + k);
                        clientMap.remove(k);
                    }
                });
            });
        }
    }

    /**
     * 开启一个广播线程
     */
    private void startBroadcasting(){
        sendExecutor.submit(()->{
            String message;
            while(true){
                message = messageQueue.takeMessage();
                if(message.equals(MessageParam.SERVER_SHUTDOWN)){
                    serverShutDown();
                    break;
                }
                broadcast(message);
            }
        });
    }

    /**
     * 每个用户一个线程发发送
     * 用多线程保证每个用户同时收到消息
     */
    public void send(String[] clients, String message) {
        for(String client : clients){
            sendExecutor.submit(()->{
                clientMap.forEach((k,v)->{
                    String[] info = k.split("/");
                    if(info[info.length - 1].equals(client)) {
                        try {
                            new DataOutputStream(v.getOutputStream()).writeUTF(message);
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.out.println("Failed to send to " + client);
                            Logger.log("Failed messaging client " + client);
                        }
                    }
                });
            });
        }
    }
}
