package com.xuecheng.content;

import com.xuecheng.ContentServiceApplication;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.xuechengplus.base.model.PageParams;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @description TODO
 * @author qjp
 * @date 2023/3/8 18:44
 * @version 1.0
 */

@SpringBootTest(classes = {ContentServiceApplication.class})
public class CourseBaseInfoServiceTests {
    @Autowired
    CourseBaseInfoService courseBaseInfoService;
    @Test
    public void testCourseBaseMapper(){
        QueryCourseParamsDto dto = new QueryCourseParamsDto();
        dto.setCourseName("java");
        dto.setAuditStatus("202004");
        dto.setPublishStatus("203001");

        PageParams pageParams = new PageParams(1L, 2L);
        System.out.println(courseBaseInfoService.queryCourseBaseList(null,pageParams, dto));
    }
}

