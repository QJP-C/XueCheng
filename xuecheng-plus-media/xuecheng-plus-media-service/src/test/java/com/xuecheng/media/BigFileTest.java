package com.xuecheng.media;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.List;

/**
 * @author qjp
 * @version 1.0
 * @description 测试大文件上传方法
 * @date 2023/4/3 11:27
 */
public class BigFileTest {
    //分块
    @Test
    public void testChunk() throws IOException {
        //源文件
        File sourceFile = new File("D:\\Users\\qjp\\Documents\\WeChat Files\\wxid_5cs21hnhndpw22\\FileStorage\\Video\\2022-09\\0f6f56ba709d4ba5c1b6619d52e46f74.mp4");
        //分块文件存储路径
        String chunkFilePath = "E:\\fenkuai\\";
        //分块文件的大小
        int chunkSize = 1024 * 1024 * 5;
        //分块文件的个数
        int chunkNum = (int) Math.ceil(sourceFile.length() * 1.0 / chunkSize);
        //使用流从源文件读数据,向分块文件中写数据                              只读
        RandomAccessFile raf_r = new RandomAccessFile(sourceFile, "r");
        //缓存区
        byte[] bytes = new byte[1024];
        for (int i = 0; i < chunkNum; i++) {
            File chunkFile = new File(chunkFilePath + i);
            //分块文件写入流
            RandomAccessFile raf_rw = new RandomAccessFile(chunkFile, "rw");
            int len = -1;
            while ((len = raf_r.read(bytes)) != -1){
                raf_rw.write(bytes,0,len);
                if (chunkFile.length()>=chunkSize){
                    break;
                }
            }
            raf_rw.close();//关闭写入流
        }
        raf_r.close();//关闭读流
    }

    //合并
    @Test
    public void testMerge() throws IOException {
        //源文件
        File sourceFile = new File("D:\\FFWallpaper\\custom\\风之女神\\video.mp4");
        //块文件目录
        File chunkFolder = new File("e:/fenkuai/");
        //合并后的文件
        File mergeFile = new File("D:\\FFWallpaper\\custom\\风之女神\\video2.mp4");

        //取出所有分块文件 (存入数组)
        File[] files = chunkFolder.listFiles();

        //对分块文件排序
        //先将数组转成list
        List<File> fileList = Arrays.asList(files);

        //重写比较器 排序
        fileList.sort((o1, o2) -> {
            return Integer.parseInt(o1.getName()) - Integer.parseInt(o2.getName());//升序排列
        });

        //向合并文件写入的流
        RandomAccessFile raf_rw = new RandomAccessFile(mergeFile, "rw");

        //缓存区
        byte[] bytes = new byte[1024];

        //遍历分块文件,向合并的文件写入
        for (File file : fileList) {
            RandomAccessFile raf_r = new RandomAccessFile(file, "r");//读分块的流
            int len = -1;
            while ((len = raf_r.read(bytes))!=-1){
                raf_rw.write(bytes,0,len);
            }
            raf_r.close();
        }
        raf_rw.close();


        //合并完成后对合并的文件校验
        FileInputStream fileInputStream_merge = new FileInputStream(mergeFile);
        FileInputStream fileInputStream_source = new FileInputStream(sourceFile);
        String md5_merge = DigestUtils.md5Hex(fileInputStream_merge);
        String md5_source = DigestUtils.md5Hex(fileInputStream_source);
        if (md5_merge.equals(md5_source)){ // 如果二者的MD5相等
            System.out.println("文件合并成功!");
        }
    }

}
