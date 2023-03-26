package com.user.Vo;


import lombok.Data;

import java.util.List;

/*
 * 这个实体类中存储的是category和blog的一对多的关系
 * */
@Data
public class CategoryListBlogs {
    private int category_id;
    private String category_name;
    private List<Long> blogList;
}
