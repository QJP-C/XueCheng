package com.xuecheng.media.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qjp
 * @version 1.0
 * @description minio配置类
 * @date 2023/4/2 11:22
 */
@Configuration
public class MinioConfig {
    //从nacos读取的配置文件中的属性
    @Value("${minio.endpoint}")
    private String endpoint;
    @Value("${minio.accessKey}")
    private String accessKey;
    @Value("${minio.secretKey}")
    private String secretKey;

    @Bean
    public MinioClient minioClient() {
        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint(endpoint)//地址
                        .credentials(accessKey, secretKey)//账号密码
                        .build();
        return minioClient;
    }

}
