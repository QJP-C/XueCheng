package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.mapper.MediaProcessMapper;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.service.MediaFileService;
import com.xuecheng.xuechengplus.base.exception.XueChengPlusException;
import com.xuecheng.xuechengplus.base.model.PageParams;
import com.xuecheng.xuechengplus.base.model.PageResult;
import com.xuecheng.xuechengplus.base.model.RestResponse;
import io.minio.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Mr.M
 * @version 1.0
 * @description TODO
 * @date 2022/9/10 8:58
 */
@Service
@Slf4j
public class MediaFileServiceImpl implements MediaFileService {

    @Autowired
    MediaFilesMapper mediaFilesMapper;

    @Autowired
    MinioClient minioClient;
    //使该对象为代理
    @Autowired
    MediaFileService currentProxy;//自己注入到自己里面 将其变为一个代理对象

    //存储普通文件
    @Value("${minio.bucket.files}")
    private String bucket_mediafiles;

    //存储视频
    @Value("${minio.bucket.videofiles}")
    private String bucket_video;

    @Resource
    MediaProcessMapper mediaProcessMapper;


    /**
     * 媒资列表查询
     * @param companyId
     * @param pageParams          分页参数
     * @param queryMediaParamsDto 查询条件
     * @return
     */
    @Override
    public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {

        //构建查询条件对象
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();
        //分页对象
        Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        // 查询数据内容获得结果
        Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
        // 获取数据列表
        List<MediaFiles> list = pageResult.getRecords();
        // 获取数据总数
        long total = pageResult.getTotal();
        // 构建结果集
        return new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());

    }


    /***
     * @description 获取文件默认存储目录路径 年/月/日/
     * @return java.lang.String
     * @author qjp
     * @date 2023/4/2 15:46
     */
    private String getDefaultFolderPath() {
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        System.out.println(simpleDateFormat.format(new Date()),replace("-","/")+"/");
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd/");
        return new SimpleDateFormat("yyyy/MM/dd/").format(new Date());
    }

    /***
     * @description 根据扩展名获取mimeType
     * @param extension
     * @return java.lang.String
     * @author qjp
     * @date 2023/4/2 15:05
     */
    private String getMimeType(String extension) {
        if (extension == null) {
            extension = "";
        }
        //通过扩展名得到媒体资源类型 mimeType
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;//通用mimeType,字节流
        if (extensionMatch != null) {
            mimeType = extensionMatch.getMimeType();
        }
        return mimeType;
    }

    /***
     * @description 将文件上传到minio
     * @param localFilePath 本地文件路径
     * @param mimeType 媒体文件类型
     * @param bucket 桶名
     * @param objectName 对象名(存放位置)
     * @return boolean
     * @author qjp
     * @date 2023/4/2 15:17
     */
    @Override
    public boolean addMediaFilesToMinIO(String localFilePath, String mimeType, String bucket, String objectName) {
        try {
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                    .bucket(bucket)//桶
                    .filename(localFilePath)//指定本地文件路径
                    //                .object("333.png")//对象名 (直接在桶下存储该文件)
                    .object(objectName)//对象名 (直接在桶的子目录下存储该文件)
                    .contentType(mimeType)//媒体资源类型(需要指定时添加)
                    .build();
            //上传文件
            minioClient.uploadObject(uploadObjectArgs);
            log.debug("上传文件到minio成功,bucket:{},objectName:{}", bucket, objectName);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传文件出错,bucket:{},objectName:{},错误信息{}", bucket, objectName, e.getMessage());
        }
        return false;
    }

    /***
     * @description 获取md5
     * @param file
     * @return java.lang.String
     * @author qjp
     * @date 2023/4/2 16:05
     */
    private String getFileMd5(File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            return DigestUtils.md5Hex(fileInputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * @param companyId           机构id
     * @param fileMd5             文件md5值
     * @param uploadFileParamsDto 上传文件的信息
     * @param bucket              桶
     * @param objectName          对象名称
     * @return com.xuecheng.media.model.po.MediaFiles
     * @description 将文件信息添加到文件表
     * @author Mr.M
     * @date 2022/10/12 21:22
     */
    @Transactional     //在有网络请求的方法中不要加数据库事物 (在极端情况下有可能导致数据库不可用) 因此事物控制放在该方法 不能放在上传方法
    @Override
    public MediaFiles addMediaFilesToDb(Long companyId, String fileMd5, UploadFileParamsDto uploadFileParamsDto, String bucket, String objectName) {
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles == null) {
            mediaFiles = new MediaFiles();
            BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);
            //文件id
            mediaFiles.setId(fileMd5);
            //机构id
            mediaFiles.setCompanyId(companyId);
            //桶
            mediaFiles.setBucket(bucket);
            //file_path
            mediaFiles.setFilePath(objectName);
            //file_id
            mediaFiles.setFileId(fileMd5);
            //url
            mediaFiles.setUrl("/" + bucket + "/" + objectName);
            //上传时间
            mediaFiles.setCreateDate(LocalDateTime.now());
            //状态  1 正常 0 不展示
            mediaFiles.setStatus("1");
            //审核状态   002003 审核已通过
            mediaFiles.setAuditStatus("002003");
            //插入数据库
            int insert = mediaFilesMapper.insert(mediaFiles);
            if (insert <= 0) {
                log.debug("向数据库保存文件信息失败,bucket:{},objectName:{}", bucket, objectName);
                return null;
            }
            //记录待处理的任务
            addWaitingTask(mediaFiles);


            //向MediaProcess插入记录

            log.debug("保存文件信息到数据库成功,{}", mediaFiles.toString());
            return mediaFiles;
        }
        return mediaFiles;
    }

    /**
     * 添加待处理任务
     * @param mediaFiles 媒资文件信息
     */
    private void addWaitingTask(MediaFiles mediaFiles) {
        //获取文件的mimeType
        //1.文件名称
        String filename = mediaFiles.getFilename();
        //2.文件扩展名
        String extension = filename.substring(filename.lastIndexOf("."));
        String mimeType = getMimeType(extension);

        if (mimeType.equals("video/x-msvideo")){//如果是avi视频则写入待处理任务表
            MediaProcess mediaProcess = new MediaProcess();
            BeanUtils.copyProperties(mediaFiles,mediaProcess);
            mediaProcess.setFilePath(mediaFiles.getFilePath());
            mediaProcess.setStatus("1");//未处理
            mediaProcess.setCreateDate(LocalDateTime.now());
            mediaProcess.setFailCount(0);//失败次数
            mediaProcess.setUrl(null);
            mediaProcessMapper.insert(mediaProcess);
        }
        //判断如果是avi视频  写入待处理任务
    }
        /**
         * 上传文件
         * @param companyId 机构id
         * @param uploadFileParamsDto 文件信息
         * @param localFilePath 文件本地路径
         * @return
         */
    @Override
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath,String objectName) {
        File file = new File(localFilePath);
        if (!file.exists()) {
            XueChengPlusException.cast("文件不存在");
        }
        //文件名称
        String filename = uploadFileParamsDto.getFilename();
        //文件扩展名
        String extension = filename.substring(filename.lastIndexOf("."));
        //文件mimeType
        String mimeType = getMimeType(extension);
        //文件的md5值
        String fileMd5 = getFileMd5(file);
        //文件的默认目录
        String defaultFolderPath = getDefaultFolderPath();
        //存储到minio中的对象名(带目录)
        if (StringUtils.isEmpty(objectName)){
            //使用默认的年月日存储
            objectName = defaultFolderPath + fileMd5 + extension;
        }
        //将文件上传到minio
        boolean result = addMediaFilesToMinIO(localFilePath, mimeType, bucket_mediafiles, objectName);
        if (!result){
            XueChengPlusException.cast("上传文件失败！");
        }
        //文件大小
        uploadFileParamsDto.setFileSize(file.length());
        //将文件信息存储到数据库
        MediaFiles mediaFiles = currentProxy.addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, bucket_mediafiles, objectName);
        if (mediaFiles==null){
            XueChengPlusException.cast("文件上传后保存消息失败！");
        }
        //准备返回数据
        UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
        BeanUtils.copyProperties(mediaFiles, uploadFileResultDto);
        return uploadFileResultDto;
    }


    /***
     * @description 检查文件是否存在
     * @param fileMd5
     * @return com.xuecheng.xuechengplusbase.model.RestResponse<java.lang.Boolean>
     * @author qjp
     * @date 2023/4/10 17:32
     */
    @Override
    public RestResponse<Boolean> checkFile(String fileMd5) {
        //先查询数据库
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles != null) {
            //桶
            String bucket = mediaFiles.getBucket();
            //objectName
            String filePath = mediaFiles.getFilePath();
            //如果数据库存在再查询minio
            GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(filePath)//指定下载的对象
                    .build();
            //查询远程服务器所获取到的流对象
            try {
                FilterInputStream inputStream = minioClient.getObject(getObjectArgs);
                if (inputStream != null) {
                    //文件已存在
                    return RestResponse.success(true);//可以继续传
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //文件不存在
        return RestResponse.success(false);
    }

    /**
     * 分块文件上传前的检测
     * @param fileMd5  文件的md5
     * @param chunkIndex  分块序号
     * @return
     */
    @Override
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex) {
        //分块存储路径是: md5 的前两位为两个子目录,子目录里chunk用来存储分块文件

        //根据md5得到分块文件所在目录的路径
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);

        //如果数据库存在再查询minio
        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket(bucket_video)
                .object(chunkFileFolderPath + chunkIndex)//路径加序号
                .build();
        //查询远程服务器所获取到的流对象
        try {
            FilterInputStream inputStream = minioClient.getObject(getObjectArgs);
            if (inputStream != null) {
                //文件已存在
                return RestResponse.success(true);//可以继续传
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //文件不存在
        return RestResponse.success(false);
    }

    /***
     * @description 得到分块文件的目录
     * @param fileMd5
     * @return java.lang.String
     * @author qjp
     * @date 2023/4/10 20:17
     */
    private String getChunkFileFolderPath(String fileMd5) {
        return fileMd5.charAt(0) + "/" + fileMd5.charAt(1) + "/" + fileMd5 + "/" + "chunk" + "/";
    }

    /***
     * @description 上传分块文件
     * @param fileMd5
     * @param chunk
     * @param localChunkFilePath
     * @return com.xuecheng.xuechengplusbase.model.RestResponse
     * @author qjp
     * @date 2023/4/10 17:55
     */
    @Override
    public RestResponse uploadChunk(String fileMd5, int chunk, String localChunkFilePath) {
        //分块文件的路径
        String chunkFilePath = getChunkFileFolderPath(fileMd5) + chunk;
        //获取mimeType
        String mimeType = getMimeType(null);
        //将文件上传至minio
        boolean b = addMediaFilesToMinIO(localChunkFilePath, mimeType, bucket_video, chunkFilePath);
        if (!b) {
            return RestResponse.validfail(false, "上传分块文件失败");
        }
        return RestResponse.success(true);
    }

    /***
     * @description 合并分块
     * @param companyId
     * @param fileMd5
     * @param chunkTotal
     * @param uploadFileParamsDto
     * @return com.xuecheng.xuechengplusbase.model.RestResponse
     * @author qjp
     * @date 2023/4/10 20:33
     */
    @Override
    public RestResponse mergechunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto) {
        //分块文件所在目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //=========找到分块文件调用minio的sdk进行文件合并             从0开始每次加一
        List<ComposeSource> sources = Stream.iterate(0, i -> ++i)
                .limit(chunkTotal)//分块总数
                .map(i -> ComposeSource.builder()
                        .bucket(bucket_video)
                        .object(chunkFileFolderPath+i)
                        .build())
                .collect(Collectors.toList());

        //===========合并
        //源文件名称
        String filename = uploadFileParamsDto.getFilename();
        //扩展名
        String extension = filename.substring(filename.lastIndexOf("."));
        //合并后文件的objectname
        String objectName = getFilePathByMd5(fileMd5, extension);

        //指定合并后的objectname等信息
        ComposeObjectArgs composeObjectArgs = ComposeObjectArgs.builder()
                .bucket(bucket_video)
                .object(objectName)
                .sources(sources)//指定源文件
                .build();

        //合并文件(minio默认分块文件最小为5M 无法更改)
        //如果报错 size1048576 must be greater than5242880,minio默认的分块文件大小5M
        try {
            minioClient.composeObject(composeObjectArgs);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("合并文件出错! bucket:{},objectName:{},错误信息:{}", bucket_video, objectName, e.getMessage());
            return RestResponse.validfail(false, "合并文件异常");
        }

        //============校验合并后的和源文件是否一致
        //先下载合并后的文件
        File file = downloadFileFromMinIO(bucket_video, objectName);

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            //计算合并后的文件的md5值                 把流放在try括号里在执行完后自动关闭
            String mergeFile_md5 = DigestUtils.md5Hex(fileInputStream);
            //比较原始的md5值和合并后的
            if (!fileMd5.equals(mergeFile_md5)) {
                log.error("校验合并文件的md5值不一致,原始文件:{},合并文件:{}", fileMd5, mergeFile_md5);
                return RestResponse.validfail(false, "文件校验失败");
            }
            //文件大小
            uploadFileParamsDto.setFileSize(file.length());
        } catch (Exception e) {
            return RestResponse.validfail(false, "文件校验失败");
        }


        //============将文件信息入库  (非事物方法调用事物方法,为保证事物一致性且不影响效率 应使用代理对象调用)
        MediaFiles mediaFiles = currentProxy.addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, bucket_video, objectName);
        if (mediaFiles == null) {
            return RestResponse.validfail(false, "文件入库失败");
        }
        //清理分块文件
        clearChunkFiles(chunkFileFolderPath, chunkTotal);

        return RestResponse.success(true);
    }

    /***
     * @description 清理分块文件
     * @param chunkFileFolderPath
     * @param chunkTotal
     * @return void
     * @author qjp
     * @date 2023/4/10 21:18
     */
    private void clearChunkFiles(String chunkFileFolderPath, int chunkTotal) {
        Iterable<DeleteObject> objects = Stream.iterate(0, i -> ++i)
                .limit(chunkTotal)//分块总数
                .map(i -> new DeleteObject(chunkFileFolderPath + i))
                .collect(Collectors.toList());
        RemoveObjectsArgs removeObjectsArgs = RemoveObjectsArgs.builder().bucket(bucket_video).objects(objects).build();
        Iterable<Result<DeleteError>> results = minioClient.removeObjects(removeObjectsArgs);
        results.forEach(f ->{
            try {
                DeleteError deleteError = f.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /***
     * @description 根据md5值得到合并后的文件地址
     * @param fileMd5
     * @param fileExt
     * @return java.lang.String
     * @author qjp
     * @date 2023/4/10 20:18
     */
    private String getFilePathByMd5(String fileMd5, String fileExt) {
        return fileMd5.charAt(0) + "/" + fileMd5.charAt(1) + "/" + fileMd5 + "/" + fileMd5 + fileExt;
    }

    /***
     * @description 根据桶和文件路径从minio下载文件
     * @param bucket
     * @param objectName
     * @return java.io.File
     * @author qjp
     * @date 2023/4/10 20:28
     */
    @Override
    public File downloadFileFromMinIO(String bucket, String objectName) {
        File minioFile = null;
        FileOutputStream outputStream = null;
        try {
            InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build());
            //创建临时文件
            minioFile = File.createTempFile("minio", ".merge");
            outputStream = new FileOutputStream(minioFile);
            IOUtils.copy(stream, outputStream);
            return minioFile;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 根据媒资id查询文件信息
     * @param mediaId
     * @return
     */
    @Override
    public MediaFiles getFileById(String mediaId) {
        return mediaFilesMapper.selectById(mediaId);
    }


}