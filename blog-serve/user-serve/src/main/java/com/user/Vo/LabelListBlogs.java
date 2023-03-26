package com.user.Vo;


import lombok.Data;

import java.util.List;


/*
* 这个实体类中存储的是label和blog的一对多的关系
* */
@Data
public class LabelListBlogs {
    private int label_id;
    private String label_name;
    private List<Long> blogIdList;
}
