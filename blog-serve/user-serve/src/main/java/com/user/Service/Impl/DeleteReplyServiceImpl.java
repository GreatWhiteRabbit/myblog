package com.user.Service.Impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.user.Mapper.DeleteReplyMapper;
import com.user.Service.DeleteReplyService;
import com.user.entity.DeleteReply;
import org.springframework.stereotype.Service;

@Service
public class DeleteReplyServiceImpl extends ServiceImpl<DeleteReplyMapper, DeleteReply> implements DeleteReplyService {

    @Override
    public boolean add(DeleteReply deleteReply) {
        save(deleteReply);
        return true;
    }

    @Override
    public IPage<DeleteReply> getAll(int page, int size) {
        Page<DeleteReply> deleteReplyPage = new Page<>(page,size);
        return page(deleteReplyPage);
    }
}
