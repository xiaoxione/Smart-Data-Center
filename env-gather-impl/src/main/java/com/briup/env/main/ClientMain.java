package com.briup.env.main;

import com.briup.env.ConfigurationImpl;
import com.briup.smart.env.Configuration;
import com.briup.smart.env.client.Client;
import com.briup.smart.env.client.Gather;
import com.briup.smart.env.entity.Environment;
import com.briup.smart.env.util.Log;

import java.util.Collection;

public class ClientMain {
    public static void main(String[] args) throws Exception {

        Configuration configuration = ConfigurationImpl.getInstance();
        Log log = configuration.getLogger();// = new LogImpl();

        //1.实例化采集模块对象，调用采集方法，实现采集功能
        //Gather gt = new GatherImpl();
        //Gather gt = new GatherBackupImpl();
        Gather gt = configuration.getGather();
        Collection<Environment> envs = gt.gather();
        //输出集合元素个数
        log.debug("元素数量: " + envs.size());

        //2.发送集合对象到服务器
        //Client client = new ClientImpl();
        Client client = configuration.getClient();
        client.send(envs);
    }
}
