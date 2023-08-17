package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.TeachplanMedia;

import java.util.List;

/**
 * @author qjp
 * @version 1.0
 * @description 课程计划管理相关的接口
 * @date 2023/3/10 17:38
 */
public interface TeachplanService {
    /***
    * @description 根据课程id查询课程计划
    * @param courseId 课程id
    * @return java.util.List<com.xuecheng.content.model.dto.TeachplanDto>
    * @author qjp
    * @date 2023/3/10 17:52
    */
    public List<TeachplanDto> findTeachplanTree(Long courseId);
    /***
    * @description 新增/修改/保存课程计划
    * @param saveTeachplanDto 
    * @return void
    * @author qjp
    * @date 2023/3/10 19:09
    */
    public void saveTeachplan(SaveTeachplanDto saveTeachplanDto);
    /***
    * @description 根据id删除指定课程计划
    * @param courseId
    * @return void
    * @author qjp
    * @date 2023/3/13 11:31
    */
    void deleteCourse(Long id);

    /**
     * 下移
     * @param id
     */
    void moveDown(Long id);

    /**
     * 上移
     * @param id
     */
    void moveUp(Long id);

    /**
     * @description 教学计划绑定媒资
     * @param bindTeachplanMediaDto
     * @return com.xuecheng.content.model.po.TeachplanMedia
     * @author Mr.M
     * @date 2022/9/14 22:20
     */
    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto);

}
