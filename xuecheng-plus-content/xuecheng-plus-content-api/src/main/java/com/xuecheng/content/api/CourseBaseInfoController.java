package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.utils.SecurityUtil;
import com.xuecheng.xuechengplus.base.exception.ValidationGroups;
import com.xuecheng.xuechengplus.base.model.PageParams;
import com.xuecheng.xuechengplus.base.model.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author qjp
 * @version 1.0
 * @description TODO
 * @date 2023/3/8 16:16
 */
@Api(value = "课程管理相关的接口", tags = "课程管理相关的接口")
@RestController
public class CourseBaseInfoController {
    @Autowired
    CourseBaseInfoService courseBaseInfoService;

    @ApiOperation("课程分页查询接口")
    @PreAuthorize("hasAuthority('xc_teachmanager_course_list')") //指定权限标识符
    @PostMapping("/course/list")
    public PageResult<CourseBase> companyIdlist(PageParams pageParams, @RequestBody QueryCourseParamsDto queryCourseParamsDto) {
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        Long companyId = null;
        if (user != null) {
            companyId = Long.parseLong(user.getCompanyId());
        }
        return courseBaseInfoService.queryCourseBaseList(companyId,pageParams, queryCourseParamsDto);
    }

    @ApiOperation("新增课程")
    @PostMapping("/course")                                //指定校验规则组别
    public CourseBaseInfoDto createCourseBase(@RequestBody @Validated(ValidationGroups.Inster.class) AddCourseDto addCourseDto) {
        Long companyId = 1232141425L;
        return courseBaseInfoService.createCourseBase(companyId, addCourseDto);
    }

    @ApiOperation("根据课程id查询接口")
    @GetMapping("/course/{courseId}")
    public CourseBaseInfoDto getCourseBaseById(@PathVariable("courseId") Long courseId){
        //获取当前用户身份
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        return courseBaseInfoService.getCourseBaseInfo(courseId);
    }

    @ApiOperation("修改课程")
    @PutMapping("/course")
    public CourseBaseInfoDto modifyCourseBase(@RequestBody @Validated(ValidationGroups.Update.class) EditCourseDto editCourseDto){
        Long companyId = 1232141425L;
        return courseBaseInfoService.updateCourseBase(companyId,editCourseDto);
    }

    @ApiOperation("删除课程")
    @DeleteMapping("/course/{id}")
    public void  deleteCourse(@PathVariable Long id){
        Long companyId = 1232141425L;
        courseBaseInfoService.deleteCourse(id,companyId);
    }

}

