package com.user.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.user.Mapper.BlogVoMapper;
import com.user.Service.BlogVoService;
import com.user.Vo.BlogVo;

import com.user.entity.Blog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BlogVoServiceImpl extends ServiceImpl<BlogVoMapper, BlogVo> implements BlogVoService {
    @Autowired
    private BlogVoMapper blogVoMapper;
    @Override
    public IPage<BlogVo> selectBlogByPage(int page, int size) {
        Page<BlogVo> blogVoPage = new Page<>(page,size,true);
        IPage<BlogVo> blogVoIPage;

        blogVoIPage = blogVoMapper.selectBlogByPage(blogVoPage);

        return blogVoIPage;
    }

    @Override
    public BlogVo selectBlogById(long blog_id) {
        return blogVoMapper.selectBlogById(blog_id);
    }

    @Override
    public List<BlogVo> getBlogShow() {
       return blogVoMapper.getBlogShow();
    }



}
