package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.service.CourseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @description 课程分类相关接口
 * @author qjp
 * @date 2023/3/9 17:32
 * @version 1.0
 */
@RestController
public class CourseCategoryController {

    @Autowired
    private CourseCategoryService courseCategoryService;
    /***
    * @description 查看课程的树状分类
    * @return java.util.List<com.xuecheng.content.model.dto.CourseCategoryTreeDto>
    * @author qjp
    * @date 2023/3/13 23:26
    */
    @GetMapping("/course-category/tree-nodes")
    public List<CourseCategoryTreeDto> queryTreeNodes(){
        return courseCategoryService.queryTreeNodes("1");
    }
}
