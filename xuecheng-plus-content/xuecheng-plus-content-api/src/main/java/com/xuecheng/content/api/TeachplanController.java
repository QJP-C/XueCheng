package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.service.TeachplanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @description 课程计划管理的相关接口
 * @author qjp
 * @date 2023/3/10 16:43
 * @version 1.0
 */
@RestController
@Api(value = "课程计划编辑接口",tags = "课程计划编辑接口")
public class TeachplanController {

    @Autowired
    TeachplanService teachplanService;

    //查询课程计划 GET /teachplan/22/tree-nodes
    @ApiOperation("查询课程计划树形结构")
    @GetMapping("/teachplan/{courseId}/tree-nodes")
    public List<TeachplanDto> getTreeNodes(@PathVariable Long courseId){
        return teachplanService.findTeachplayTree(courseId);
    }

    @ApiOperation("创建或修改课程计划")
    @PostMapping("/teachplan")
    public void saveTeachplan(@RequestBody @Validated SaveTeachplanDto teachplan){
        teachplanService.saveTeachplan(teachplan);
    }
}
