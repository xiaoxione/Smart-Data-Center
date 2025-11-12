import org.apache.log4j.Logger;
import org.junit.Test;

public class Log4JTest {
    //Logger logger = Logger.getRootLogger();
//推荐用法
    Logger logger = Logger.getLogger(Log4JTest.class);
    @Test
    public void test01() {
//使用默认的配置信息，不需要写log4j.properties
//BasicConfigurator.configure();
//设置日志输出级别为info，这将覆盖配置文件中设置的级别
//logger.setLevel(Level.INFO);
        System.out.println("原来的日志输出");
//下面的消息将被输出
        logger.debug("this is an debug");
        logger.info("this is an info");
        logger.warn("this is a warn");
        logger.error("this is an error");
        logger.fatal("this is a fatal");
    }
    @Test
    public void test02() {
        try {
// 代码块可能会抛出异常
            int result = 10 / 0;
            System.out.println("Result: " + result);
        } catch (Exception e) {
// 记录异常信息
            logger.error(e);
            System.out.println("------------");
//异常日志推荐用法
            logger.warn("数据运算异常: " + e.getMessage(), e);
        }
        logger.info("main end...");
    }
}