package cs.service.imp;

import cs.service.GenericServer;
import cs.thread.ReadThread;
import message.queue.GenericQueue;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务端对象
 */
public class Server implements GenericServer {
    private static ServerSocket serverSocket;
    private static Map<String, Socket> clientMap = new ConcurrentHashMap<>();
    private static ReadThread readThread;
    private GenericQueue messageQueue;
    private boolean isClient;
    private boolean isLAN;

    public Server(GenericQueue messageQueue, boolean isLAN){
        this.messageQueue = messageQueue;
        isClient = false;
        this.isLAN = isLAN;
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        startBroadcasting();//首先开启一个广播线程
        while(true) {
            try {
                client = serverSocket.accept();
            }catch (IOException e){
                break;//服务端已经关闭了的话则结束本线程
            }
                System.out.println("A client has just connected...");
                readThread = new ReadThread(client, clientMap, isClient, this);
                new Thread(readThread).start();
            }
    }

    /**
     * 关闭服务端
     * 关闭服务端的读线程
     */
    @Override
    public void serverShutDown() {
        if(serverSocket != null) {
            try {
                serverSocket.close();
                System.exit(0);//这个到具体应用上的作用待检验
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 向用户广播消息
     * 通常用于一对多的通信
     * 一对一通信则优先选择单播
     */
    @Override
    public void broadcast(String content) {
        clientMap.forEach((k, v) ->{
            try {
                new DataOutputStream(v.getOutputStream()).writeUTF(content);
                } catch (IOException e) {
                clientMap.remove(k);
            }
        });
    }

    /**
     * 开启一个广播线程
     * 定制一条服务端关闭指令
     */
    private void startBroadcasting(){
        new Thread(()->{
                String message;
                while(true){
                    message = messageQueue.takeMessage();
                    if(message.equals("shutdown -s")){
                        serverShutDown();
                        break;
                    }
                    broadcast(message);
                }
            //}
        }).start();
    }

    /**
     * 此方法非接口规定方法
     * 后期可自由开发更好的发送方法
     *
     * 每个用户一个线程发发送
     * 尽量保证每个用户同时收到消息
     */
    public void send(String[] clients, String message) {
        for(String client : clients){
            new Thread(()->{
                clientMap.forEach((k,v)->{
                    if(k.contains(client)) {
                        try {
                            new DataOutputStream(v.getOutputStream()).writeUTF(message);
                        } catch (IOException e) {
                            System.out.println("Failed to send to " + client);
                        }
                    }
                });
            }
            ).start();
        }
    }
}
