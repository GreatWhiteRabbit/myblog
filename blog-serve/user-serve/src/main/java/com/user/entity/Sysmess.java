package com.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/*
* 系统公告
* */
@Data
public class Sysmess {
    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    private String title; // 公告标题
    private String content; // 公告内容
    private boolean sys_show; // 公告是否显示
}
