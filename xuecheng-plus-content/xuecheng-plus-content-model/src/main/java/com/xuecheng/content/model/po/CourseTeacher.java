package com.xuecheng.content.model.po;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 课程-教师关系表
 * </p>
 *
 * @author itcast
 */
@Data
@TableName("course_teacher")
@ApiModel(value = "CourseTeacher",description = "课程教师关系信息")
public class CourseTeacher implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 课程标识
     */
    @NotNull(message = "课程标识(id)不能为空")
    @ApiModelProperty(value = "课程标识(id)",required = true)
    private Long courseId;

    /**
     * 教师名称
     */
    @NotEmpty(message = "教师名称不能为空")
    @ApiModelProperty(value = "教师名称",required = true)
    private String teacherName;

    /**
     * 教师职位
     */
    @NotEmpty(message = "教师职位不能为空")
    @ApiModelProperty(value = "教师职位",required = true)
    private String position;

    /**
     * 教师简介
     */
    @ApiModelProperty(value = "教师简介",required = true)
    private String introduction;

    /**
     * 照片
     */
    @ApiModelProperty(value = "照片")
    private String photograph;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createDate;


}
