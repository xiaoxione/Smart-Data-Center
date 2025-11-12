package com.briup.env.util;

import com.briup.smart.env.util.Backup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class BackupImpl implements Backup {

    @Override
    public Object load(String filePath, boolean del) throws Exception {
        File file = new File(filePath);
        if(!file.exists()||!file.isFile()){
            System.out.println("备份模块：想要读取的备份文件不存在"+filePath);
            return null;
        }
        if(file.length()==0){
            System.out.println("备份模块：备份文件中无数据可读"+filePath);
            return null;
        }
        Object object=null;
        ObjectInputStream ois = null;
        try{
            ois = new ObjectInputStream(new FileInputStream(filePath));
            object = ois.readObject();
            System.out.println("备份模块：成功读取备份文件"+filePath);
        }catch(Exception e){
            e.printStackTrace();
        }
        if(del){
        boolean delete = file.delete();
        System.out.println("备份模块: 文件" + (delete ? "删除成功" : "删除失败"));
        }
        return object;
    }


    @Override
    public void store(String filePath, Object obj, boolean append) throws Exception {
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath, append))){
            oos.writeObject(obj);
            System.out.println("备份模块: 数据已保存到备份文件中" + filePath);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

