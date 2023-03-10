package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

/**
 * @description TODO
 * @author qjp
 * @date 2023/3/10 14:36
 * @version 1.0
 */
@Data
public class EditCourseDto extends AddCourseDto {
    @ApiModelProperty(value = "课程id",required = true)
    private Long id;
}
