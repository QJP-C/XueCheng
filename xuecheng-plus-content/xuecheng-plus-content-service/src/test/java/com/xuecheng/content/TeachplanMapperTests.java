package com.xuecheng.content;

import com.xuecheng.ContentServiceApplication;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @description 课程计划mapper测试
 * @author qjp
 * @date 2023/3/8 18:44
 * @version 1.0
 */

@SpringBootTest(classes = {ContentServiceApplication.class})
public class TeachplanMapperTests {
    @Autowired
    TeachplanMapper teachplanMapper;
    @Test
    public void testTeachplanMapper(){
        List<TeachplanDto> teachplanDtos = teachplanMapper.selectTreeNodes(117L);
        System.out.println(teachplanDtos);
    }
}

