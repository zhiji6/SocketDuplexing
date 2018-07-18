package message.dispatcher.imp;

import cs.service.GenericClient;
import cs.service.GenericServer;
import cs.service.imp.Client;
import cs.service.imp.Server;
import message.dispatcher.Dispatcher;
import message.manager.MessageManager;
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
            GenericClient client = new Client(mq, clientToken, isLAN);
            client.connectServer(ip, port);
            String message;
            while(true){
                Scanner sc = new Scanner(System.in);
                message = sc.nextLine();
                mq.putMessage(message);
                if(message.equals("shutdown -c")) break;
            }
        }

        @Override
        public MessageManager dispatchClientOnApplication(String ip, int port, String clientToken, boolean isLAN) {
            MessageManager mm = MessageManager.getInstance();
            mm.setMessageQueue(new MessageQueue());
            return mm;
        }
    };

    public static Dispatcher getInstance(){return MessageDispatcher.dispatcher;}


}
