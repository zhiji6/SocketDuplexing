package message.dispatcher;

import message.manager.MessageManager;

public interface Dispatcher {
    /**
     *开启服务端监听
     * @param port 监听的端口
     * @param isLAN 是否为局域网，true为内网，false则为外网
     */
    void dispatchServerOnTerminal(int port, boolean isLAN);

    /**
     * 在终端运行客户端
     * @param ip 服务器ip
     * @param port 端口
     * @param clientToken 用户唯一标识符
     * @param isLAN 是否为局域网，true为内网，false则为外网
     */
    void dispatchClientOnTerminal(String ip, int port, String clientToken, boolean isLAN);

    /**
     * 在应用层运行客户端
     * @param ip 服务器ip
     * @param port 端口
     * @param clientToken 用户唯一标识符
     * @param isLAN 是否为局域网，true为内网，false则为外网
     * @return 返回一个消息管理器供应用层调用
     */

    MessageManager dispatchClientOnApplication(String ip, int port, String clientToken, boolean isLAN);
}
