package message.manager;

import cs.service.GenericClient;
import cs.service.imp.Client;
import message.queue.GenericQueue;
import java.util.Vector;

/**
 * @author Wenqiang Luo
 * @date 2018/07/24
 */
public class MessageHandler {
    private static GenericQueue sendQueue = null;
    private static GenericQueue recvQueue = null;
    
    private static String ip;
    private static int port;
    private static String clientToken;
    private static boolean isLAN;

    public static void setIp(String ip) {
        MessageHandler.ip = ip;
    }

    public static void setPort(int port) {
        MessageHandler.port = port;
    }

    public static void setClientToken(String clientToken) {
        MessageHandler.clientToken = clientToken;
    }

    public void setLAN(boolean LAN) {
        isLAN = LAN;
    }

    private static MessageHandler messageHandler = null;

    private MessageHandler(){}

    public static MessageHandler getInstance(){
        if(messageHandler == null)
            messageHandler = new MessageHandler();
        return messageHandler;
    }

    /**
     * 设置发送消息队列
     * @param sendQueue 用来发送的消息队列
     */
    public static void setSendQueue(GenericQueue sendQueue){
        if(MessageHandler.sendQueue == null)
            MessageHandler.sendQueue = sendQueue;
    }

    /**
     * 设置接收消息队列
     * @param recvQueue 用来接收的消息队列
     */
    public static void setRecvQueue(GenericQueue recvQueue){
        if(MessageHandler.recvQueue == null)
            MessageHandler.recvQueue = recvQueue;
    }

    /**
     * list内添加一至多个用户token，达到一对一或群聊的效果
     * 将用户和消息包装成服务端可以读取的指令
     * @param clients 用户的tokens
     * @param message 消息内容
     */
    public static void sendMessage(Vector<String> clients, String message) throws NullPointerException{
        if(sendQueue == null)
            throw new NullPointerException();
        StringBuffer sb = new StringBuffer();
        clients.forEach((c)->{
            sb.append(" " + c);
        });
        sb.insert(0, "-t");
        sb.append("-end");
        sb.append(" " + message);
        sendQueue.putMessage(sb.toString());
    }

    /**
     * 这是一个阻塞方法，直到得到消息才会返回消息
     * @return 按顺序返回收到的消息
     */
    public static String receiveMessage(){
        return recvQueue.takeMessage();
    }

    public static void closeClient(){
        sendQueue.putMessage("shutdown -c");
    }

    public static void connect() {
        if(ip == null || port == 0 || clientToken == null)
            throw new NullPointerException("Client connection config not complete!");
        GenericClient client = new Client(sendQueue, recvQueue, clientToken, isLAN, false);
        client.connectServer(ip, port);
    }
}
