package com.briup.env;

import com.briup.smart.env.Configuration;
import com.briup.smart.env.client.Client;
import com.briup.smart.env.client.Gather;
import com.briup.smart.env.server.DBStore;
import com.briup.smart.env.server.Server;
import com.briup.smart.env.support.ConfigurationAware;
import com.briup.smart.env.support.PropertiesAware;
import com.briup.smart.env.util.Backup;
import com.briup.smart.env.util.Log;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/*
    配置模块实现类
        1.解析config.xml配置文件
        2.获取其他模块对象，添加map集合统一管理
        3.获取其他模块属性值，添加到Properties统一管理
 */
public class ConfigurationImpl implements Configuration {
    //将配置类对象设置为 单例对象，具体步骤如下
    //1.static配置类对象
    private static ConfigurationImpl configuration = new ConfigurationImpl();

    //3.私有构造器，防止类外实例化对象
    private ConfigurationImpl() {}

    //2.获取配置类对象
    public static ConfigurationImpl getInstance() {
        return configuration;
    }

    // key: 模块名   value: 模块对象
    private static Map<String,Object> map = new HashMap<>();
    // key: 属性名   value: 属性值
    private static Properties prop = new Properties();

    //静态代码块中，对map和prop进行初始化
    static {
        //1.解析config.xml文件，获取所有一级子标签 class属性，并实例化对象（反射技术）
        String filePath = "env-gather-impl/src/main/resources/config.xml";
        File file = new File(filePath);
        //System.out.println("exist: " + file.exists());

        try {
            //1.获取document对象
            SAXReader reader = new SAXReader();
            Document document = reader.read(file);

            //2.获取根标签，进而获取所有一级子标签
            List<Element> elements = document.getRootElement().elements();
            // 逐个一级子标签遍历
            for(Element e : elements) {
                // 3.获取模块名和模块对象，添加到map集合
                // 3.1 获取指定模块 名称
                String name = e.getName();
                //System.out.println(name);

                // 3.2 获取 指定模块 类的全包名
                Attribute classAttr = e.attribute("class");
                String className = classAttr.getValue();
                //System.out.println(className);

                // 3.3 根据类的全包名，实例化对象
                Class clazz = Class.forName(className);
                Object obj = clazz.newInstance();

                // 3.4 将 模块名-对象 添加到map集合中
                map.put(name,obj);

                // 4.解析二级子标签，则将其name-value 添加到prop对象中
                List<Element> secElements = e.elements();
                // 判断二级标签是否存在
                if(secElements != null && secElements.size() > 0) {
                    //System.out.println(name + "下的二级标签存在");

                    //4.1 遍历存在的二级子标签
                    for(Element secE : secElements) {
                        //4.2 获取标签名 标签值，并添加到prop中
                        //System.out.println(secE.getName()+": " + secE.getText());
                        prop.setProperty(secE.getName(),secE.getText());
                    }
                }
            }//至此，所有模块对象全部创建并添加到map中，所有属性值全部添加到prop中

            //测试：遍历map和prop
            // 5.
            Collection<Object> coll = map.values();
            // 遍历各模块对象
            for(Object obj : coll) {
                //5.1 对各模块中属性进行初始化
                // 如果模块对象实现了PropertiesAware接口，则调用init方法实现属性初始化
                if(obj instanceof PropertiesAware) {
                    PropertiesAware propertiesAware = (PropertiesAware) obj;
                    propertiesAware.init(prop);
                }

                //5.2 对各模块中包含的其他模块对象 进行初始化
                if(obj instanceof ConfigurationAware) {
                    ConfigurationAware configurationAware = (ConfigurationAware) obj;
                    configurationAware.setConfiguration(configuration);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Log getLogger() throws Exception {
        return (Log) map.get("logger");
    }

    @Override
    public Server getServer() throws Exception {
        return (Server) map.get("server");
    }

    @Override
    public Client getClient() throws Exception {
        return (Client) map.get("client");
    }

    @Override
    public DBStore getDbStore() throws Exception {
        return (DBStore) map.get("dbStore");
    }

    @Override
    public Gather getGather() throws Exception {
        return (Gather) map.get("gather");
    }

    @Override
    public Backup getBackup() throws Exception {
        return (Backup) map.get("backup");
    }
}
