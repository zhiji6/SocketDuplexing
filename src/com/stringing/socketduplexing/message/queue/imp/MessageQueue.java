package com.stringing.socketduplexing.message.queue.imp;

import com.stringing.socketduplexing.message.queue.GenericQueue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageQueue implements GenericQueue {
    private BlockingQueue<String> messageQueue;
    private static int size;
    private final static int DEFAULT_SIZE = 5;

    public MessageQueue(){
        this(DEFAULT_SIZE);
    }

    public MessageQueue(int size){
        this.size = size;
        messageQueue = new LinkedBlockingQueue<>(size);
    }

    @Override
    public String takeMessage(){
        String message = null;
        try {
            message = messageQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("waiting for message...");
        }
        return message;
    }

    @Override
    public void putMessage(String message)  {
        try {
            messageQueue.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("Message busy...");
        }
    }

    @Override
    public boolean isEmpty() {
        return messageQueue.isEmpty();
    }

    @Override
    public int getSize() {
        return messageQueue.size();
    }
}
