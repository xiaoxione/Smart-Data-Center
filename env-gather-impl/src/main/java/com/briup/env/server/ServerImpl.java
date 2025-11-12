package com.briup.env.server;

import com.briup.smart.env.Configuration;
import com.briup.smart.env.entity.Environment;
import com.briup.smart.env.server.DBStore;
import com.briup.smart.env.server.Server;
import com.briup.smart.env.support.ConfigurationAware;
import com.briup.smart.env.support.PropertiesAware;
import com.briup.smart.env.util.Log;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Properties;

public class ServerImpl implements Server, PropertiesAware, ConfigurationAware {
    private Log log;// = new LogImpl();
    private DBStore dbStore;

    //服务器起始状态：未关闭
    private boolean isStop;// = false;
    private int port;// = 9999;
    private ServerSocket serverSocket;

    @Override
    public void init(Properties prop) throws Exception {
        String isStopStr = prop.getProperty("isStop");
        isStop = Boolean.parseBoolean(isStopStr);

        String portStr = prop.getProperty("port");
        port = Integer.parseInt(portStr);
    }

    @Override
    public void setConfiguration(Configuration configuration) throws Exception {
        log = configuration.getLogger();
        dbStore = configuration.getDbStore();
    }

    //多线程服务器
    @Override
    public void receive() {
        //1.搭建TCP服务器，接收客户端发送过来的集合对象
        try {
            // 1.搭建服务端
            serverSocket = new ServerSocket(port);
            log.info("网络模块服务端启动成功,port: " + port + ",等待客户端连接...");

            while (!isStop) {
                // 2.接收客户端的连接
                Socket socket = serverSocket.accept();
                log.info("客户端成功连接,socket: " + socket);

                // 3.分离子线程为客户端提供服务
                Thread th = new Thread() {
                    @Override
                    public void run() {
                        ObjectInputStream ois = null;
                        try {
                            if(isStop)
                                return;

                            // 3.准备对象流
                            ois = new ObjectInputStream(socket.getInputStream());
                            // 4.读取集合对象
                            Collection<Environment> coll =
                                    (Collection<Environment>) ois.readObject();
                            //System.out.println("成功接收到集合对象: " + coll);
                            log.info("成功接收到集合对象，内含环境数据个数: " + coll.size());

                            //入库功能实现
                            //DBStore dbStore = new DBStoreImpl();
                            //DBStore dbStore = new DBStoreBackupImpl();
                            dbStore.saveDB(coll);

                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            log.info("连接客户端"+socket+" 即将关闭...");
                            try {
                                if (ois != null)
                                    ois.close();
                            } catch (IOException e) {
                                log.warn("流对象关闭产生异常: " + e.toString());
                            }

                            try {
                                if (socket != null)
                                    socket.close();
                            } catch (IOException e) {
                                log.warn("socket关闭产生异常: " + e.toString());
                            }
                        }
                    }
                };
                th.start();
            }

            log.debug("while循环结束...");
        } catch(Exception e) {
            log.warn("服务器端网络传输异常: " + e.toString());
        }
    }

    //单线程服务器
    //@Override
    public void receive01() {
        Socket socket = null;
        //1.搭建TCP服务器，接收客户端发送过来的集合对象
        try {
            // 1.搭建服务端
            serverSocket = new ServerSocket(port);
            System.out.println("网络模块服务端启动成功,port: " + port + ",等待客户端连接...");

            // 2.接收客户端的连接
            socket = serverSocket.accept();
            System.out.println("客户端成功连接,socket: " + socket);

            // 3.准备对象流
            ObjectInputStream ois =
                    new ObjectInputStream(socket.getInputStream());

            // 4.读取集合对象
            @SuppressWarnings("unchecked")
            Collection<Environment> coll =
                    (Collection<Environment>) ois.readObject();
            System.out.println("成功接收到集合对象，内含环境数据个数: " + coll.size());

            //未完待续...

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("服务器即将关闭...");
            try {
                if(socket != null)
                    socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if(serverSocket != null)
                    serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void shutdown() throws Exception {
        isStop = true;
        log.info("修改关闭标识isStop为true");

        if (serverSocket != null)
            serverSocket.close();

        log.info("服务端网络模块: shutdown执行完毕");
    }
}