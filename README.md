# SocketDuplexing
基于Java Socket的通讯引擎

鉴于在Java Socket编程过程中碰到过的种种玄学问题，花了点时间开发出这个通讯引擎来省掉很多不必要的麻烦，同时把这段时间学到的一些知识运用一下当作菜菜菜鸟的练习哈哈。

这个通讯引擎可以适用于各种网络应用中，从简单的网络传输到复杂些的局域网/互联网聊天应用都可以使用该引擎来简化开发，开发者只需写好相应的应用逻辑即可达到各种通讯功能。

2018/07/18
完成了大体的功能
后续还要添加服务端日志记录的功能

2018/07/24
旅游了几天，今天把剩下的模块做完了
包括应用层的接收消息队列和简单的日志模块

下面是使用姿势：

首先在作为服务器的主机上开启服务端

  Dispatcher dispatcher = MessageDispatcher.getInstance();
  
  dispatcher.dispatchServerOnTerminal(端口号(int), 是否为局域网(boolean));
  
然后是客户端

如果只是简单的在终端进行传输任务，则可以

  Dispatcher dispatcher = MessageDispatcher.getInstance();
  
  dispatcher.dispatchClientOnTerminal(服务器ip地址(String), 服务端监听的端口(int), 用户唯一标识(String), 是否为局域网(boolean));
  
如果是做成应用，比如一个可视化的聊天软件，则可以

MessageManager mm = dispatcher.dispatchClientOnApplication(服务器ip地址(String), 服务端监听的端口(int), 用户唯一标识(String),是否为局域网(boolean));

应用层用得到的这个MessageManager消息管理器来进行通讯

它里面有两个主要方法

弟一个方法是void sendMessage(Vector<String> clients, String message);
  
第一个参数是用户唯一标识，从一对一到群聊可以对应的添加一到多个用户

第二个参数则是要发送的消息内容
  
第二个方法是String receiveMessage();
这是一个阻塞方法，通常放在一个循环内就可以，只有收到消息才会继续往下走，没收到则阻塞在此位置继续等待

另外退出应用的时候记得调用closeClient方法

日志模块目前就是简单的在服务端记录服务端和客户连接的情况保存在server.log里，有更详细的需要可以自行改动
