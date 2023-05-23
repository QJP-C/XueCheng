package com.xuecheng.media.api;


import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFileService;
import com.xuecheng.xuechengplus.base.model.PageParams;
import com.xuecheng.xuechengplus.base.model.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @author Mr.M
 * @version 1.0
 * @description 媒资文件管理接口
 * @date 2022/9/6 11:29
 */
@Api(value = "媒资文件管理接口", tags = "媒资文件管理接口")
@Slf4j
@RestController
public class MediaFilesController {


    @Autowired
    MediaFileService mediaFileService;

    @ApiOperation("媒资列表查询接口")
    @PostMapping("/files")
    public PageResult<MediaFiles> list(PageParams pageParams, @RequestBody QueryMediaParamsDto queryMediaParamsDto) {
        Long companyId = 1232141425L;
        return mediaFileService.queryMediaFiels(companyId, pageParams, queryMediaParamsDto);
    }

    /***
    * @description TODO
    * @param filedata 
    * @return com.xuecheng.media.model.dto.UploadFileResultDto
    * @author qjp
    * @date 2023/4/2 15:19
    */
    @ApiOperation("上传文件")
    @RequestMapping(value = "/upload/coursefile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)//指定消费者（java后端）接收的类型
    @ResponseBody
    public UploadFileResultDto upload(@RequestPart("filedata")MultipartFile filedata) throws IOException {
        //准备上传文件信息
        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        //原始文件名称
        uploadFileParamsDto.setFilename(filedata.getOriginalFilename());
        //文件大小
        uploadFileParamsDto.setFileSize(filedata.getSize());
        //文件类型
        uploadFileParamsDto.setFileType("001001");
        //接受到文件
        //创建一个临时文件
        File tempFile = File.createTempFile("minio", ".temp");
        filedata.transferTo(tempFile);

        //机构id
        Long companyId = 1232141425L;
        //文件路径
        String localFilePath = tempFile.getAbsolutePath();

        return mediaFileService.uploadFile(companyId,uploadFileParamsDto,localFilePath);

    }

}
