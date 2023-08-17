package com.xuecheng.media.service.jobHandler;

import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.service.MediaFileProcessService;
import com.xuecheng.media.service.MediaFileService;
import com.xuecheng.xuechengplus.base.utils.Mp4VideoUtil;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 任务处理类
 */
@Component
@Slf4j
public class VideoTask {

    @Resource
    MediaFileProcessService mediaFileProcessService;
    @Resource
    MediaFileService mediaFileService;
    //ffmpeg路径
    @Value("${videoprocess.ffmpegpath}")
    private String ffmpegpath;

    /**
     * 2、视频处理任务
     */
    @XxlJob("videoJobHandler")
    public void videoJobHandler() throws Exception {

        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();//执行器的序号  （从0开始）
        int shardTotal = XxlJobHelper.getShardTotal();//执行器的总数

        //确定 cpu 核心数 ()
        int processors = Runtime.getRuntime().availableProcessors();
        //1.查询待处理任务
        List<MediaProcess> mediaProcessList = mediaFileProcessService.getMediaProcessList(shardIndex, shardTotal, processors);

        //任务数量
        int size = mediaProcessList.size();
        log.debug("取到的视频处理任务数：{}",size);
        if (size<=0){
            return;
        }
        //创建一个线程池
        ExecutorService executorService = Executors.newFixedThreadPool(size);
        //使用计数器
        CountDownLatch countDownLatch = new CountDownLatch(size);
        mediaProcessList.forEach(mediaProcess -> {
            //将任务加入线程池
            executorService.execute(() -> {
                try {//任务执行逻辑
                    //任务id
                     Long taskId = mediaProcess.getId();
                    //文件的id就是md5值
                    String fileId = mediaProcess.getFileId();
                    //2.开启任务
                    boolean b = mediaFileProcessService.startTask(taskId);
                    if (!b) {
                        log.debug("抢占任务失败，任务id:{}", taskId);
                        return;
                    }
                    //下载minio视频到本地
                    //桶
                    String bucket = mediaProcess.getBucket();
                    //objectName
                    String objectName = mediaProcess.getFilePath();
                    File file = mediaFileService.downloadFileFromMinIO(bucket, objectName);
                    if (file == null) {
                        log.debug("下载视频出错，任务id:{}，bucket:{}，filePath:{}", taskId, bucket, objectName);
                        //保存任务处理失败的结果
                        mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, "下载视频到本地失败");
                        return;
                    }

                    //源avi视频的路径
                    String video_path = file.getAbsolutePath();
                    //转换后mp4文件的名称
                    String mp4_name = fileId + ".mp4";
                    //先创建一个临时文件，作为转换后的文件
                    File mp4file = null;
                    try {
                        mp4file = File.createTempFile("minio", ".mp4");
                    } catch (IOException e) {
                        log.debug("创建临时文件信息异常，{}", e.getMessage());
                        //保存任务处理失败的结果
                        mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, "创建临时文件信息异常");
                        return;
                    }
                    //转换后mp4文件的路径
                    String mp4_path = mp4file.getAbsolutePath();
                    //创建工具类对象
                    Mp4VideoUtil videoUtil = new Mp4VideoUtil(ffmpegpath, video_path, mp4_name, mp4_path);
                    //开始视频转换，成功将返回success
                    //3.执行视频转码  成功将返回success,失败返回失败原因
                    String result = videoUtil.generateMp4();
                    if (!result.equals("success")) {
                        log.debug("视频转码失败，原因：{},bucket：{}，objectName:{}", result, bucket, objectName);
                        mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, result);
                        return;
                    }
                    //4.上传minio
                    boolean b1 = mediaFileService.addMediaFilesToMinIO(mp4file.getAbsolutePath(), "video/mp4", bucket, objectName);
                    if (!b1) {
                        log.debug("上传MP4到minio失败，taskId:{}", taskId);
                        mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, result);
                        return;
                    }

                    //mp4文件的url
                    String url = getFilePath(fileId, ".mp4");

                    //更新任务状态为成功
                    mediaFileProcessService.saveProcessFinishStatus(taskId, "2", fileId, url, "创建临时文件信息异常");
                }finally { //不论是否执行成功 走哪个分支都减一
                    //计数器减一
                    countDownLatch.countDown();

                }

            });
        });

        //阻塞 (等所有任务结束  为0时 再完成这个方法)  指定一个最大限度的等待时间   （完成所有任务的时间）
        countDownLatch.await(30, TimeUnit.MINUTES);

    }
    private String getFilePath(String fileMd5,String fileExt){
        return   fileMd5.charAt(0) + "/" + fileMd5.charAt(1) + "/" + fileMd5 + "/" +fileMd5 +fileExt;
    }

}
