package com.xuecheng.content.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @author makejava
 * @since 2023-06-09 20:02:03
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@TableName("course_publish")
public class CoursePublish {


    private Long id;

    private Long companyId;

    private String companyName;

    private String name;

    private String users;

    private String tags;

    private String username;

    private String mt;

    private String mtName;

    private String st;

    private String stName;

    private String grade;

    private String teachmode;

    private String pic;

    private String description;

    private String market;

    private String teachplan;

    private String teachers;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss") //转对像时指定日期格式 Feign远程调用
    private LocalDateTime createDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime onlineDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime offlineDate;

    private String status;

    private String remark;

    private String charge;

    private String price;

    private String originalPrice;

    private Integer validDays;

}
