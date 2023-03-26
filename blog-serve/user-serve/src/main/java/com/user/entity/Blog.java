package com.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class Blog {
    @TableId(value = "blog_id", type = IdType.AUTO)
    private long blog_id;
    private String blog_title;
    private Timestamp blog_date;
    private String  blog_content;
    private String blog_cover;
    private String blog_description;
    private long user_id;
    private boolean blog_show;
}
