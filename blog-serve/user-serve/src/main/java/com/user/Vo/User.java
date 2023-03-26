package com.user.Vo;

import lombok.Data;

import java.sql.Date;
import java.sql.Timestamp;


@Data
public class User {
    private long user_id;
    private String user_description;
    private String user_password;
    private Date user_birthday;
    private int user_sex;
    private String user_image;
    private String user_account;

    private String user_name;
    private int user_status;
    private String user_email;
    private Timestamp user_creattime;
}

