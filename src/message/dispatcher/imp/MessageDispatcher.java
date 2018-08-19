package message.dispatcher.imp;

import client.GenericClient;
import server.GenericServer;
import client.impl.Client;
import server.impl.Server;
import message.constants.MessageParam;
import message.dispatcher.Dispatcher;
import message.manager.MessageHandler;
import message.queue.GenericQueue;
import message.queue.imp.MessageQueue;
import utils.SocketConfiguration;

import java.util.Scanner;

public enum MessageDispatcher implements Dispatcher {
    dispatcher{
        @Override
        public void dispatchServerOnTerminal(SocketConfiguration conf) {
            GenericQueue mq = new MessageQueue();
            GenericServer server = new Server(mq, conf.isLAN());
            new Thread(()->{
                String message;
                while(true){
                    Scanner sc = new Scanner(System.in);
                    message = sc.nextLine();
                    mq.putMessage(message);
                    if(message.equals(MessageParam.SERVER_SHUTDOWN)) break;
                }
            }).start();
            ((Server) server).listenWithConf(conf);
        }

        @Override
        public void dispatchClientOnTerminal(String clientToken, SocketConfiguration conf) {
            GenericQueue mq = new MessageQueue();
            GenericClient client = new Client(mq, null, clientToken, conf.isLAN(), true);
            ((Client) client).connectWithConf(conf);
            String message;
            while(true){
                Scanner sc = new Scanner(System.in);
                message = sc.nextLine();
                mq.putMessage(message + " " + MessageParam.TALK_FROM + " " + clientToken);
                if(message.equals(MessageParam.CLIENT_SHUTDOWN)) break;
            }
        }

        @Override
        public MessageHandler dispatchClientOnApplication(SocketConfiguration conf, String clientToken) {
            MessageHandler mm = MessageHandler.getInstance();
            mm.setSendQueue(new MessageQueue());
            mm.setRecvQueue(new MessageQueue());
            mm.setIp(conf.getClientIp());
            mm.setPort(conf.getClientPort());
            mm.setClientToken(clientToken);
            mm.setLAN(conf.isLAN());
            mm.connect();
            return mm;
        }
    };

    public static Dispatcher getInstance(){return MessageDispatcher.dispatcher;}


}
