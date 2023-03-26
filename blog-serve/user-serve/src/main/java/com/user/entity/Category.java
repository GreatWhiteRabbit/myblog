package com.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Category {
    @TableId(value = "category_id", type = IdType.AUTO)
private int category_id;
private String category_name;
}
