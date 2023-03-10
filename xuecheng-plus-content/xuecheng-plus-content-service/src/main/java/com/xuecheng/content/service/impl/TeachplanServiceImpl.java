package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author qjp
 * @version 1.0
 * @description TODO
 * @date 2023/3/10 17:53
 */
@Service
public class TeachplanServiceImpl implements TeachplanService {
    @Autowired
    TeachplanMapper teachplanMapper;
    /***
    * @description 根据id查询课程计划信息
    * @param courseId
    * @return java.util.List<com.xuecheng.content.model.dto.TeachplanDto>
    * @author qjp
    * @date 2023/3/10 19:15
    */
    @Override
    public List<TeachplanDto> findTeachplayTree(Long courseId) {
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
}
