package com.user.Service.Impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.user.Mapper.ReplyMapper;
import com.user.Service.ReplyService;
import com.user.entity.Reply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class ReplyServiceImpl extends ServiceImpl<ReplyMapper, Reply> implements ReplyService {
    @Autowired
    private ReplyMapper replyMapper;

    @Override
    public Reply addBlogReply(Reply reply) {
        // 获取留言发表的时间
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        reply.setReply_time(timestamp);
        int insert = replyMapper.insert(reply);
        if(insert == 0) {
            return null;
        }
        return reply;
    }
    @Override
    public Reply addReply(Reply reply) {
        // 获取留言发表的时间
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        reply.setReply_time(timestamp);
        int insert = replyMapper.insert(reply);
        if(insert == 0) {
            return null;
        }
        return reply;
    }

    @Override
    public List<Reply> getListByBlogId(long blog_id) {
        QueryWrapper<Reply> replyQueryWrapper = new QueryWrapper<>();
        // 获取所有博文直接评论，不包括被回复的评论，那么就是replyed_id要为0
        replyQueryWrapper.eq("blog_id",blog_id);
        replyQueryWrapper.eq("replyed_id",0);
        List<Reply> replyList = list(replyQueryWrapper);
        return replyList;
    }

    @Override
    public List<Reply> getListReply() {
        QueryWrapper<Reply> replyQueryWrapper = new QueryWrapper<>();
        // 获取所有与博文无关的留言，那么博文ID就是为0，而且被回复的ID也要为0
        replyQueryWrapper.eq("blog_id",0);
        replyQueryWrapper.eq("replyed_id",0);
        List<Reply> replyList = list(replyQueryWrapper);
        return replyList;
    }

    @Override
    public boolean deleteReply(Reply reply) {
        QueryWrapper<Reply> replyQueryWrapper = new QueryWrapper<>();
        replyQueryWrapper.eq("reply_id",reply.getReply_id());
        return remove(replyQueryWrapper);
    }

    @Override
    public List<Reply> getListByReplyedId(long replyed_id) {
        QueryWrapper<Reply> replyQueryWrapper = new QueryWrapper<>();
        replyQueryWrapper.eq("replyed_id",replyed_id);
        return list(replyQueryWrapper);
    }
}
