package com.xuecheng.content.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.xuecheng.content.model.po.CourseTeacher;

import java.util.List;

/**
 * 课程-教师关系表(CourseTeacher)表服务接口
 *
 * @author makejava
 * @since 2023-03-13 20:41:25
 */
public interface CourseTeacherService extends IService<CourseTeacher> {

    List<CourseTeacher> getCourseTeacher(Long courseId);

    CourseTeacher postCourseTeacher(Long companyId, CourseTeacher courseTeacher);

    void deleteCourseTeacher(Long courseId, Long id ,Long companyId);
}

