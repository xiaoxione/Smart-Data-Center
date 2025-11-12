package com.briup.env.main;

import com.briup.smart.env.Configuration;
import com.briup.smart.env.server.Server;
import com.briup.smart.env.util.Log;

public class ServerMain {
    public static void main(String[] args) throws Exception {

        Configuration configuration = com.briup.env.ConfigurationImpl.getInstance();

        //Log log = new LogImpl();
        Log log = configuration.getLogger();

        //1.启动服务器，接收客户端发送过来数据并入库
        //Server server = new ServerImpl();
        Server server = configuration.getServer();

        try {
            server.receive();
        } catch (Exception e) {
            log.warn("服务器网络接收异常,异常为：" + e.toString());
        }

        log.info("服务器应用程序，正常退出!");
    }
}
