package netty;

import java.io.*;

/**
 * Created by hzyouzhihao on 2016/9/12.
 */
public class Test {

    public static void main(String[] args) throws Exception {
        File subDir = new File("D:\\java\\workpace\\yanxuan\\Netty\\src\\main\\java\\netty\\example\file");
//        for (File subDir : dir.listFiles()) {
        for (File file : subDir.listFiles()) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            StringBuilder sb = new StringBuilder();
            String content;
            while ((content = reader.readLine()) != null) {
                if (content.contains("package io.netty.example")) {
                    content.replace("io.", "");
                }
                sb.append(content + "\r\n");
            }
            reader.close();
            writer.write(sb.toString());
        }
//        }
    }

}
