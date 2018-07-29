package cs.thread;



import cs.service.imp.Server;
import message.manager.MessageFilter;
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
        MessageFilter mf = new MessageFilter();
            while(true){
                String content = null;
                try {
                    content = dis.readUTF();
                } catch (IOException e) {
                    try {
                        dis.close();
                        is.close();
                        client.close();
                        //服务端或客户端读消息失败的话说明对应连接的客户端或服务端已挂，则结束本线程
                        break;
                    }catch (IOException e1){
                        System.out.println("Something wrong happened while shutting down client!");
                    }
                }
                //终端模式输出消息在控制台
                if(isTerminal)
                    System.out.println(content);
                //如果是身份为客户端并为应用模式而不是终端模式
                if(isClient && identity.equals("client") && !isTerminal){
                    messageQueue.putMessage(content);
                }
                //若为服务端，因为客户端连接成功后默认会隐秘发送一条客户唯一标识消息
                //则接受这条消息并保存对应的客户端对象，并把isClient置反
                //因为唯一标识消息只会发一次，后面的都为客户消息
                if(!isClient) {
                    clientMap.put(content, client);
                    token = content;
                    isClient = !isClient;
                    Logger.log("Client connected " + token);
                }
                //经过了上面的置反操作后的服务端开始接收转发客户消息
                //客户消息交给消息过滤器MessageFilter进行分割、加密、过滤等处理
                //处理后转发给对应的客户端
                if(isClient && identity.equals("server")){
                    mf.setContent(content);
                    String[] clients = mf.getClients();
                    String message = mf.getFilteredMessage();
                    if(clients != null && message != null)
                        server.send(clients, message);
                }
            }
    }
}
