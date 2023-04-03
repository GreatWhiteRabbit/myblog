package com.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class UpdateLog {
    @TableId(value = "id" , type = IdType.AUTO)
    private int id;
    private String update_info; // 更新说明
    private Date update_time; // 更新时间


}
