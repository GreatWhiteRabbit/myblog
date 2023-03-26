package com.user.Vo;

import lombok.Data;

import java.util.List;

@Data
public class BlogDocPage {
   private List<BlogDoc> blogDocList;
    private long total;
    public BlogDocPage(List<BlogDoc> blogDocList, long total){
        this.blogDocList = blogDocList;
        this.total = total;
    }
}
