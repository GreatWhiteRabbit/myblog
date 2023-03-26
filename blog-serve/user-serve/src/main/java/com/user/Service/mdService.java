package com.user.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.user.entity.md;


public interface mdService extends IService<md> {
    boolean addMarkDown(long md_id, String md_content);

    void deleteById(long blogId);
    String getContent(long blog_id);
}
