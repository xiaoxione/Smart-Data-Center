package com.briup.env.client;

import com.briup.smart.env.Configuration;
import com.briup.smart.env.client.Gather;
import com.briup.smart.env.entity.Environment;
import com.briup.smart.env.support.ConfigurationAware;
import com.briup.smart.env.support.PropertiesAware;
import com.briup.smart.env.util.Backup;
import com.briup.smart.env.util.Log;

import java.io.File;
import java.io.RandomAccessFile;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

public class GatherBackupImpl implements Gather, PropertiesAware, ConfigurationAware {
    //添加日志对象
    private Log log;// = new LogImpl();
    // 备份对象
    private Backup backup;// = new BackupImpl();
    //需要采集的文件路径
    private String gatherFilePath;// = "env-gather-impl/src/main/resources/data-file-simple";
    //采集备份文件：存储已经读取文件字节数
    private String gatherBackupFilePath;// = "env-gather-impl/src/main/resources/gather-backup.txt";

    //给属性进行赋值(从prop对象)
    @Override
    public void init(Properties prop) throws Exception {
        gatherFilePath = prop.getProperty("gatherFilePath");
        gatherBackupFilePath = prop.getProperty("gatherBackupFilePath");
    }

    //给当前对象中包含的其他模块对象 赋值
    @Override
    public void setConfiguration(Configuration configuration) throws Exception {
        log = configuration.getLogger();
        backup = configuration.getBackup();
    }

    @Override
    public Collection<Environment> gather() {
        //一、从备份文件gather-backup.txt获取上一次采集文件字节数
        long offset = 0;
        File file = new File(gatherBackupFilePath);
        if(file.exists() && file.isFile() && file.length() > 0) {
            try {
                Object obj = backup.load(gatherBackupFilePath,Backup.LOAD_REMOVE);
                //offset = dis.readLong();
                offset = (Long)obj;
                log.debug("上一次偏移量: " + offset);
                //调整偏移量到下一行 (换行符占2个字节)
                offset += 2;
            } catch (Exception e) {
                //e.printStackTrace();
                log.warn("采集模块中load产生异常: " + e.toString());
            }
        }

        RandomAccessFile raf = null;
        ArrayList<Environment> list = new ArrayList<>();

        try {

            //二、采集功能实现
            raf = new RandomAccessFile(gatherFilePath, "r");
            //调整文件偏移量
            raf.seek(offset);

            String str = null;
            while ((str = raf.readLine()) != null) {
                log.debug("line: " + str);
                //原始数据案例:
                //100|101|2|16|1|3|5d706fbc02|1|1516323604876
                //字符串分割操作，按 | 进行分割，|在正则表达式中有特殊的含义，需要特别处理
                String[] arr = str.split("[|]"); // \\|
                //将数据封装到Environment对象中
                Environment environment = new Environment();
                //设置发送端id
                environment.setSrcId(arr[0]);
                //设置树莓派系统id
                environment.setDesId(arr[1]);
                //设置实验箱区域模块id(1-8)
                environment.setDevId(arr[2]);
                //设置模块上传感器地址
                environment.setSensorAddress(arr[3]);
                //设置传感器个数 String转int
                environment.setCount(Integer.parseInt(arr[4]));
                //设置发送指令标号 3表示接收数据 16表示发送命令
                environment.setCmd(arr[5]);
                //设置状态(默认1,表示成功) String类型转换为int类型
                environment.setStatus(Integer.parseInt(arr[7]));
                //设置采集时间 时间戳 String转long
                environment.setGatherDate(new Timestamp(Long.parseLong(arr[8])));

                //根据arr[3](传感器地址)计算 name、环境值data
                switch (arr[3]) {
                    case "16":
                        /*
                         表示温度和湿度数据。当数据为温度和湿度时，环境数据arr[6]（例:5d806ff802）的前两个字节表示温度(即前4位数,例:5d80)
                         中间的两个字节表示湿度(即中间的4位数，例:6ff8)
                         */
                        //处理温度，获取前2个字节，按照公式求温度值
                        environment.setName("温度");
                        String temperature = arr[6].substring(0, 4);
                        //16进制转换为10进制
                        int t = Integer.parseInt(temperature, 16);
                        environment.setData((t * (0.00268127F)) - 46.85F);
                        list.add(environment);

                        //表示湿度,中间两个字节是湿度
                        Environment environment1 = cloneEnvironment(environment);
                        environment1.setName("湿度");
                        String humidity = arr[6].substring(4, 8);
                        int h = Integer.parseInt(humidity, 16);
                        environment1.setData((h * 0.00190735F) - 6);
                        list.add(environment1);
                        break;
                    case "256":
                        //光照强度数据，前两个字节是数据值，剩余字节不用管
                        environment.setName("光照强度");
                        environment.setData(Integer.parseInt(arr[6].substring(0, 4), 16));
                        list.add(environment);
                        break;
                    case "1280":
                        //二氧化碳数据，前两个字节是数据值，剩余字节不用管
                        environment.setName("二氧化碳浓度");
                        environment.setData(Integer.parseInt(arr[6].substring(0, 4), 16));
                        list.add(environment);
                        break;
                    default:
                        log.warn("数据格式错误: " + str);
                        break;
                }
            }
        }catch (Exception e) {
            //e.printStackTrace();
            //log.warn("数据采集异常：" + e.getMessage(), e);
            log.warn(e.toString());
        }finally {
            //三、将本次采集数据字节数，备份到gather-backup.txt
            log.info("成功采集数据数量：" + list.size());
            //try(DataOutputStream dos =
            //            new DataOutputStream(new FileOutputStream(backupFilePath))) {
            try {
                //获取文件当前偏移量
                Long currOffset = raf.getFilePointer();
                //将其写入备份文件gather-backup.txt
                //dos.writeLong(currOffset);
                backup.store(gatherBackupFilePath,currOffset,Backup.STORE_OVERRIDE);
                log.info("采集备份数据长度成功，currOffset: " + currOffset);
                if(raf != null)
                    raf.close();
            } catch (Exception e) {
                //log.warn(e.getMessage());
                log.warn("备份数据长度时出现异常：" + e.toString());
            }
        }

        return list;
    }

    /**
     * 克隆Environment对象。对于属性，除了name(环境种类名称)和data(环境值)都会复制
     *
     * @param e 被克隆的Environment对象
     * @return 克隆得到的的Environment对象
     */
    private Environment cloneEnvironment(Environment e) {
        return new Environment(null, e.getSrcId(), e.getDesId(), e.getDevId(), e.getSensorAddress(), e.getCount(), e.getCmd(), e.getStatus(), 0, e.getGatherDate());
    }

}


