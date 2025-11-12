package com.briup.env.client;

import com.briup.smart.env.Configuration;
import com.briup.smart.env.client.Client;
import com.briup.smart.env.entity.Environment;
import com.briup.smart.env.support.ConfigurationAware;
import com.briup.smart.env.support.PropertiesAware;
import com.briup.smart.env.util.Log;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.Properties;

public class ClientImpl implements Client, PropertiesAware, ConfigurationAware {
    private Log log;// = new LogImpl();

    //准备服务器ip和port
    private String serverIp;// = "127.0.0.1";
    private int serverPort;// = 9999;

    @Override
    public void init(Properties prop) throws Exception {
        serverIp = prop.getProperty("serverIp");
        String portStr = prop.getProperty("serverPort");
        serverPort = Integer.parseInt(portStr);
    }

    @Override
    public void setConfiguration(Configuration configuration) throws Exception {
        log = configuration.getLogger();
    }

    @Override
    public void send(Collection<Environment> coll) {
        //参数判断
        if (coll == null || coll.size() == 0) {
            log.info("客户端网络模块: 接收的数据有误");
            return;
        }

        //搭建TCP客户端，然后发送collection集合对象到服务器
        Socket socket = null;
        ObjectOutputStream oos = null;
        try {
            //1.搭建客户端，连接到服务器
            socket = new Socket(serverIp, serverPort);
            log.info("客户端网络模块: 连接成功");

            //2.获取IO流
            oos = new ObjectOutputStream(socket.getOutputStream());
            log.info("客户端网络模块: 准备发送数据");

            //3.发送集合对象
            oos.writeObject(coll);
            log.info("客户端网络模块: 数据发送成功,共" + coll.size() + "条");
        } catch (IOException e) {
            log.warn(e.toString());
        } finally {
            //4.关闭资源
            try {
                if(oos != null)
                    oos.close();
            } catch (IOException e) {
                log.warn(e.toString());
            }

            try {
                if(socket != null)
                    socket.close();
            } catch (IOException e) {
                log.warn(e.toString());
            }
            log.info("客户端成功关闭!");
        }
    }
}