package com.xuecheng.content.service.jobhandler;

import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MessageProcessAbstract;
import com.xuecheng.messagesdk.service.MqMessageService;
import com.xuecheng.xuechengplus.base.exception.XueChengPlusException;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;

/**
 * 课程发布任务类
 */
@Slf4j
@Component
public class CoursePublishTask extends MessageProcessAbstract {
    @Resource
    CoursePublishService coursePublishService;

    @XxlJob("CoursePublishJobHandler")
    public void coursePublishJobHandler() throws Exception {
        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        log.debug("shardIndex="+shardIndex+",shardTotal="+shardTotal);
        //调用抽象类的方法来执行任务
        //参数:分片序号、分片总数、消息类型、一次最多取到的任务数量、一次任务调度执行的超时时间
        process(shardIndex,shardTotal,"course_publish",30,60);
    }


    /**
     * 执行课程发布任务的逻辑
     * 如果此方法抛出了异常，说明此任务执行失败
     * @param mqMessage 执行任务内容
     * @return
     */
    @Override
    public boolean execute(MqMessage mqMessage) {
        //从mqMessage中拿到课程id
        Long courseId = Long.parseLong(mqMessage.getBusinessKey1());
        //课程静态化上传到minio
        generateCourseHtml(mqMessage,courseId);

        //向ES写索引数据
        saveCourseIndex(mqMessage,courseId);
        //向Redis写缓存



        //返沪true表述任务完成
        return true;
    }

    /**
     * 生成课程静态化页面并上传至文件系统
     * @param mqMessage
     * @param courseId
     */
    public void generateCourseHtml(MqMessage mqMessage, long courseId) {
        //消息id
        Long taskId = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();

        //做任务幂等性处理
        //查询数据库取出该阶段的执行状态
        int stageOne = mqMessageService.getStageOne(taskId);
        if (stageOne>0){
            log.debug("课程静态化任务完成，无需处理...");
            return;
        }

        //开始进行课程静态化
        File file = coursePublishService.generateCourseHtml(courseId);
        if (file == null){
            XueChengPlusException.cast("生成的静态页面为空");
        }
        //将html上传minio
        coursePublishService.uploadCourseHtml(courseId,file);



        //任务处理完成 将该阶段任务状态改为完成（stageOne）
        mqMessageService.completedStageOne(taskId);

    }

    /**
     * 保存课程索引信息
     * @param mqMessage
     * @param courseId
     */
    public void saveCourseIndex(MqMessage mqMessage,long courseId){
        //任务id
        Long taskId = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();
        //取出第二个阶段的状态
        int stageTwo = mqMessageService.getStageTwo(taskId);
        //任务幂等性 判断
        if (stageTwo>0){
            log.debug("课程索引信息已写入，无需执行...");
            return;
        }

        //查询课程信息，调用搜索服务添加索引

        //完成本阶段的任务
        mqMessageService.completedStageTwo(taskId);


    }

}
