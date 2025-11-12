package com.briup.smart.env.server;

/**
 * Server接口是物联网数据中心项目网络模块中服务端的规范
 * 该模板负责与客户端进行通信接收信息
 * 并且利用DBStore模块的功能将接收的数据进行持久化操作
 */
public interface Server {
    /**
     * 接收网络模块客户端的连接，要求一直接收连接，每当有一个客户端连接到服务端
     * 后，服务端就创建一个线程来接收客户端发送过来的数据。接收完毕后调用入库模块
     * 将接收的数据保存到数据库中
     */
    void receive() throws Exception;

    /**
     * 关闭网络模块服务端，停止接收来自网络模块客户端的连接
     */
    void shutdown() throws Exception;
}
