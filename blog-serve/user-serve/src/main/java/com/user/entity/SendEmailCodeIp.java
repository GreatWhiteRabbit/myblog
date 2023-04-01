package com.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.sql.Timestamp;

@Data
// 记录发送邮箱验证码的IP地址
public class SendEmailCodeIp {
    @TableId(value = "id" , type = IdType.AUTO)
    private int id;
    private Timestamp surf_time;
    private String email;
    private String ip;
    private int allowed; // 1表示允许，0表示禁用IP
}
