package server;

public interface GenericServer {
    /**
     * 服务端监听
     * @param port 监听端口
     */
    void startListening(int port);

    /**
     * 关闭服务端
     */
    void serverShutDown();

    /**
     * 广播，发送消息给所有客户端
     * @param content 消息内容
     */
    void broadcast(String content);

}
