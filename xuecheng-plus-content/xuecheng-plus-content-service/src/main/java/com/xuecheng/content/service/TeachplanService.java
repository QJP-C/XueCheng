package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;

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
    public List<TeachplanDto> findTeachplayTree(Long courseId);
    /***
    * @description 新增/修改/保存课程计划
    * @param saveTeachplanDto 
    * @return void
    * @author qjp
    * @date 2023/3/10 19:09
    */
    public void saveTeachplan(SaveTeachplanDto saveTeachplanDto);
}
