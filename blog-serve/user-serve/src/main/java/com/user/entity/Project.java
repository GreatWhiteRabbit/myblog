package com.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.sql.Timestamp;

/*
* 项目实体类
* */
@Data
public class Project {
    @TableId(value = "id" , type = IdType.AUTO)
    private int id;
    private String imgurl;
    private String link;
    private String name;
    private String info;
    private int project_show;
    private Timestamp create_time;
}
