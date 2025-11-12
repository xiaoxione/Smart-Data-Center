package com.briup.smart.env.support;

import com.briup.smart.env.Configuration;

/**
 * 如果某模块A， 需要传入配置模块以进行支持
 * 那么这个模块A，就需要实现这个ConfigurationAware接口
 * <p>
 * 例如，采集模块需要备份模块的支持，那么可以在采集模块中传入配置模块，在通过配置模块获取备份模块
 */
public interface ConfigurationAware {
    /**
     * 可通过参数configuration的getXxx()方法获取需要的模块的对象
     *
     * @param configuration Configuration接口的实现类对象
     */
    void setConfiguration(Configuration configuration) throws Exception;
}
