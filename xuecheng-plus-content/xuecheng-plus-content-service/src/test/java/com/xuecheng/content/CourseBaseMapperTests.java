package com.xuecheng.content;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.ContentServiceApplication;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.xuechengplus.base.model.PageParams;
import com.xuecheng.xuechengplus.base.model.PageResult;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.List;

/**
 * @description TODO
 * @author qjp
 * @date 2023/3/8 18:44
 * @version 1.0
 */

@SpringBootTest(classes = {ContentServiceApplication.class})
public class CourseBaseMapperTests {
    @Autowired
    CourseBaseMapper courseBaseMapper;
    @Test
    public void testCourseBaseMapper(){
        CourseBase courseBase = courseBaseMapper.selectById(18);
        Assertions.assertNotNull(courseBase);

        QueryCourseParamsDto dto = new QueryCourseParamsDto();
        dto.setCourseName("java");


        LambdaQueryWrapper<CourseBase> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(dto.getCourseName()),CourseBase::getName,dto.getCourseName());
        wrapper.eq(StringUtils.isNotEmpty(dto.getAuditStatus()),CourseBase::getAuditStatus,dto.getAuditStatus());
        //todo: 按课程发布状态查询
        //构造分页参数
        PageParams pageParams = new PageParams(1L, 2L);

        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());

        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, wrapper);
        //数据
        List<CourseBase> records = pageResult.getRecords();
        //总记录数
        long total = pageResult.getTotal();
        PageResult<CourseBase> courseBasePageResult = new PageResult<>(records, total, pageParams.getPageNo(), pageParams.getPageSize());
        System.out.println(courseBasePageResult);
    }
}

