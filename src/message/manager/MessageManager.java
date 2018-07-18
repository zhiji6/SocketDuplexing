package message.manager;

import message.queue.GenericQueue;
import java.util.Vector;

public class MessageManager {
    private static GenericQueue mq = null;

    private static MessageManager messageManager = null;

    private MessageManager(){}

    public static MessageManager getInstance(){
        if(messageManager == null)
            messageManager = new MessageManager();
        return messageManager;
    }

    public static void setMessageQueue(GenericQueue messageQueue){
        if(messageQueue == null)
            mq = messageQueue;
    }

    /**
     * list内添加一至多个用户token，达到一对一或群聊的效果
     * 将用户和消息包装成服务端可以读取的指令
     * @param clients 用户的tokens
     * @param message 消息内容
     */
    public static void sendMessage(Vector<String> clients, String message) throws NullPointerException{
        if(mq == null)
            throw new NullPointerException();
        StringBuffer sb = new StringBuffer();
        clients.forEach((c)->{
            sb.append(" " + c);
        });
        sb.insert(0, "-t");
        sb.append("-end");
        sb.append(" " + message);
        mq.putMessage(sb.toString());
    }

    public static void closeClient(){
        mq.putMessage("shutdown -c");
    }
}
