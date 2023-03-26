package com.user.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class Reply {
    @TableId(value = "reply_id", type = IdType.AUTO)
    private long reply_id; // 回复ID
    private long replyed_id; //被回复的ID
    private String reply_content; // 回复内容
    private long reply_user; // 发表内容的用户ID
    private long response_user; // 被回复的用户ID
    private long blog_id; // 如果在博文下面留言，那么博文ID是多少
    private Timestamp reply_time; // 发布留言时间
    private String first_name; // 留言者的昵称
    private String second_name; // 被回复者的昵称

}
