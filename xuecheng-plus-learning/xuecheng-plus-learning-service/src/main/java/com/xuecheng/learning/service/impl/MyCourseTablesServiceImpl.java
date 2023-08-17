package com.xuecheng.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.learning.feignclient.ContentServiceClient;
import com.xuecheng.learning.mapper.XcChooseCourseMapper;
import com.xuecheng.learning.mapper.XcCourseTablesMapper;
import com.xuecheng.learning.model.dto.XcChooseCourseDto;
import com.xuecheng.learning.model.dto.XcCourseTablesDto;
import com.xuecheng.learning.model.po.XcChooseCourse;
import com.xuecheng.learning.model.po.XcCourseTables;
import com.xuecheng.learning.service.MyCourseTablesService;
import com.xuecheng.xuechengplus.base.exception.XueChengPlusException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 选课相关接口实现
 */
@Service
public class MyCourseTablesServiceImpl implements MyCourseTablesService {

    @Resource
    XcChooseCourseMapper chooseCourseMapper;

    @Resource
    XcCourseTablesMapper courseTablesMapper;

    @Resource
    ContentServiceClient contentServiceClient;

    /**
     * @param userId   用户id
     * @param courseId 课程id
     * @return com.xuecheng.learning.model.dto.XcChooseCourseDto
     * @description 添加选课
     * @date 2022/10/24 17:33
     */
    @Transactional
    @Override
    public XcChooseCourseDto addChooseCourse(String userId, Long courseId) {
        //选课调用内容管理服务查询课程收费规则
        CoursePublish coursepublish = contentServiceClient.getCoursepublish(courseId);
        if (coursepublish == null) {
            XueChengPlusException.cast("课程不存在");
        }
        //收费规则
        String charge = coursepublish.getCharge();
        //选课记录
        XcChooseCourse chooseCourse = null;
        if ("201000".equals(charge)) {//免费课
            //向选课记录表写数据
            chooseCourse = addFreeCoruse(userId, coursepublish);//向选课记录表写
            //向我的课程表写数据
            XcCourseTables xcCourseTables = addCourseTabls(chooseCourse);
        } else {//收费
            //如果是收费课，只向选课记录表
            chooseCourse = addChargeCoruse(userId, coursepublish);
        }

        //判断学生的学习资格
        XcCourseTablesDto xcCourseTablesDto = getLearningStatus(userId, courseId);

        //构造返回值
        XcChooseCourseDto xcChooseCourseDto = new XcChooseCourseDto();
        BeanUtils.copyProperties(xcCourseTablesDto,xcChooseCourseDto);
        //设置学习资格状态
        xcChooseCourseDto.setLearnStatus(xcChooseCourseDto.learnStatus);

        return xcChooseCourseDto;
    }

    /**
     * @param userId
     * @param courseId
     * @return XcCourseTablesDto 学习资格状态 [{"code":"702001","desc":"正常学习"},{"code":"702002","desc":"没有选课或选课后没有支付"},{"code":"702003","desc":"已过期需要申请续期或重新支付"}]
     * @description 判断学习资格
     * @author Mr.M
     * @date 2022/10/3 7:37
     */
    @Override
    public XcCourseTablesDto getLearningStatus(String userId, Long courseId) {
        //返回的结果
        XcCourseTablesDto courseTablesDto = new XcCourseTablesDto();

        //查询我的课程表 如果查不到 说明没有选课
        XcCourseTables xcCourseTables = getXcCourseTables(userId, courseId);
        if (xcCourseTables == null) {
            courseTablesDto.setLearnStatus("702002");
            return courseTablesDto;
        }
        //如果查到了，判断是否过期 ，如果过期不能学习，没有过期可以继续学习
        boolean before = xcCourseTables.getValidtimeEnd().isBefore(LocalDateTime.now());
        if (before) {//过期    {"code":"702003","desc":"已过期需要申请续期或重新支付"}
            BeanUtils.copyProperties(xcCourseTables, courseTablesDto);
            courseTablesDto.setLearnStatus("702003");
            return courseTablesDto;
        } else {//未过期 可以正常学习   {"code":"702001","desc":"正常学习"}
            BeanUtils.copyProperties(xcCourseTables, courseTablesDto);
            courseTablesDto.setLearnStatus("702001");
            return courseTablesDto;
        }
    }

