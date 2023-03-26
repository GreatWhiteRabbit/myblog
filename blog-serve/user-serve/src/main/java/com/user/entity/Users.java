package com.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users {
    @TableId(type = IdType.AUTO)
    private long user_id;
    private String user_name;
    private int user_status;
    private String user_email;
    private Timestamp user_creattime;
}
