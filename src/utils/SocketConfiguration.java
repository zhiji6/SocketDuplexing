package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @Description socket连接参数配置
 * @Author Stringing
 * @Date 2018/8/19 14:14
 */
public class SocketConfiguration {
    private String clientIp;
    private String clientPort;
    private String serverPort;
    private boolean isLAN;
    private int threadNum;
    private final String SERVER_CONF_PATH;
    private final String CLIENT_CONF_PATH;
    private final String DEFAULT_SERVER_CONF_PATH = "serverConf.prop";
    private final String DEFAULT_CLIENT_CONF_PATH = "clientConf.prop";
    private final String DEFAULT_IP = "localhost";
    private final String DEFAULT_PORT = "33333";
    private final boolean DEFAULT_LAN = true;
    private final int DEFAULT_THREAD_NUM = 10;

    /**
     * 默认读写当前目录下的配置文件
     * @throws IOException
     */
    public SocketConfiguration() throws IOException {
        SERVER_CONF_PATH = DEFAULT_SERVER_CONF_PATH;
        CLIENT_CONF_PATH = DEFAULT_CLIENT_CONF_PATH;
        isLAN = DEFAULT_LAN;
        init();
    }

    /**
     * 读取指定的配置文件，若只需一端的自定义配置则把另一端的路径设为null即可
     * @param serverConfPath 服务端配置文件路径
     * @param clientConfPath 客户端配置文件路径
     * @throws IOException
     */
    public SocketConfiguration(String serverConfPath, String clientConfPath) throws IOException {
        if(serverConfPath == null && clientConfPath == null)
            throw new IllegalArgumentException("Configuration arguments cannot both be null!");
        SERVER_CONF_PATH = serverConfPath;
        CLIENT_CONF_PATH = clientConfPath;
        init();
    }

    /**
     * 配置初始化
     * @throws IOException
     */
    private void init() throws IOException {
        serverInit(new Properties());
        clientInit(new Properties());
    }

    /**
     * 服务器配置读取
     * @param properties 服务器配置文件
     * @throws IOException
     */
    private void serverInit(Properties properties) throws IOException {
        if(SERVER_CONF_PATH != null){
            File serverConf = new File(SERVER_CONF_PATH);
            if(!serverConf.exists()){
                serverConf.createNewFile();
                serverPort = DEFAULT_PORT;
                threadNum = DEFAULT_THREAD_NUM;
                properties.setProperty("port", serverPort);
                properties.setProperty("isLAN", isLAN ? "true" : "false");
                properties.setProperty("MaxClientNum", String.valueOf(threadNum));
                properties.store(new FileOutputStream(serverConf), null);
            }else{
                properties.load(new FileInputStream(serverConf));
                serverPort = properties.getProperty("port");
                isLAN = properties.getProperty("isLAN").equals("true") ? true : false;
                threadNum = Integer.parseInt(properties.getProperty("MaxClientNum"));
            }
        }
    }

    /**
     * 客户端配置读取
     * @param properties 客户端配置文件
     * @throws IOException
     */
    private void clientInit(Properties properties) throws IOException {
        if(CLIENT_CONF_PATH != null){
            File clientConf = new File(CLIENT_CONF_PATH);
            if(!clientConf.exists()){
                clientConf.createNewFile();
                clientIp = DEFAULT_IP;
                clientPort = DEFAULT_PORT;
                properties.setProperty("ip", clientIp);
                properties.setProperty("port", serverPort);
                properties.setProperty("isLAN", isLAN ? "true" : "false");
                properties.store(new FileOutputStream(clientConf), null);
            }else{
                properties.load(new FileInputStream(clientConf));
                clientIp = properties.getProperty("ip");
                clientPort = properties.getProperty("port");
                isLAN = properties.getProperty("isLAN").equals("true") ? true : false;
            }
        }
    }

    public boolean isLAN() {
        return isLAN;
    }

    public String getClientIp() {
        return clientIp;
    }

    public int getClientPort() {
        return Integer.parseInt(clientPort);
    }

    public int getServerPort() {
        return Integer.parseInt(serverPort);
    }

    public int getThreadNum() { return threadNum; }
}
