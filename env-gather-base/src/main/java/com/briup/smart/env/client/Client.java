package com.briup.smart.env.client;

import com.briup.smart.env.entity.Environment;

import java.util.Collection;

/**
 * Client接口是物联网环境监测数据中心项目网络模块中客户端的规范
 * Client的作用就是与服务器进行通信传递信息
 */
public interface Client {
    /**
     * 向网络模块服务端发送对象
     *
     * @param collection 集合对象
     */
    void send(Collection<Environment> collection) throws Exception;
}
