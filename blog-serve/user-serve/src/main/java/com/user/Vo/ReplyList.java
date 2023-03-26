package com.user.Vo;

import com.user.entity.Reply;
import lombok.Data;

import java.util.List;

@Data
public class ReplyList {
    private Reply message;
    private List<Reply> replyed; // 查看留言下面有哪些回复的留言
}
