package server;

import utils.SocketConfiguration;

/**
 * @Description
 * @Author Stringing
 * @Date 2018/8/19 14:46
 */
public interface ConfServer {

    /**
     * 根据配置文件的参数来监听
     * @param conf Socket配置
     */
    void listenWithConf(SocketConfiguration conf);
}
