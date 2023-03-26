package com.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("blogdata")
public class BlogData {
    private long blog_id;
    private int blog_likes;
    private int blog_browse;
}
