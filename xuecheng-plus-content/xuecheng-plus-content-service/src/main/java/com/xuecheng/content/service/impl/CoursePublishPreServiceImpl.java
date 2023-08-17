package com.xuecheng.content.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.content.mapper.CoursePublishPreMapper;
import com.xuecheng.content.model.po.CoursePublishPre;
import com.xuecheng.content.service.CoursePublishPreService;
import org.springframework.stereotype.Service;

/**
 * 课程发布(CoursePublishPre)表服务实现类
 *
 * @author makejava
 * @since 2023-06-09 11:43:12
 */
@Service("coursePublishPreService")
public class CoursePublishPreServiceImpl extends ServiceImpl<CoursePublishPreMapper, CoursePublishPre> implements CoursePublishPreService {

}

