package com.xuecheng.media.service.impl;

import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.mapper.MediaProcessHistoryMapper;
import com.xuecheng.media.mapper.MediaProcessMapper;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.model.po.MediaProcessHistory;
import com.xuecheng.media.service.MediaFileProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 任务视频处理接口实现
 */
@Slf4j
@Service
public class MediaFileProcessServiceImpl implements MediaFileProcessService {
    @Resource
    MediaProcessMapper mediaProcessMapper;

    @Resource
    MediaFilesMapper mediaFilesMapper;

    @Resource
    MediaProcessHistoryMapper mediaProcessHistoryMapper;
    /**
     * @description 获取待处理任务
     * @param shardIndex 分片序号
     * @param shardTotal 分片总数
     * @param count 获取记录数
     * @return
     */
    public List<MediaProcess> getMediaProcessList(int shardIndex, int shardTotal, int count){
        return mediaProcessMapper.selectListByShardIndex(shardTotal, shardIndex, count);
    }

    /**
     * 抢锁（乐观锁）
     * @param id 任务id
     * @return
     */
    public boolean startTask(long id) {
        int result = mediaProcessMapper.startTask(id);
        return result > 0;
    }

    /**
     * 保存任务结果
     * @param taskId  任务id
     * @param status 任务状态
     * @param fileId  文件id
     * @param url url
     * @param errorMsg 错误信息
     */
    @Override
    public void saveProcessFinishStatus(Long taskId, String status, String fileId, String url, String errorMsg) {
        //1、要更新的任务
        MediaProcess mediaProcess = mediaProcessMapper.selectById(taskId);
        if (mediaProcess == null){
            return;
        }
        //2、如果任务执行失败
        if (status.equals("3")){
            //2.1更新MediaProcess表的状态
            mediaProcess.setStatus("3");
            mediaProcess.setFailCount(mediaProcess.getFailCount()+1);//失败次数加一
            mediaProcess.setErrormsg(errorMsg);
            mediaProcessMapper.updateById(mediaProcess);
            //TODO mediaProcessMapper.update()更高效
            return;
        }

        //3、如果任务执行成功
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileId);
        //更新Media_file表中的url
        mediaFiles.setUrl(url);
        mediaFilesMapper.updateById(mediaFiles);
        //3.2 更新MediaProcess表的状态
        mediaProcess.setStatus("2");
        mediaProcess.setFinishDate(LocalDateTime.now());
        mediaProcess.setUrl(url);
        mediaProcessMapper.updateById(mediaProcess);
        //3.3 将MediaProcess表记录插入到MediaProcessHistory表
        MediaProcessHistory mediaProcessHistory = new MediaProcessHistory();
        BeanUtils.copyProperties(mediaFiles,mediaProcessHistory);
        mediaProcessHistory.setFinishDate(mediaProcess.getFinishDate());
        mediaProcessHistoryMapper.insert(mediaProcessHistory);
        //3.4 从MediaProcess表删除记录
        mediaProcessMapper.deleteById(taskId);

    }

}



