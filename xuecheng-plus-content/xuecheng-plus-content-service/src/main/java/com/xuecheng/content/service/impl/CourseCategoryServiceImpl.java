package com.xuecheng.content.service.impl;

import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.service.CourseCategoryService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description TODO
 * @author qjp
 * @date 2023/3/9 20:07
 * @version 1.0
 */
@Slf4j
@Service
public class CourseCategoryServiceImpl implements CourseCategoryService {

    @Resource
    private CourseCategoryMapper CourseCategoryMapper;
    /***
    * @description 查看课程的树状分类
    * @param id
    * @return java.util.List<com.xuecheng.content.model.dto.CourseCategoryTreeDto>
    * @author qjp
    * @date 2023/3/13 23:27
    */
    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {
        //调用mapper递归查询出分类信息
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = CourseCategoryMapper.selectTreeNodes(id);

        //定义一个list作为最终返回的list
        List<CourseCategoryTreeDto> courseCategoryList = new ArrayList<>();

        //找到每个节点的字节点   封装成List
        //先將List转成map,key就是结点的id,value就是CourseCategoryTreeDto对象，目的就是为了方便从map获取结点
        Map<String, CourseCategoryTreeDto> map = courseCategoryTreeDtos.stream()
                .filter(item-> !id.equals(item.getId()))    //去除根节点
                .collect(Collectors.toMap(              //遍历收集为map
                        CourseCategory::getId,          //map的key
                        value -> value,                 //map的value
                        (key1, key2) -> key2            //当key重复时 以第二个key为主  (防报错)
                ));
        //从头遍历List<CourseCategoryTreeDto.>,一边，遍历一边找子节点放在父节点childrenTreeNodes
        courseCategoryTreeDtos.stream().filter(item-> !id.equals(item.getId())).forEach(item->{
            if (item.getParentid().equals(id)){
                //将二级节点放入集合
                courseCategoryList.add(item);
            }
            //找到节点的父节点
            CourseCategoryTreeDto courseCategoryParent = map.get(item.getParentid());
            if (courseCategoryParent!=null){
                if (courseCategoryParent.getChildrenTreeNodes()==null){
                    //如果该父节点的ChildrenTreeNodes属性为空 说明是二级节点 要new一个集合,因为我们要向该集合中放它的子节点
                    courseCategoryParent.setChildrenTreeNodes(new ArrayList<CourseCategoryTreeDto>());
                }
                //到每个节点的子节点放在父节点的childrenTreeNodes属性中
                courseCategoryParent.getChildrenTreeNodes().add(item);
            }
        });
        return courseCategoryList;
    }
}

