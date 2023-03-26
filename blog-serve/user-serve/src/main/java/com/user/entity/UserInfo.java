package com.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    private long user_id;
    private String user_description;
    private String user_password;
    private Date user_birthday;
    private int user_sex;
    private String user_image;
    private String user_account;

}
