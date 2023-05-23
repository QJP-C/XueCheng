package com.xuecheng.content;

import com.xuecheng.ContentServiceApplication;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.service.CourseCategoryService;
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
public class CourseCategoryServiceTests {
    @Autowired
    CourseCategoryService courseCategoryService;
    @Test
    public void testcourseCategoryService(){
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryService.queryTreeNodes("1");
        System.out.println(courseCategoryTreeDtos);
    }
}

