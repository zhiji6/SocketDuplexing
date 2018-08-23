package com.stringing.socketduplexing.thread;

import com.stringing.socketduplexing.message.constants.MessageParam;
import com.stringing.socketduplexing.message.queue.GenericQueue;
import com.stringing.socketduplexing.utils.IPTool;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class WriteThread implements Runnable {
    private Socket client;
    private String clientToken;
    private DataOutputStream dos;
    private boolean isClient;
    private boolean isLAN;
    private final String identity;
    private GenericQueue messageQueue;
    private ExecutorService executorService;


    public WriteThread(Socket client, String clientToken, GenericQueue messageQueue, boolean isClient, boolean isLAN, ExecutorService executorService){
        this.client = client;
        this.clientToken = clientToken;
        this.isClient = isClient;
        this.isLAN = isLAN;
        identity = isClient ? MessageParam.CLIENT_IDENTITY : MessageParam.SERVER_IDENTITY;
        this.messageQueue = messageQueue;
        this.executorService = executorService;
    }

    /**
     * 判断若是客户端则首先将本机唯一标识发送给服务端
     * 作为服务端识别一次持续连接中不同用户的依据
     * clientMap键格式为用户 "公网ip/内网ip"
     * clientMap值为用户socket对象
     */
    @Override
    public void run() {
        try {
            OutputStream os = client.getOutputStream();
            dos = new DataOutputStream(os);
            //如果是客户端则首先发送Ip给服务端
            if (isClient) {
                String localIp = IPTool.getLocalIp();
                //因为只是第一次要发送客户信息，所以发送完就取消状态标记
                isClient = !isClient;
                //如果是局域网环境则只发送内网ip
                if(isLAN) {
                    dos.writeUTF(localIp + "/" + clientToken);
                }else{
                    //如果是非局域网环境则还要发送外网ip
                    String ip = IPTool.getIp();
                    dos.writeUTF(ip + "/" + localIp + "/" + clientToken);
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
            String message;
            while(true){
                message = messageQueue.takeMessage();
                //用startsWith因为发过来的消息加上了发送者标识，所以并不是单纯的一条指令
                if((message).startsWith(MessageParam.CLIENT_SHUTDOWN)) {
                    try {
                        dos.close();
                        client.close();
                        executorService.shutdownNow();
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Something wrong happened while shutting down the client!");
                    }
                }
                try {
                    dos.writeUTF(message);
                } catch (IOException e) {
                    System.out.println("Transportation failed! Please check on the server!");
                    e.printStackTrace();
                    break;
                }
            }
    }

}
