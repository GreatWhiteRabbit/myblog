package com.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.sql.Timestamp;

/*
* 文件上传和下载的实体类
* */
@Data
public class BlogFile {
    @TableId(value = "id", type = IdType.AUTO)
    private int id; // 文件的ID
    private String file_path; // 文件保存的路径
    private Timestamp upload_time; // 文件上传时间

}
