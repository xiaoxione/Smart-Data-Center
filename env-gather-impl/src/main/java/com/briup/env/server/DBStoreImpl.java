package com.briup.env.server;

import com.briup.env.util.JdbcUtils;
import com.briup.smart.env.entity.Environment;
import com.briup.smart.env.server.DBStore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;

public class DBStoreImpl implements DBStore {
    //多表入库
    @Override
    public void saveDB(Collection<Environment> collection) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
//实际入库条数
        int saveCount = 0;
        try {
//1 2.获取数据库连接
            conn = JdbcUtils.getConnection();
            conn.setAutoCommit(false);
// 准备计数器
            int count = 0;
            int preDay = -1;
            int currDay = -1;
            for(Environment env : collection) {
//获取采集天
                Timestamp gatherDate = env.getGatherDate();
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(gatherDate.getTime());
                currDay = cal.get(Calendar.DAY_OF_MONTH);
                if(preDay == -1 || preDay != currDay) {
//如果读取到新的一天，则将前一个pstmt对象提交
                    if(preDay != -1) {
                        pstmt.executeBatch();
                        conn.commit();
                        saveCount += count;
                        count = 0;
                        pstmt.close();
                        System.out.println("提交事务,saveCount: " + saveCount);
                    }
//3.获取pstmt对象
                    String sql = "insert into e_detail_"+currDay+" (name,srcId,desId,devId,sersorAddress,count,cmd,status,data,gather_date) " +
                    "values(?,?,?,?,?,?,?,?,?,?)";
                    pstmt = conn.prepareStatement(sql);
                    System.out.println("创建新pstmt: " + sql);
                }
//4.1 设置?值
                pstmt.setString(1, env.getName());
                pstmt.setString(2, env.getSrcId());
                pstmt.setString(3, env.getDesId());
                pstmt.setString(4, env.getDevId());
                pstmt.setString(5, env.getSensorAddress());
                pstmt.setInt(6, env.getCount());
                pstmt.setString(7, env.getCmd());
                pstmt.setFloat(8, env.getData());
                pstmt.setInt(9, env.getStatus());
                pstmt.setTimestamp(10, env.getGatherDate());
                count++;
//4.2 添加到批处理
                pstmt.addBatch();
//4.3 执行插入操作
                if(count % 3 == 0) {
                    pstmt.executeBatch();
//提交事务
                    conn.commit();
//记录实际入库数据
                    saveCount += count;
//重置计数器为0
                    count = 0;
                    System.out.println("提交事务,saveCount: " + saveCount);
                }
//当前数据处理完成，记录当前天数
                preDay = currDay;
            }
//4.4 出循环再次执行批处理
            pstmt.executeBatch();
//提交事务
            conn.commit();
//记录实际入库数据
            saveCount += count;
            System.out.println("成功入库数据条数：" + saveCount);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JdbcUtils.close(pstmt,conn);
        }
    }
}
