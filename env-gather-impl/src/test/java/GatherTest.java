import com.briup.env.util.BackupImpl;
import com.briup.smart.env.util.Backup;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

public class GatherTest {
    @Test
    public void test01() throws Exception {
//IO流绑定解析文件
//env-gather-impl\src\main\resources\data-file-simple
        BufferedReader br = new BufferedReader(new FileReader("src/data-file-simple"));
        System.out.println(br);
//读取一行数据
        String line = br.readLine();
        System.out.println("read: " + line);
        if(br != null)
            br.close();
    }

    @Test
    public void test02() {
        String s1 = "100|101|2|16|1|3|5d806ff802|1|1516323615936";
//注意：| 是特殊字符，需要去除其特殊含义：[|] 或 \\|
        String[] split = s1.split("[|]");
        System.out.println("length: " + split.length);
        System.out.println(Arrays.toString(split));
    }

    @Test
    public void test03() {
//温湿度数据解析
        String s1 = "5d806ff802";
        String substring = s1.substring(0, 4);
        System.out.println(substring);
    }

    @Test
    public void test4() {
//将16进制的字符串 转换为 int值
        int i = Integer.parseInt("5d80", 16);
        System.out.println(i);
    }

    //store功能测试
    @Test
    public void test05() throws Exception {
        ArrayList<String> list = new ArrayList<>();
        list.add("hello");
        list.add("world");
        list.add("briup");
        Backup backup = new BackupImpl();
        backup.store("src/test/a.txt",list,true);
        System.out.println("备份数据完成！");
    }
    //load功能测试
    @Test
    public void test06() throws Exception {
        Backup backup = new BackupImpl();
        Object obj = backup.load("src/test/a.txt",true);
        System.out.println("读取数据完成！");
        ArrayList<String> list = (ArrayList<String>) obj;
        System.out.println(list);
    }
}

