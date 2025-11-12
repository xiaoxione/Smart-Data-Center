package com.briup.smart.env.server;

import com.briup.smart.env.entity.Environment;

import java.util.Collection;

/**
 * DBStore接口是物联网数据中心项目入库模块的规范
 * 该模块负责对Environment集合进行持久化操作
 */
public interface DBStore {
    /**
     * 將集合对象 collection 中的一条条数据保存到数据库中
     *
     * @param collection 存储了Environment类的对象的集合
     */
    void saveDB(Collection<Environment> collection) throws Exception;
}
