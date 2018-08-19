package client.impl;

import client.ConfClient;
import client.GenericClient;
import server.ConfServer;
import thread.ReadThread;
import thread.WriteThread;
import message.queue.GenericQueue;
import utils.SocketConfiguration;

import java.io.IOException;
import java.net.Socket;

public class Client implements GenericClient, ConfClient {
    private Socket client;
    private String clientToken;
    private boolean isLAN;
    private boolean isClient;
    private boolean isTerminal;
    private GenericQueue sendQueue;
    private GenericQueue recvQueue;

    public Client(GenericQueue sendQueue, GenericQueue recvQueue, String clientToken, boolean isLAN, boolean isTerminal){
        this.sendQueue = sendQueue;
        this.recvQueue = recvQueue;
        this.clientToken = clientToken;
        this.isLAN = isLAN;
        isClient = true;
        this.isTerminal = isTerminal;
    }

    @Override
    public void connectWithConf(SocketConfiguration conf) {
        connectServer(conf.getClientIp(), conf.getClientPort());
    }

    @Override
    public Socket connectServer(String ip, int port) {
        try {
            client = new Socket(ip, port);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Connection failed! Please check on the server!");
            System.exit(1);
            return null;
        }

        new Thread(new ReadThread(client, null, recvQueue, isClient, isTerminal, null)).start();
        new Thread(new WriteThread(client, clientToken, sendQueue, isClient, isLAN)).start();

        return client;
    }

}
