package com.user.Vo;

import lombok.Data;

@Data
public class PreAndNextBlog {
    private long preBlogId;
    private long nextBlogId;
    private String preTitle;
    private String nextTitle;
}
