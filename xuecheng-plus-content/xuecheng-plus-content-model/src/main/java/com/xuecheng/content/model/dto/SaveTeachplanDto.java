package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author qjp
 * @version 1.0
 * @description 新增大章节,小章节,修改章节信息
 * @date 2023/3/10 18:24
 */
@Data
@ApiModel(value="SaveTeachplanDto", description="创建或修改课程计划")
public class SaveTeachplanDto {

    /***
     * 教学计划id
     */
    @ApiModelProperty(value = "教学计划id")
    private Long id;

    /**
     * 课程计划名称
     */
    @ApiModelProperty(value = "课程计划名称",required = true)
    @NotEmpty(message = "课程计划名称不能为空")
    private String pname;

    /**
     * 课程计划父级Id
     */
    @ApiModelProperty(value = "课程计划父级Id",required = true)
    @NotNull(message = "课程计划父级id不能为空")
    private Long parentid;

    /**
     * 层级，分为1、2、3级
     */
    @ApiModelProperty(value = "层级，分为1、2、3级",required = true)
    @NotNull(message = "层级不能为空")
    private Integer grade;

    /**
     * 课程类型:1视频、2文档
     */
    @ApiModelProperty(value = "课程类型:1视频、2文档")
    private String mediaType;


    /**
     * 课程标识
     */
    @ApiModelProperty(value = "课程标识",required = true)
    @NotNull(message = "课程标识不能为空")
    private Long courseId;

    /**
     * 课程发布标识
     */
    @ApiModelProperty(value = "课程发布标识")
    private Long coursePubId;


    /**
     * 是否支持试学或预览（试看）
     */
    @ApiModelProperty(value = "是否支持试学或预览（试看）")
    private String isPreview;
}
