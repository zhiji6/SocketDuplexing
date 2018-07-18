package cs.service;

import java.net.Socket;

public interface GenericClient {
    /**
     * 连接服务器
     * @param ip 服务器ip
     * @param port 端口
     * @return 返回用户socket对象
     */
    Socket connectServer(String ip, int port);


}
