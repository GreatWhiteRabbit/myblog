package com.user.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.user.entity.DeleteReply;

public interface DeleteReplyService extends IService<DeleteReply> {
    boolean add(DeleteReply deleteReply);

    IPage<DeleteReply> getAll(int page, int size);
}
