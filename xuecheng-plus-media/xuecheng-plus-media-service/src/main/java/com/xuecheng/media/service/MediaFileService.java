package com.xuecheng.media.service;


import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.xuechengplus.base.model.PageParams;
import com.xuecheng.xuechengplus.base.model.PageResult;
import com.xuecheng.xuechengplus.base.model.RestResponse;

import java.io.File;

/**
 * @author Mr.M
 * @version 1.0
 * @description 媒资文件管理业务类
 * @date 2022/9/10 8:55
 */
public interface MediaFileService {

    /**
     * @param pageParams          分页参数
     * @param queryMediaParamsDto 查询条件
     * @return com.xuecheng.base.model.PageResult<com.xuecheng.media.model.po.MediaFiles>
     * @description 媒资文件查询方法
     * @author Mr.M
     * @date 2022/9/10 8:57
     */
    public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);

    /***
     * @description 上传文件
     * @param companyId 机构id
     * @param uploadFileParamsDto 文件信息
     * @param localFilePath 文件本地路径
     * @return com.xuecheng.media.model.dto.UploadFileParamsDto
     * @author qjp
     * @date 2023/4/2 15:00
     */
    UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath,String objectName);

    /**
     * @description 将文件上传到minio
     * @param localFilePath 本地文件路径
     * @param mimeType 媒体文件类型
     * @param bucket 桶名
     * @param objectName 对象名(存放位置)
     * @return boolean
     * @author qjp
     * @date 2023/4/2 15:17
     */
    boolean addMediaFilesToMinIO(String localFilePath, String mimeType, String bucket, String objectName);

    /***
    * @description 存入数据库
    * @param companyId
     * @param fileMd5
     * @param uploadFileParamsDto
     * @param bucket
     * @param objectName
    * @return com.xuecheng.media.model.po.MediaFiles
    * @author qjp
    * @date 2023/4/2 21:46
    */
    public MediaFiles addMediaFilesToDb(Long companyId, String fileMd5, UploadFileParamsDto uploadFileParamsDto, String bucket, String objectName);

    /**
     * @description 检查文件是否存在
     * @param fileMd5 文件的md5
     * @return com.xuecheng.base.model.RestResponse<java.lang.Boolean> false不存在，true存在
     * @author Mr.M
     * @date 2022/9/13 15:38
     */
    public RestResponse<Boolean> checkFile(String fileMd5);

    /**
     * @description 检查分块是否存在
     * @param fileMd5  文件的md5
     * @param chunkIndex  分块序号
     * @return com.xuecheng.base.model.RestResponse<java.lang.Boolean> false不存在，true存在
     * @author Mr.M
     * @date 2022/9/13 15:39
     */
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex);

    /**
     * 上传分块
     * @param fileMd5
     * @param chunk
     * @param localChunkFilePath
     * @return
     */
    public RestResponse uploadChunk(String fileMd5,int chunk,String localChunkFilePath);

    /**
     * @description 合并分块
     * @param companyId  机构id
     * @param fileMd5  文件md5
     * @param chunkTotal 分块总和
     * @param uploadFileParamsDto 文件信息
     * @return com.xuecheng.base.model.RestResponse
     * @author Mr.M
     * @date 2022/9/13 15:56
     */
    public RestResponse mergechunks(Long companyId,String fileMd5,int chunkTotal,UploadFileParamsDto uploadFileParamsDto);

    /**
     * 根据桶和文件路径从minio下载文件
     * @param bucket
     * @param objectName
     * @return
     */
    public File downloadFileFromMinIO(String bucket, String objectName);

    /**
     * 根据媒资id查询文件信息
     * @param mediaId
     * @return
     */
    MediaFiles getFileById(String mediaId);
}
