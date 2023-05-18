package com.xuecheng.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.content.model.po.CourseTeacher;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 课程-教师关系表(CourseTeacher)表数据库访问层
 *
 * @author makejava
 * @since 2023-03-13 20:40:33
 */
public interface CourseTeacherDao extends BaseMapper<CourseTeacher> {

/**
* 批量新增数据（MyBatis原生foreach方法）
*
* @param entities List<CourseTeacher> 实例对象列表
* @return 影响行数
*/
int insertBatch(@Param("entities") List<CourseTeacher> entities);

/**
* 批量新增或按主键更新数据（MyBatis原生foreach方法）
*
* @param entities List<CourseTeacher> 实例对象列表
* @return 影响行数
* @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
*/
int insertOrUpdateBatch(@Param("entities") List<CourseTeacher> entities);

}

