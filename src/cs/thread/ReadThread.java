package cs.thread;



import cs.service.imp.Server;
import message.queue.GenericQueue;
import utils.Logger;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Map;

public class ReadThread implements Runnable {
    private Socket client;
    private final String identity;
    private boolean isClient;
    private boolean isTerminal;
    private DataInputStream dis;
    private static Map<String,Socket> clientMap;
    private String token;
    private Server server;
    private GenericQueue messageQueue;

    public ReadThread(Socket client, Map<String,Socket> clientMap, GenericQueue messageQueue, boolean isClient, boolean isTerminal, Server server){
        this.client = client;
        this.isClient = isClient;
        this.isTerminal = isTerminal;
        this.clientMap = clientMap;
        identity = isClient ? "client" : "server";
        this.server = server;
        this.messageQueue = messageQueue;
    }

    @Override
    public void run() {
        InputStream is = null;
        try {
            is = client.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        dis = new DataInputStream(is);
            while(true){
                String content = null;
                try {
                    content = dis.readUTF();
                } catch (IOException e) {
                    try {
                        dis.close();
                        is.close();
                        client.close();
                        break;//服务端或客户端读消息失败的话说明对应连接的客户端或服务端已挂，则结束本线程
                    }catch (IOException e1){
                        System.out.println("Something wrong happened while shutting down client!");
                    }
                }
                if(isTerminal)
                    System.out.println(content);
                if(isClient && identity.equals("client") && !isTerminal){
                    messageQueue.putMessage(content);
                }
                if(!isClient) {
                    clientMap.put(content, client);
                    token = content;
                    isClient = !isClient;
                    Logger.log("Client connected " + token);
                }
                if(isClient && identity.equals("server")){
                    /**
                     * 通讯格式
                     * "-t " + "c1 c2 ... cn"+ "-end" + " content"
                     * 切分到中间的若干用户传给server定向发送消息
                     */
                    if(content.startsWith("-t ")){
                        int end = content.indexOf("-end");
                        String[] clients = content.substring(3, end).split(" ");
                        String message = content.substring(end + 5);
                        server.send(clients, message);
                    }
                }
            }
    }
}
