package com.xuecheng.generator.controller;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.generator.entity.CourseTeacher;
import com.xuecheng.generator.service.CourseTeacherService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * 课程-教师关系表(CourseTeacher)表控制层
 *
 * @author makejava
 * @since 2023-03-13 20:40:32
 */
@RestController
@RequestMapping("courseTeacher")
public class CourseTeacherController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private CourseTeacherService courseTeacherService;

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param courseTeacher 查询实体
     * @return 所有数据
     */
    @GetMapping
    public R selectAll(Page<CourseTeacher> page, CourseTeacher courseTeacher) {
        return success(this.courseTeacherService.page(page, new QueryWrapper<>(courseTeacher)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public R selectOne(@PathVariable Serializable id) {
        return success(this.courseTeacherService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param courseTeacher 实体对象
     * @return 新增结果
     */
    @PostMapping
    public R insert(@RequestBody CourseTeacher courseTeacher) {
        return success(this.courseTeacherService.save(courseTeacher));
    }

    /**
     * 修改数据
     *
     * @param courseTeacher 实体对象
     * @return 修改结果
     */
    @PutMapping
    public R update(@RequestBody CourseTeacher courseTeacher) {
        return success(this.courseTeacherService.updateById(courseTeacher));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    public R delete(@RequestParam("idList") List<Long> idList) {
        return success(this.courseTeacherService.removeByIds(idList));
    }
}

