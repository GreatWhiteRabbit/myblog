package com.user.Vo;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
public class BlogVo {
    private long blog_id;
    private String blog_title;
    private Timestamp blog_date;
    private String  blog_content;
    private String blog_cover;
    private String blog_description;
    private long user_id;
    private boolean blog_show;

    // blogData
    private int blog_likes;
    private int blog_browse;
    // category
    private int category_id;
    private String category_name;

   // label
    private int label_id;
    private String label_name;

    // md
    private String md_content;

}
