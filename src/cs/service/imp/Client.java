package cs.service.imp;

import cs.service.GenericClient;
import cs.thread.ReadThread;
import cs.thread.WriteThread;
import message.queue.GenericQueue;

import java.io.IOException;
import java.net.Socket;

public class Client implements GenericClient {
    private Socket client;
    private String clientToken;
    private boolean isLAN;
    private boolean isClient;
    private GenericQueue messageQueue;

    public Client(GenericQueue messageQueue, String clientToken, boolean isLAN){
        this.messageQueue = messageQueue;
        this.clientToken = clientToken;
        this.isLAN = isLAN;
        isClient = true;
    }

    @Override
    public Socket connectServer(String ip, int port) {
        try {
            client = new Socket("localhost", 33333);
        } catch (IOException e) {
            System.out.println("Connection failed! Please check on the server!");
        }

        new Thread(new ReadThread(client, null, isClient, null)).start();
        new Thread(new WriteThread(client, clientToken, messageQueue, isClient, isLAN)).start();

        return client;
    }

}
