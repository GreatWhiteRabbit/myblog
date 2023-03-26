package com.user.Vo;

import com.user.entity.Blog;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Array;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
public class BlogDoc {
    private long id;
    private String blog_title;

    private String  blog_content;
    private String blog_cover;
    private String blog_description;
    private long user_id;
    private boolean blog_show;

    private long blog_date;

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

    private List<String> suggestion;
    public BlogDoc(BlogVo blogVo) {
        this.id = blogVo.getBlog_id();
        this.blog_title = blogVo.getBlog_title();
        this.blog_content = blogVo.getBlog_content();
        this.blog_cover = blogVo.getBlog_cover();
        this.blog_description  = blogVo.getBlog_description();
        this.blog_date = blogVo.getBlog_date().getTime();
        this.user_id = blogVo.getUser_id();
        this.blog_show = blogVo.isBlog_show();
        this.blog_likes = blogVo.getBlog_likes();
        this.blog_browse = blogVo.getBlog_browse();
        this.category_id = blogVo.getCategory_id();
        this.category_name = blogVo.getCategory_name();
        this.label_id = blogVo.getLabel_id();
        this.label_name = blogVo.getLabel_name();
        this.md_content = blogVo.getMd_content();
        this.suggestion = Arrays.asList(this.blog_title,this.blog_description,this.category_name,this.label_name);
    }
}
