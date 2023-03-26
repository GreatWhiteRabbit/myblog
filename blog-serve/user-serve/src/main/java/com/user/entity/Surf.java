package com.user.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.sql.Timestamp;

/*
* 获取访问者IP、浏览器等信息
* */
@Data
public class Surf {
    @TableId(value = "surf_id", type = IdType.AUTO)
    private long surf_id; // ID
    private String ip; // IP
    private Timestamp surf_time; // 访问时间
    private String device_system; // 操作系统
    private String browsername; // 浏览器名称
    private String province; // IP所在省份
    private String city; // IP所在城市
    private String device; // 设备
}
