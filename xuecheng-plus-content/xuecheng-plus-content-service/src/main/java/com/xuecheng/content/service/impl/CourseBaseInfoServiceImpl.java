package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.content.mapper.*;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.*;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.xuechengplusbase.exception.XueChengPlusException;
import com.xuecheng.xuechengplusbase.model.PageParams;
import com.xuecheng.xuechengplusbase.model.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author qjp
 * @version 1.0
 * @description TODO
 * @date 2023/3/8 21:46
 */
@Slf4j
@Service
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {
    @Resource
    private CourseBaseMapper courseBaseMapper;
    @Resource
    private CourseMarketMapper courseMarkerMapper;
    @Resource
    private CourseCategoryMapper courseCategoryMapper;
    @Resource
    private CourseTeacherMapper courseTeacherMapper;
    @Resource
    private TeachplanMapper teachplanMapper;
    @Resource
    private TeachplanMediaMapper teachplanMediaMapper;
    @Resource
    private TeachplanWorkDao teachplanWorkDao;

    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto dto) {

        LambdaQueryWrapper<CourseBase> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(dto.getCourseName()), CourseBase::getName, dto.getCourseName());
        wrapper.eq(StringUtils.isNotEmpty(dto.getAuditStatus()), CourseBase::getAuditStatus, dto.getAuditStatus());
        wrapper.eq(StringUtils.isNotEmpty(dto.getPublishStatus()), CourseBase::getStatus, dto.getPublishStatus());
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());

        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, wrapper);
        //数据
        List<CourseBase> records = pageResult.getRecords();
        //总记录数
        long total = pageResult.getTotal();

        return new PageResult<>(records, total, pageParams.getPageNo(), pageParams.getPageSize());
    }

    @Transactional
    @Override
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto dto) {

        //1.向课程基本信息表course_base写入数据
        CourseBase courseBaseNew = new CourseBase();
        //将传入的页面参数放到courseBaseNew
        BeanUtils.copyProperties(dto, courseBaseNew);//只要属性名称一致就可以拷贝
        courseBaseNew.setCompanyId(companyId);
        courseBaseNew.setCreateDate(LocalDateTime.now());
        //审核状态默认为未提交
        courseBaseNew.setAuditStatus("202002");
        //发布状态为未发布
        courseBaseNew.setStatus("203001");
        //插入数据库
        int insert = courseBaseMapper.insert(courseBaseNew);
        if (insert <= 0) {
            XueChengPlusException.cast("添加课程失败");
        }

        //2.向课程营销表course_market写入数据
        CourseMarket courseMarketNew = new CourseMarket();
        //页面输入的数据拷贝到courseMarketNew中
        BeanUtils.copyProperties(dto, courseMarketNew);
        //主键 课程的id
        Long id = courseBaseNew.getId();
        courseMarketNew.setId(id);
        //保存营销信息
        saveCourseMarket(courseMarketNew);
        //从数据库查询课程的详细信息,包括两部分
        return getCourseBaseInfo(id);
    }

    /***
     * @description 根据id查询课程信息
     * @param courseId
     * @return com.xuecheng.content.model.dto.CourseBaseInfoDto
     * @author qjp
     * @date 2023/3/9 23:52
     */
    @Override
    public CourseBaseInfoDto getCourseBaseInfo(Long courseId) {

        //从课程基本信息表查询
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) {
            return null;
        }
        //从课程营销表查询
        CourseMarket courseMarket = courseMarkerMapper.selectById(courseId);

        //组装在一起
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase, courseBaseInfoDto);
        if (courseMarket != null) {
            BeanUtils.copyProperties(courseMarket, courseBaseInfoDto);
        }
        //通过courseCategoryMapper查询分类信息,查出分类名放入返回结果
        CourseCategory bigCourseCategory = courseCategoryMapper.selectById(courseBaseInfoDto.getMt());
        CourseCategory litCourseCategory = courseCategoryMapper.selectById(courseBaseInfoDto.getSt());
        courseBaseInfoDto.setMtName(bigCourseCategory.getName());
        courseBaseInfoDto.setStName(litCourseCategory.getName());
        return courseBaseInfoDto;
    }
    /***
    * @description 修改课程
    * @param companyId
     * @param editCourseDto
    * @return com.xuecheng.content.model.dto.CourseBaseInfoDto
    * @author qjp
    * @date 2023/3/13 23:25
    */
    @Transactional
    @Override
    public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto editCourseDto) {

        //拿到课程id
        Long courseId = editCourseDto.getId();
        //查询课程
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        CourseMarket courseMarket = courseMarkerMapper.selectById(courseId);
        if (courseBase == null) {
            XueChengPlusException.cast("课程不存在");
        }

        //数据合法性校验
        //根据具体的业务逻辑校验
        //本机构只能修改本机构的课程
        if (!companyId.equals(courseBase.getCompanyId())) {
            XueChengPlusException.cast("本机构只能修改本机构的课程");
        }

        //封装数据
        BeanUtils.copyProperties(editCourseDto, courseBase);
        BeanUtils.copyProperties(editCourseDto, courseMarket);
        //修改时间
        courseBase.setChangeDate(LocalDateTime.now());
        //更新数据库
        //更新基本信息
        int i = courseBaseMapper.updateById(courseBase);
        //更新营销信息
        int i1 = saveCourseMarket(courseMarket);
        if (i <= 0 && i1 <= 0) {
            XueChengPlusException.cast("修改课程失败");
        }
        //查询课程信息并返回
        return getCourseBaseInfo(courseId);
    }


    /***
     * @description 保存营销信息  存在则更新 不存在则添加
     * @param courseMarketNew  需要保存的营销信息
     * @return int
     * @author qjp
     * @date 2023/3/9 23:50
     */
    private int saveCourseMarket(CourseMarket courseMarketNew) {
        //1.参数的合法性校验
        String charge = courseMarketNew.getCharge();
        if (StringUtils.isEmpty(charge)) {
            XueChengPlusException.cast("收费规则为空!");
        }
        //如果课程收费,价格没有填写
        if (charge.equals("201001")) {
            if (courseMarketNew.getPrice() == null || courseMarketNew.getPrice() <= 0) {
                XueChengPlusException.cast("课程价格不能为空并且必要大于0!");
            }
        }
        //2.从数据库查询营销信息   存在则更新 不存在则添加
        Long id = courseMarketNew.getId();
        CourseMarket courseMarket = courseMarkerMapper.selectById(id);
        if (courseMarket == null) {
            //插入数据库
            return courseMarkerMapper.insert(courseMarketNew);
        } else {
            //将courseMarketNew拷贝到courseMarket
            BeanUtils.copyProperties(courseMarketNew, courseMarket);
            courseMarket.setId(courseMarketNew.getId());
            //更新
            return courseMarkerMapper.updateById(courseMarket);
        }
    }

    /***
     * @description 删除课程
     * @param id
     * @param companyId
     * @return void
     * @author qjp
     * @date 2023/3/13 22:12
     */
    @Override
    public void deleteCourse(Long id, Long companyId) {
        CourseBase courseBase = courseBaseMapper.selectById(id);
        if (!courseBase.getCompanyId().equals(companyId)) {
            //非本机构课程
            XueChengPlusException.cast("只允许删除本机构的课程。");
        }
        //删除相关课程信息
        courseBaseMapper.deleteById(id);
        //删除营销信息
        courseMarkerMapper.deleteById(id);
        LambdaQueryWrapper<CourseTeacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseTeacher::getCourseId, id);
        courseTeacherMapper.delete(wrapper);
        //删除课程计划
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId, id);
        teachplanMapper.delete(queryWrapper);
        //删除计划相关媒体文件
        LambdaQueryWrapper<TeachplanMedia> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(TeachplanMedia::getCourseId, id);
        teachplanMediaMapper.delete(queryWrapper1);
        //删除作业
        LambdaQueryWrapper<TeachplanWork> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.eq(TeachplanWork::getCourseId, id);
        teachplanWorkDao.delete(queryWrapper2);
    }
}
