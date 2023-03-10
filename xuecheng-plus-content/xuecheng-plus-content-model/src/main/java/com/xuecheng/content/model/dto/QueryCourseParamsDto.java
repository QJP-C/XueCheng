package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;
import lombok.ToString;

/**
 * @description TODO
 * @author qjp
 * @date 2023/3/8 16:02
 * @version 1.0
 */
@Data
@ToString
public class QueryCourseParamsDto {
    @ApiModelProperty("审核状态")
    //审核状态
    private String auditStatus;
    @ApiModelProperty("课程名称")
    //课程名称
    private String courseName;
    @ApiModelProperty("发布状态")
    //发布状态
    private String publishStatus;

}
