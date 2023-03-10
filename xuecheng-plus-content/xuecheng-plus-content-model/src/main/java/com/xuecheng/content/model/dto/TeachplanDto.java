package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @description k
 * @author qjp
 * @date 2023/3/10 16:08
 * @version 1.0
 */
@Data
@ToString
public class TeachplanDto extends Teachplan {

    //与媒资关联的信息
    private TeachplanMedia teachplanMedia;
    //小章节列表
    private List<TeachplanMedia> teachPlanTreeNodes;

}
