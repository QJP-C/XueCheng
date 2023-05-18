package com.xuecheng.content.model.po;

import java.util.Date;
import java.io.Serializable;

/**
 * (TeachplanWork)实体类
 *
 * @author makejava
 * @since 2023-03-13 22:36:51
 */
public class TeachplanWork implements Serializable {
    private static final long serialVersionUID = 416306640841324908L;
    /**
     * 主键
     */
    private Long id;
    /**
     * 作业信息标识
     */
    private Long workId;
    /**
     * 作业标题
     */
    private String workTitle;
    /**
     * 课程计划标识
     */
    private Long teachplanId;
    /**
     * 课程标识
     */
    private Long courseId;
    
    private Date createDate;
    
    private Long coursePubId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWorkId() {
        return workId;
    }

    public void setWorkId(Long workId) {
        this.workId = workId;
    }

    public String getWorkTitle() {
        return workTitle;
    }

    public void setWorkTitle(String workTitle) {
        this.workTitle = workTitle;
    }

    public Long getTeachplanId() {
        return teachplanId;
    }

    public void setTeachplanId(Long teachplanId) {
        this.teachplanId = teachplanId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Long getCoursePubId() {
        return coursePubId;
    }

    public void setCoursePubId(Long coursePubId) {
        this.coursePubId = coursePubId;
    }

}

