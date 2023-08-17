package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanService;
import com.xuecheng.xuechengplus.base.exception.XueChengPlusException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author qjp
 * @version 1.0
 * @description TODO
 * @date 2023/3/10 17:53
 */
@Service
public class TeachplanServiceImpl implements TeachplanService {
    @Resource
    TeachplanMapper teachplanMapper;
    @Resource
    TeachplanMediaMapper teachplanMediaMapper;

    /***
    * @description 根据id查询课程计划信息
    * @param courseId
    * @return java.util.List<com.xuecheng.content.model.dto.TeachplanDto>
    * @author qjp
    * @date 2023/3/10 19:15
    */
    @Override
    public List<TeachplanDto> findTeachplanTree(Long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
    }

    /***
    * @description TODO
    * @param saveTeachplanDto 
    * @return void
    * @author qjp
    * @date 2023/3/10 19:15
    */
    @Override
    public void saveTeachplan(SaveTeachplanDto saveTeachplanDto) {
        //通过课程计划id判断是新增还是修改
        Long teachplanId = saveTeachplanDto.getId();
        if (teachplanId == null){
            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(saveTeachplanDto,teachplan);
            //确定排序字段,找到它的同级节点个数,排序字段就是个数加一    select count(1)from teachplan where course_id=117 and parentid=268
            Long parentId = saveTeachplanDto.getParentid();
            Long courseId = saveTeachplanDto.getCourseId();
            int count = getTeachplanCount(parentId, courseId);
            teachplan.setOrderby(count);

            //新增
            teachplanMapper.insert(teachplan);
        }else {
            //修改
            Teachplan teachplan = teachplanMapper.selectById(teachplanId);
            //将参数复制到teachplan
            BeanUtils.copyProperties(saveTeachplanDto,teachplan);
            teachplanMapper.updateById(teachplan);
        }
    }


    /***
    * @description 求课程计划排序字段(看现有多少同级字段,防止所有字段后面)
    * @param parentid 
     * @param courseId 
    * @return int
    * @author qjp
    * @date 2023/3/10 19:53
    */
    private int getTeachplanCount(Long parentid, Long courseId) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId, courseId).eq(Teachplan::getParentid, parentid);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        return count+1;
    }
    /***
     * @description 根据id删除课程计划
     * @param id
     * @return void
     * @author qjp
     * @date 2023/3/13 11:33
     */
    @Override
    public void deleteCourse(Long id) {
        LambdaQueryWrapper<Teachplan> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(Teachplan::getParentid,id);
        Integer count = teachplanMapper.selectCount(wrapper1);
        if (count>=1){
            //有子小节
            XueChengPlusException.cast("课程计划信息还有子级信息，无法操作");
        }else {
            Teachplan teachplan = new Teachplan();
            teachplan.setId(id);
            teachplan.setStatus(0);
            teachplan.setCreateDate(LocalDateTime.now());
            teachplanMapper.updateById(teachplan);
            LambdaQueryWrapper<TeachplanMedia> wrapper2 = new LambdaQueryWrapper<>();
            wrapper2.eq(TeachplanMedia::getTeachplanId,id);
            teachplanMediaMapper.delete(wrapper2);
        }
    }
    /***
    * @description 课程计划向下移动
    * @param id
    * @return void
    * @author qjp
    * @date 2023/3/13 17:04
    */
    @Override
    public void moveDown(Long id) {
        if (id == null){
            XueChengPlusException.cast("课程计划id不能为空");
        }
        Teachplan teachplan = teachplanMapper.selectById(id);
        Integer orderBy1 = teachplan.getOrderby();
        //看看有没有后一个
        LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teachplan::getParentid, teachplan.getParentid())
                .eq(Teachplan::getCourseId,teachplan.getCourseId())
                .eq(Teachplan::getOrderby, orderBy1+1);
        //交换
        exchange(teachplan, wrapper);
    }
    /***
    * @description 课程计划向上移动
    * @param id
    * @return void
    * @author qjp
    * @date 2023/3/13 17:41
    */
    @Override
    public void moveUp(Long id) {
        if (id == null){
            XueChengPlusException.cast("课程计划id不能为空");
        }
        Teachplan teachplan = teachplanMapper.selectById(id);
        Integer orderBy1 = teachplan.getOrderby();
        //看看有没有前一个
        LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teachplan::getParentid, teachplan.getParentid())
                .eq(Teachplan::getCourseId,teachplan.getCourseId())
                .eq(Teachplan::getOrderby, orderBy1-1);
        //交换
        exchange(teachplan, wrapper);
    }

    /**
     * 教学计划绑定媒资
     * @param bindTeachplanMediaDto
     * @return
     */
    @Override
    @Transactional
    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto) {
        //课程计划id
        Long teachplanId = bindTeachplanMediaDto.getTeachplanId();
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        if (teachplan == null){
            XueChengPlusException.cast("课程计划不存在");
        }
        LambdaQueryWrapper<TeachplanMedia> qw = new LambdaQueryWrapper<>();
        qw.eq(TeachplanMedia::getTeachplanId,bindTeachplanMediaDto.getTeachplanId());
        //先删除原有记录  根据课程计划id删除它绑定的媒资
        int delete = teachplanMediaMapper.delete(qw);


        //再添加新纪录
        TeachplanMedia teachplanMedia = new TeachplanMedia();
        BeanUtils.copyProperties(bindTeachplanMediaDto,teachplanMedia);
        teachplanMedia.setCourseId(teachplan.getCourseId());
        teachplanMedia.setMediaFilename(bindTeachplanMediaDto.getFileName());
        teachplanMediaMapper.insert(teachplanMedia);
        return null;
    }

    /***
     * @description 交换两个计划的排序字段
     * @param teachplan
     * @param wrapper
     * @return void
     * @author qjp
     * @date 2023/3/13 17:40
     */
    private void exchange(Teachplan teachplan, LambdaQueryWrapper<Teachplan> wrapper) {
        Integer count = teachplanMapper.selectCount(wrapper);
        if (count !=1L){
            return;
        }else {
            //交换两个计划的orderby
            Teachplan teachplan1 = teachplanMapper.selectOne(wrapper);
            Integer orderby1 = teachplan1.getOrderby();
            Integer orderby = teachplan.getOrderby();
            teachplan.setOrderby(orderby1);
            teachplan1.setOrderby(orderby);
            teachplanMapper.updateById(teachplan);
            teachplanMapper.updateById(teachplan1);
        }
    }
}