    /**
     * 添加免费课程,免费课程加入选课记录表、我的课程表
     *
     * @param userId
     * @param coursepublish
     * @return
     */
    public XcChooseCourse addFreeCoruse(String userId, CoursePublish coursepublish) {
        //课程id
        Long courseId = coursepublish.getId();
        //判断  如果该用户该课已经存在免费的选课记录并且选课状态为成功，直接返回了
        LambdaQueryWrapper<XcChooseCourse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(XcChooseCourse::getUserId, userId).
                eq(XcChooseCourse::getCourseId, courseId).
                eq(XcChooseCourse::getOrderType, "700001").//免费课程
                eq(XcChooseCourse::getStatus, "701001");//选课成功
        List<XcChooseCourse> xcChooseCourses = chooseCourseMapper.selectList(wrapper);
        if (xcChooseCourses.size() > 0) return xcChooseCourses.get(0);//存在 返回

        //向选课记录表写数据
        XcChooseCourse chooseCourse = new XcChooseCourse();

        chooseCourse.setCourseId(courseId);
        chooseCourse.setCourseName(coursepublish.getName());
        chooseCourse.setUserId(userId);
        chooseCourse.setCompanyId(coursepublish.getCompanyId());
        chooseCourse.setOrderType("700001");//免费课程
        chooseCourse.setCreateDate(LocalDateTime.now());
        chooseCourse.setCoursePrice(Float.valueOf(coursepublish.getPrice()));
        chooseCourse.setValidDays(365);
        chooseCourse.setStatus("701001");//选课成功
        chooseCourse.setValidtimeStart(LocalDateTime.now());//有效期的开始时间
        chooseCourse.setValidtimeEnd(LocalDateTime.now().plusDays(365));//有效期的结束时间

        int insert = chooseCourseMapper.insert(chooseCourse);
        if (insert <= 0) XueChengPlusException.cast("添加选课记录失败");


        return chooseCourse;
    }

    /**
     * 添加到我的课程表
     *
     * @param xcChooseCourse
     * @return
     */
    public XcCourseTables addCourseTabls(XcChooseCourse xcChooseCourse) {
        //选课成功了才可以向我的课程表添加
        String status = xcChooseCourse.getStatus();
        if (!"701001".equals(status)) {
            XueChengPlusException.cast("选课没有成功无法添加到课程表");
        }
        XcCourseTables xcCourseTables = getXcCourseTables(xcChooseCourse.getUserId(), xcChooseCourse.getCourseId());
        if (xcCourseTables != null) {
            return xcCourseTables;
        }
        xcCourseTables = new XcCourseTables();
        BeanUtils.copyProperties(xcChooseCourse, xcCourseTables);
        xcCourseTables.setChooseCourseId(xcCourseTables.getId());//记录选课记录的主键
        xcCourseTables.setCourseType(xcChooseCourse.getOrderType());//课程类型
        xcCourseTables.setUpdateDate(LocalDateTime.now());
        int insert = courseTablesMapper.insert(xcCourseTables);
        if (insert <= 0) XueChengPlusException.cast("添加我的课程表失败");


        return xcCourseTables;
    }

    /**
     * 添加收费课程
     *
     * @param userId
     * @param coursepublish
     * @return
     */
    public XcChooseCourse addChargeCoruse(String userId, CoursePublish coursepublish) {
//课程id
        Long courseId = coursepublish.getId();
        //判断  如果该用户该课已经存在收费的选课记录并且选课状态为待支付，直接返回了
        LambdaQueryWrapper<XcChooseCourse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(XcChooseCourse::getUserId, userId).
                eq(XcChooseCourse::getCourseId, courseId).
                eq(XcChooseCourse::getOrderType, "700002").//收费课程
                eq(XcChooseCourse::getStatus, "701002");//待支付
        List<XcChooseCourse> xcChooseCourses = chooseCourseMapper.selectList(wrapper);
        if (xcChooseCourses.size() > 0) return xcChooseCourses.get(0);//存在 返回

        //向选课记录表写数据
        XcChooseCourse chooseCourse = new XcChooseCourse();

        chooseCourse.setCourseId(courseId);
        chooseCourse.setCourseName(coursepublish.getName());
        chooseCourse.setUserId(userId);
        chooseCourse.setCompanyId(coursepublish.getCompanyId());
        chooseCourse.setOrderType("700002");//收费课程
        chooseCourse.setCreateDate(LocalDateTime.now());
        chooseCourse.setCoursePrice(Float.valueOf(coursepublish.getPrice()));
        chooseCourse.setValidDays(365);
        chooseCourse.setStatus("701002");//待支付
        chooseCourse.setValidtimeStart(LocalDateTime.now());//有效期的开始时间
        chooseCourse.setValidtimeEnd(LocalDateTime.now().plusDays(365));//有效期的结束时间

        int insert = chooseCourseMapper.insert(chooseCourse);
        if (insert <= 0) XueChengPlusException.cast("添加选课记录失败");

        return chooseCourse;
    }

    /**
     * @param userId
     * @param courseId
     * @return com.xuecheng.learning.model.po.XcCourseTables
     * @description 根据课程和用户查询我的课程表中某一门课程
     * @date 2022/10/2 17:07
     */
    public XcCourseTables getXcCourseTables(String userId, Long courseId) {
        XcCourseTables xcCourseTables = courseTablesMapper.selectOne(new LambdaQueryWrapper<XcCourseTables>().eq(XcCourseTables::getUserId, userId).eq(XcCourseTables::getCourseId, courseId));
        return xcCourseTables;

    }

}
