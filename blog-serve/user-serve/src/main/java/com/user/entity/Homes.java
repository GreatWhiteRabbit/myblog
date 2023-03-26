package com.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/*
* 主页图片轮播表
* */
@Data
public class Homes {
    @TableId(value = "id",type = IdType.AUTO)
    private int id;
    private String title;
    private String info;
    private String imgurl;
    private int home_show;
    private String link;
}
