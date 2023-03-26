package com.user.Vo;

import com.user.entity.Reply;
import lombok.Data;

import java.util.List;

/*
* 将获取到的留言List存放到此类中，并且将留言的总数存放到此类中
* */
@Data
public class ReplyListVo {
    private List<ReplyList> replyList;
    private int total; // 留言总数
}
