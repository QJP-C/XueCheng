package com.xuecheng.generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.generator.dao.CourseTeacherDao;
import com.xuecheng.generator.entity.CourseTeacher;
import com.xuecheng.generator.service.CourseTeacherService;
import org.springframework.stereotype.Service;

/**
 * 课程-教师关系表(CourseTeacher)表服务实现类
 *
 * @author makejava
 * @since 2023-03-13 20:41:26
 */
@Service("courseTeacherService")
public class CourseTeacherServiceImpl extends ServiceImpl<CourseTeacherDao, CourseTeacher> implements CourseTeacherService {

}

