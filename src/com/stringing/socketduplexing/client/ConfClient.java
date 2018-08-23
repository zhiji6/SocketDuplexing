package com.stringing.socketduplexing.client;

import com.stringing.socketduplexing.utils.SocketConfiguration;

/**
 * @Description
 * @Author Stringing
 * @Date 2018/8/19 14:54
 */
public interface ConfClient {
    /**
     * 根据配置参数来连接服务端
     * @param conf Socket配置
     */
    void connectWithConf(SocketConfiguration conf);
}
