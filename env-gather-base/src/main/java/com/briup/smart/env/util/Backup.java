package com.briup.smart.env.util;

/**
 * Backup接口是物联网数据中心项目备份模块的规范
 */
public interface Backup {
    /**
     * 在保存数据时,在原来文件的基础上进行追加
     */
    boolean STORE_APPEND = true;

    /**
     * 在保存数据时,覆盖原来的文件
     */
    boolean STORE_OVERRIDE = false;

    /**
     * 在读取数据同时,删除备份文件
     */
    boolean LOAD_REMOVE = true;

    /**
     * 在读取数据同时,不删除备份文件
     */
    boolean LOAD_UNREMOVE = false;

    /**
     * 读取备份文件中存储的对象
     *
     * @param filePath 备份文件的路径
     * @param del      读取完备份文件后是否要删除此备份文件，true为删除，false为不删除。
     *                 在调用时推荐使用本接口中定义的静态常量LOAD_REMOVE、LOAD_UNREMOVE
     * @return 备份文件中存储的对象
     */
    Object load(String filePath, boolean del) throws Exception;

    /**
     * 将需要备份的集合对象写入到备份文件
     *
     * @param filePath 备份文件的路径
     * @param obj      将要写入备份文件的对象
     * @param append   在写入时追加还是覆盖，true为追加，false为覆盖,在调用时推荐使用接口中定义的静态常量STORE_APPEND、STORE_OVERRIDE
     */
    void store(String filePath, Object obj, boolean append) throws Exception;
}

