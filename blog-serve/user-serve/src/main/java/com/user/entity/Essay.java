package com.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.sql.Timestamp;

/*
* 随笔实体类
* */
@Data
public class Essay {
    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    private String imgurl; // 随笔封面
    private String title; // 随笔标题
    private String info; // 随笔信息
    private int essay_show; // 随笔是否首页显示,1表示显示，0表示不显示，不想用Boolean了，前端数据接收有误
    private Timestamp create_time; // 创建时间
}
