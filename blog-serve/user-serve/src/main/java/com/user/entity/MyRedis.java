package com.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class MyRedis {
    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    private String key_name; // key的名称
    private int expire_time; // 过期时间
}
