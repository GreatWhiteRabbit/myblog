package com.user.Vo;

import lombok.Data;


/*
* 这个实体类是访客用户的昵称、邮箱、和验证码
* */
@Data
public class Email {


    private String user_name;
    private String user_email;
    private String testCode;
}
