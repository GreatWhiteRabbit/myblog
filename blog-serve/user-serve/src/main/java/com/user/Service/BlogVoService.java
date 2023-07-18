package com.user.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.user.Vo.BlogVo;

import java.util.List;


public interface BlogVoService extends IService<BlogVo> {
    IPage<BlogVo> selectBlogByPage(int page, int size);
    BlogVo selectBlogById(long blog_id);

    List<BlogVo> getBlogShow();


}
