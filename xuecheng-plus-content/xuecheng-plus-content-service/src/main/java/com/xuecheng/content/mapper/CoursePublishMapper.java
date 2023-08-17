package com.xuecheng.content.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.content.model.po.CoursePublish;
import org.apache.ibatis.annotations.Mapper;

/**
 * 课程发布(com.xuecheng.content.model.po.CoursePublish)表数据库访问层
 *
 * @author makejava
 * @since 2023-06-09 20:02:03
 */
@Mapper
public interface CoursePublishMapper extends BaseMapper<CoursePublish> {

}
