package message.dispatcher;

import message.manager.MessageHandler;
import utils.SocketConfiguration;

public interface Dispatcher {
    /**
     *开启服务端监听
     * @param conf Socket配置
     */
    void dispatchServerOnTerminal(SocketConfiguration conf);

    /**
     * 在终端运行客户端
     * @param clientToken 用户唯一标识符
     * @param conf Socket配置
     */
    void dispatchClientOnTerminal(String clientToken, SocketConfiguration conf);

    /**
     * 在应用层运行客户端
     * @param conf Socket配置
     * @param clientToken 用户唯一标识符
     * @return 返回一个消息管理器供应用层调用
     */

    MessageHandler dispatchClientOnApplication(SocketConfiguration conf, String clientToken);
}
