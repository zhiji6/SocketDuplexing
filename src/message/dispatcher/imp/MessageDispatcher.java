package message.dispatcher.imp;

import cs.service.GenericClient;
import cs.service.GenericServer;
import cs.service.imp.Client;
import cs.service.imp.Server;
import message.dispatcher.Dispatcher;
import message.manager.MessageHandler;
import message.queue.GenericQueue;
import message.queue.imp.MessageQueue;

import java.util.Scanner;

public enum MessageDispatcher implements Dispatcher {
    dispatcher{
        @Override
        public void dispatchServerOnTerminal(int port, boolean isLAN) {
            GenericQueue mq = new MessageQueue();
            GenericServer server = new Server(mq, isLAN);
            new Thread(()->{
                String message;
                while(true){
                    Scanner sc = new Scanner(System.in);
                    message = sc.nextLine();
                    mq.putMessage(message);
                    if(message.equals("shutdown -s")) break;
                }
            }).start();
            server.startListening(port);
        }

        @Override
        public void dispatchClientOnTerminal(String ip, int port, String clientToken, boolean isLAN) {
            GenericQueue mq = new MessageQueue();
            GenericClient client = new Client(mq, null, clientToken, isLAN, true);
            client.connectServer(ip, port);
            String message;
            while(true){
                Scanner sc = new Scanner(System.in);
                message = sc.nextLine();
                mq.putMessage(message + " -f " + clientToken);
                if(message.equals("shutdown -c")) break;
            }
        }

        @Override
        public MessageHandler dispatchClientOnApplication(String ip, int port, String clientToken, boolean isLAN) {
            MessageHandler mm = MessageHandler.getInstance();
            mm.setSendQueue(new MessageQueue());
            mm.setRecvQueue(new MessageQueue());
            mm.setIp(ip);
            mm.setPort(port);
            mm.setClientToken(clientToken);
            mm.setLAN(isLAN);
            mm.connect();
            return mm;
        }
    };

    public static Dispatcher getInstance(){return MessageDispatcher.dispatcher;}


}
