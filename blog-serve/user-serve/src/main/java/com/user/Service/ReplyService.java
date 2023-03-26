package com.user.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.user.entity.Reply;

import java.util.List;

public interface ReplyService extends IService<Reply> {
    Reply addBlogReply(Reply reply);

    Reply addReply(Reply reply);

    List<Reply> getListByBlogId(long blog_id);

    List<Reply> getListReply();

    boolean deleteReply(Reply reply);

    List<Reply> getListByReplyedId(long replyed_id);
}
