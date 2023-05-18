package com.xuecheng.content.api;

import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author qjp
 * @version 1.0
 * @description TODO
 * @date 2023/3/13 20:28
 */
@Slf4j
@RestController
@Api(value = "课程教师相关接口",tags = "课程教师相关接口")
public class CourseTeacherController {

    @Autowired
    private CourseTeacherService courseTeacherService;

    /***
    * @description 查看教师列表
    * @param courseId
    * @return java.util.List<com.xuecheng.content.model.po.CourseTeacher>
    * @author qjp
    * @date 2023/3/13 21:24
    */
    @ApiOperation("查看教师列表")
    @GetMapping("/courseTeacher/list/{courseId}")
    public List<CourseTeacher>  getCourseTeacher(@PathVariable Long courseId){
        return courseTeacherService.getCourseTeacher(courseId);
    }
    /***
    * @description 新增或修改老师
    * @param courseTeacher
    * @return com.xuecheng.content.model.po.CourseTeacher
    * @author qjp
    * @date 2023/3/13 21:39
    */
    @ApiOperation("新增或修改老师")
    @PostMapping("/courseTeacher")
    public CourseTeacher postCourseTeacher(@RequestBody @Validated CourseTeacher courseTeacher){
        Long companyId = 1232141425L;
        return courseTeacherService.postCourseTeacher(companyId,courseTeacher);
    }
    /***
    * @description 删除老师
    * @param courseId
     * @param id
    * @return void
    * @author qjp
    * @date 2023/3/13 22:50
    */
    @ApiOperation("删除老师")
    @DeleteMapping("/courseTeacher/course/{courseId}/{id}")
    public void deleteCourseTeacher(@PathVariable("courseId")Long courseId,@PathVariable("id")Long id){
        Long companyId = 1232141425L;
        courseTeacherService.deleteCourseTeacher(courseId,id,companyId);
    }

}
