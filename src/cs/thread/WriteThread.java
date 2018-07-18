package cs.thread;

import message.queue.GenericQueue;
import utils.IPTool;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class WriteThread implements Runnable {
    private Socket client;
    private String clientToken;
    private DataOutputStream dos;
    private boolean isClient;
    private boolean isLAN;
    private final String identity;
    private GenericQueue messageQueue;


    public WriteThread(Socket client, String clientToken, GenericQueue messageQueue, boolean isClient, boolean isLAN){
        this.client = client;
        this.clientToken = clientToken;
        this.isClient = isClient;
        this.isLAN = isLAN;
        identity = isClient ? "client" : "server";
        this.messageQueue = messageQueue;
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
            if (isClient) {
                String localIp = IPTool.getLocalIp();
                isClient = !isClient;
                if(isLAN) {
                    dos.writeUTF(localIp + "/" + clientToken);
                }else{
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
                if(message.equals("shutdown -c")) {
                    try {
                        dos.close();
                        dos.close();
                        client.close();
                        break;
                    } catch (IOException e) {
                        System.out.println("Something wrong happened while shutting down the client!");
                    }
                }
                try {
                    dos.writeUTF(message);
                } catch (IOException e) {
                    System.out.println("Transportation failed! Please check on the server!");
                    break;
                }
            }
    }

}
