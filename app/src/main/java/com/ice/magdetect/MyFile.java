package com.ice.magdetect;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.text.SimpleDateFormat;

public class MyFile {
    private File file;

    public MyFile(Context context,String name){
        String addr=context.getExternalFilesDir(null)+"/";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        addr+=  formatter.format(System.currentTimeMillis());
        file=new File(addr+"_"+name+".txt");
    }

    public void CreateFile(){
        if(file.exists()){
            file.delete();
        }

        try {
            file.createNewFile();
            // 获取文件的输出流对象
            FileOutputStream outStream = new FileOutputStream(file);
            // 获取字符串对象的byte数组并写入文件流
            outStream.write("Acc:".getBytes());
            // 最后关闭文件输出流
            outStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void WriteIntoFile(String s){
        FileWriter writer;
        try {
            writer = new FileWriter(file, true);
            writer.write(s + ",");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Cancel(){
        file=null;
    }
}
