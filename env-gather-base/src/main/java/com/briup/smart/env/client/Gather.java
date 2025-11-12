package com.briup.smart.env.client;

import com.briup.smart.env.entity.Environment;

import java.util.Collection;


/**
 * Gather接口是物联网数据中心项目采集模块的规范
 * 该模块对物联网数据中心项目环境信息进行采集
 * 将采集的数据封装成Collection<Environment>集合
 */
public interface Gather {
    /**
     * 使用IO流读取数据文件中的一行行数据，对数据进行解析，封装成Environment类的对象
     * 将所有Environment类的对象保存到集合中
     *
     * @return 存储了若干个Environment对象的集合，Environment对象是对采集数据的封装
     */
    Collection<Environment> gather() throws Exception;
}
