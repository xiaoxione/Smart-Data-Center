package com.briup.env.util;


import com.briup.smart.env.util.Log;
import org.apache.log4j.Logger;

public class LogImpl implements Log {
    //获取Log4J日志对象

    Logger logger = Logger.getLogger(LogImpl.class);

    @Override
    public void debug(String message) {
        logger.debug(message);
    }

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void warn(String message) {
        logger.warn(message);
    }

    @Override
    public void error(String message) {
        logger.error(message);
    }

    @Override
    public void fatal(String message) {
        logger.fatal(message);
    }

}
