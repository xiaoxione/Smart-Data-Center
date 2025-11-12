package com.briup.smart.env.support;

import java.util.Properties;

/**
 * 如果某模块A， 需要传入外部数据properties(key-value形式)进行初始化赋值
 * 那么这个模块A，就需要实现这个PropertiesAware接口
 * <p>
 * 例如采集模块需要采集的数据文件的路径这一信息，那么采集模块就应该实现PropertiesAware接口，
 * 在init(Properties properties)方法中对数据文件的路径这一变量进行赋值
 */
public interface PropertiesAware {
    /**
     * 对当前模块进行初始化赋值，变量值从properties中获取
     *
     * @param properties 存储了配置信息(例如ip、端口号)的Properties对象
     */
    void init(Properties properties) throws Exception;
}
