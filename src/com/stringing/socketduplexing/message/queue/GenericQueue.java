package com.stringing.socketduplexing.message.queue;

public interface GenericQueue {

    String takeMessage();

    void putMessage(String message);

    boolean isEmpty();

    int getSize();
}
