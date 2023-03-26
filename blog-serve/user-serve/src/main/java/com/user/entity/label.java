package com.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class label {
    @TableId(value = "label_id", type = IdType.AUTO)
    private int label_id;
    private String label_name;
}
