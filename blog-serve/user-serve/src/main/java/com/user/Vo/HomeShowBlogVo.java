package com.user.Vo;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class HomeShowBlogVo {

    private long blog_id;
    private String blog_title;
    private Timestamp blog_date;

    private String blog_cover;
    private String blog_description;

    // category
    private String category_name;

    // label
    private String label_name;





}
