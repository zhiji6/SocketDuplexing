package com.stringing.socketduplexing.client.impl;

import com.stringing.socketduplexing.client.ConfClient;
import com.stringing.socketduplexing.client.GenericClient;
import com.stringing.socketduplexing.thread.ReadThread;
import com.stringing.socketduplexing.thread.WriteThread;
import com.stringing.socketduplexing.message.queue.GenericQueue;
import com.stringing.socketduplexing.utils.SocketConfiguration;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client implements GenericClient, ConfClient {
    private Socket client;
    private String clientToken;
    private boolean isLAN;
    private boolean isClient;
    private boolean isTerminal;
    private GenericQueue sendQueue;
    private GenericQueue recvQueue;
    private ExecutorService ioExecutor;

    public Client(GenericQueue sendQueue, GenericQueue recvQueue, String clientToken, boolean isLAN, boolean isTerminal){
        this.sendQueue = sendQueue;
        this.recvQueue = recvQueue;
        this.clientToken = clientToken;
        this.isLAN = isLAN;
        isClient = true;
        this.isTerminal = isTerminal;
        ioExecutor = Executors.newFixedThreadPool(2);
    }

    @Override
    public void connectWithConf(SocketConfiguration conf) {
        connectServer(conf.getClientIp(), conf.getClientPort());
    }

    @Override
    public Socket connectServer(String ip, int port) {
        System.out.println("Connecting server...");
        try {
            client = new Socket(ip, port);
        } catch (IOException e) {
            e.printStackTrace();
            ioExecutor.shutdownNow();
            System.out.println("Connection failed! Please check on the server!");
            System.exit(1);
            return null;
        }
        System.out.println("Connection established.");

        ioExecutor.submit(new ReadThread(client, null, recvQueue, isClient, isTerminal, null));
        ioExecutor.submit(new WriteThread(client, clientToken, sendQueue, isClient, isLAN, ioExecutor));

        return client;
    }

}
