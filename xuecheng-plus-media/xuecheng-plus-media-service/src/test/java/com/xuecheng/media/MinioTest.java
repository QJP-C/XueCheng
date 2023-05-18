package com.xuecheng.media;

import com.alibaba.nacos.common.utils.IoUtils;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import io.minio.*;
import io.minio.errors.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author qjp
 * @version 1.0
 * @description 测试minio的sdk(工具类库)
 * @date 2023/4/2 9:42
 */
public class MinioTest {
    MinioClient minioClient =
            MinioClient.builder()
                    .endpoint("http://192.168.101.65:9000")//地址
                    .credentials("minioadmin", "minioadmin")//账号密码
                    .build();
    /***
    * @description 上传文件
    * @return void
    * @author qjp
    * @date 2023/4/2 10:25
    */

    @Test
    public void test_upload() throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        //通过扩展名得到媒体资源类型 mimeType
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(".mp4");
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;//通用mimeType,字节流
        if (extensionMatch!=null){
            mimeType = extensionMatch.getMimeType();
        }
        //上传文件的参数信息
        UploadObjectArgs testbucket = UploadObjectArgs.builder()
                .bucket("testbucket")//桶
                .filename("D:\\Users\\qjp\\Pictures\\Screenshots\\333.png")//指定本地文件路径
//                .object("333.png")//对象名 (直接在桶下存储该文件)
                .object("test/01/333.png")//对象名 (直接在桶的子目录下存储该文件)
                .contentType(mimeType)//媒体资源类型(需要指定时添加)
                .build();

        //上传文件
        minioClient.uploadObject(testbucket);

//        minioClient.uploadObject(
//                UploadObjectArgs.builder()
//                        .bucket("asiatrip")
//                        .object("asiaphotos-2015.zip")
//                        .filename("/home/user/Photos/asiaphotos.zip")
//                        .build());
    }
    /***
    * @description 删除文件
    * @return void
    * @author qjp
    * @date 2023/4/2 10:29
    */
    @Test
    public void test_remove() throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        //RemoveObjectArgs 删除文件的参数信息
        RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                .bucket("testbucket")
                .object("333.png")//指定删除的对象
                .build();


        //删除文件
        minioClient.removeObject(removeObjectArgs);

    }
    /***
    * @description 查询文件 (从minio中下载文件)
    * @return void
    * @author qjp
    * @date 2023/4/2 10:42
    */
    @Test
    public void test_getFile() throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket("testbucket")
                .object("test/01/333.png")//指定下载的对象
                .build();
        //查询远程服务器所获取到的流对象
        FilterInputStream inputStream = minioClient.getObject(getObjectArgs);
        //指定输出流
        FileOutputStream outputStream = new FileOutputStream(new File("D:\\Users\\qjp\\Pictures\\Screenshots\\33.png"));
        IoUtils.copy(inputStream, outputStream);

        //校验文件的完整性  (对文件的内容进行md5比对)  如果传输过程中有网络丢包 文件内容有丢失 md5不一样
        //String source_md5 = DigestUtils.md5Hex(inputStream);//minio中文件的md5
        String local_md5 = DigestUtils.md5Hex(Files.newInputStream(new File("D:\\Users\\qjp\\Pictures\\Screenshots\\33.png").toPath()));
        String local1_md5 = DigestUtils.md5Hex(Files.newInputStream(new File("D:\\Users\\qjp\\Pictures\\Screenshots\\333.png").toPath()));
        if (local1_md5.equals(local_md5)){
            System.out.println("下载成功");
        }
    }



    //将分块文件上传到minio
    @Test
    public void uploadChunk() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        for (int i = 0; i < 3; i++) {
            //上传文件的参数信息
            UploadObjectArgs testbucket = UploadObjectArgs.builder()
                    .bucket("testbucket")//桶
                    .filename("e://fenkuai//"+i)//指定本地文件路径
//                .object("333.png")//对象名 (直接在桶下存储该文件)
                    .object("chunk/"+i)//对象名 (直接在桶的子目录下存储该文件)
                    .build();

            //上传文件
            minioClient.uploadObject(testbucket);
            System.out.println("上传成功"+i+"成功");
        }
    }


    //调用minio的接口合并
    @Test
    public void testMerge() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

//        List<ComposeSource> sources = new ArrayList<>();
//        for (int i = 0; i < 3; i++) {
//            //指定分块文件的信息
//            ComposeSource composeSource = ComposeSource.builder().bucket("testbucket").object("chunk/" + i).build();
//            sources.add(composeSource);
//        }
        // 换为stream流                                 迭代器      从0索引开始  每次加一     30次             映射为
        List<ComposeSource> sources = Stream.iterate(0, i -> ++i)
                .limit(3)
                .map(i -> ComposeSource.builder()
                        .bucket("testbucket")
                        .object("chunk/" + i)
                        .build())
                .collect(Collectors.toList());

        //指定合并后的objectname等信息
        ComposeObjectArgs composeObjectArgs = ComposeObjectArgs
                .builder()
                .bucket("testbucket")
                .object("merge01.mp4")
                .sources(sources)//指定源文件
                .build();
        //合并文件(minio默认分块文件最小为5M 无法更改)
        minioClient.composeObject(composeObjectArgs);

    }

    //批量清理分块文件







    @Test
    void TestTimeUnilts(){
        System.out.println(new SimpleDateFormat("yyyy/MM/dd/").format(new Date()));
    }
}
