package com.user.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class Link {
    @TableId(value = "id" , type = IdType.AUTO)
    private int id;
    private String imgurl;
    private String link;
    private String name;
    private String info;
    private int link_show;
    private Timestamp apply_time;
}
