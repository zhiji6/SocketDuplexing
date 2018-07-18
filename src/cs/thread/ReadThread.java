package cs.thread;



import cs.service.imp.Server;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Map;

public class ReadThread implements Runnable {
    private Socket client;
    private final String identity;
    private boolean isClient;
    private DataInputStream dis;
    private static Map<String,Socket> clientMap;
    private String token;
    private Server server;

    public ReadThread(Socket client, Map<String,Socket> clientMap, boolean isClient, Server server){
        this.client = client;
        this.isClient = isClient;
        this.clientMap = clientMap;
        identity = isClient ? "client" : "server";
        this.server = server;
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
                System.out.println(content);
                if(!isClient) {
                    clientMap.put(content, client);
                    token = content;
                    isClient = !isClient;
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
