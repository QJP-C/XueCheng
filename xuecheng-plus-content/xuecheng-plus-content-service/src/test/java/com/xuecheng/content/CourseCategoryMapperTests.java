package com.xuecheng.content;

import com.xuecheng.ContentServiceApplication;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description TODO
 * @author qjp
 * @date 2023/3/8 18:44
 * @version 1.0
 */

@SpringBootTest(classes = {ContentServiceApplication.class})
public class CourseCategoryMapperTests {
    @Resource
    CourseCategoryMapper courseCategoryMapper;
    @Test
    public void testCourseBaseMapper(){
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryMapper.selectTreeNodes("1");
        courseCategoryTreeDtos.forEach(System.out::println);
    }
}

