package com.stringing.socketduplexing.message.dispatcher.imp;

import com.stringing.socketduplexing.client.GenericClient;
import com.stringing.socketduplexing.server.GenericServer;
import com.stringing.socketduplexing.client.impl.Client;
import com.stringing.socketduplexing.server.impl.Server;
import com.stringing.socketduplexing.message.constants.MessageParam;
import com.stringing.socketduplexing.message.dispatcher.Dispatcher;
import com.stringing.socketduplexing.message.manager.MessageHandler;
import com.stringing.socketduplexing.message.queue.GenericQueue;
import com.stringing.socketduplexing.message.queue.imp.MessageQueue;
import com.stringing.socketduplexing.utils.SocketConfiguration;

import java.util.Scanner;

public enum MessageDispatcher implements Dispatcher {
    dispatcher{
        @Override
        public void dispatchServerOnTerminal(SocketConfiguration conf) {
            GenericQueue mq = new MessageQueue();
            GenericServer server = new Server(mq, conf.isLAN(), conf.getThreadNum());
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
