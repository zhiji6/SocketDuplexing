package message.manager;



public class MessageFilter {
    private String content;
    private String[] clients;
    private String message;

    /**
     * 通讯格式
     * "-t " + "$c1 $c2 ... $cn"+ "-end" + " content" + " -f $sender"
     * 首先切割处理分出接收者、消息内容、发送者
     * 消息内容经过过滤方法getFilteredMessage处理
     * 接收者收到的消息格式为"$sender" + ":filteredContent"
     * 不满足格式则将消息内容和接收者置为空
     */
    public void setContent(String content) {
        this.content = content;
        if(content.startsWith("-t ")){
            StringBuffer sb = new StringBuffer();
            int end = content.indexOf("-end");
            int fEnd = content.lastIndexOf("-f");
            if(end == -1 || fEnd == -1){
                message = null;
                clients = null;
                return;
            }
            clients = content.substring(3, end).split(" ");
            message = sb.append(content.substring(fEnd + 3)).append(":").append(content, end + 5, fEnd - 1).toString();
        }else{
            message = null;
            clients = null;
        }
    }

    /**
     * 得到接收者
     * @return 返回接收者，若content不符合格式则返回null
     */
    public String[] getClients(){
        return clients;
    }

    /**
     * 返回处理过的消息，比如加密过滤等等。
     * @return 返回处理过的消息，若content不符合格式则返回null
     */
    public String getFilteredMessage(){
        //详细过滤方法可根据需求定制，这里简单返回原消息
        return message;
    }

}
