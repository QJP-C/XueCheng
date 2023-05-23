package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.xuechengplus.base.model.PageParams;
import com.xuecheng.xuechengplus.base.model.PageResult;

/**
 * @author qjp
 * @version 1.0
 * @description TODO课程信息管理接口
 * @date 2023/3/8 21:42
 */
public interface CourseBaseInfoService {
    /***
    * @description TODO课程分页查询
    * @param pageParams 分页查询参数
    * @param dto 查询条件
    * @return 查询结果
    * @author qjp
    * @date 2023/3/8 21:49
    */
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto dto);
    /***
    * @description 新增课程
    * @param companyId 机构id
    * @param addCourseDto  课程信息
    * @return com.xuecheng.content.model.dto.CourseBaseInfoDto  课程详细信息
    * @author qjp
    * @date 2023/3/9 23:10
    */
    public CourseBaseInfoDto createCourseBase(Long companyId,AddCourseDto addCourseDto);

    /***
    * @description 根据课程id查询课程的信息
    * @param courseId 课程id
    * @return com.xuecheng.content.model.dto.CourseBaseInfoDto  课程的详细信息
    * @author qjp
    * @date 2023/3/10 14:27
    */
    public CourseBaseInfoDto getCourseBaseInfo(Long courseId);
    /***
    * @description 修改课程
    * @param companyId 机构id
     * @param editCourseDto 修改课程的信息
    * @return com.xuecheng.content.model.dto.CourseBaseInfoDto 课程的详细详细
    * @author qjp
    * @date 2023/3/10 14:46
    */
    public CourseBaseInfoDto updateCourseBase(Long companyId,EditCourseDto editCourseDto);

    void deleteCourse(Long id, Long companyId);
}
