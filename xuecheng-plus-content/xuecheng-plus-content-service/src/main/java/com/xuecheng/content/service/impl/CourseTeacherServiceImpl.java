package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseTeacherDao;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import com.xuecheng.xuechengplus.base.exception.XueChengPlusException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 课程-教师关系表(CourseTeacher)表服务实现类
 *
 * @author makejava
 * @since 2023-03-13 20:41:26
 */
@Service
public class CourseTeacherServiceImpl extends ServiceImpl<CourseTeacherDao, CourseTeacher> implements CourseTeacherService {

    @Resource
    private CourseTeacherMapper courseTeacherMapper;
    @Resource
    private CourseBaseMapper courseBaseMapper;


    /***
     * @description 查看教师列表
     * @param courseId
     * @return java.util.List<com.xuecheng.content.model.po.CourseTeacher>
     * @author qjp
     * @date 2023/3/13 21:28
     */
    @Override
    public List<CourseTeacher> getCourseTeacher(Long courseId) {
        LambdaQueryWrapper<CourseTeacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseTeacher::getCourseId, courseId);
        return courseTeacherMapper.selectList(wrapper);
    }

    /***
     * @description 新增或修改老师
     * @param companyId
     * @param courseTeacher
     * @return com.xuecheng.content.model.po.CourseTeacher
     * @author qjp
     * @date 2023/3/13 21:38
     */
    @Override
    public CourseTeacher postCourseTeacher(Long companyId, CourseTeacher courseTeacher) {
        Long courseId = courseTeacher.getCourseId();
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (!courseBase.getCompanyId().equals(companyId)) {
            //非本机构课程
            XueChengPlusException.cast("只允许向机构自己的课程中添加老师、删除老师。");
        }
        if (courseTeacher.getId() == null) {
            //新增
            LocalDateTime now = LocalDateTime.now();
            courseTeacher.setCreateDate(now);
            courseTeacherMapper.insert(courseTeacher);
            LambdaQueryWrapper<CourseTeacher> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CourseTeacher::getCourseId, courseId).eq(CourseTeacher::getCreateDate, now);
            return courseTeacherMapper.selectOne(wrapper);
        } else {
            //修改
            courseTeacherMapper.updateById(courseTeacher);
            return courseTeacherMapper.selectById(courseTeacher.getId());
        }
    }

    /***
    * @description 删除老师
    * @param courseId
     * @param id
    * @return void
    * @author qjp
    * @date 2023/3/13 22:02
    */
    @Override
    public void deleteCourseTeacher(Long courseId, Long id,Long companyId) {
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (!courseBase.getCompanyId().equals(companyId)) {
            //非本机构课程
            XueChengPlusException.cast("只允许向机构自己的课程中添加老师、删除老师。");
        }
        courseTeacherMapper.deleteById(id);
    }
}

