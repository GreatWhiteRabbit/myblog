package com.user.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.user.Mapper.BlogDataMapper;
import com.user.Service.BlogDataService;
import com.user.entity.BlogData;
import org.springframework.stereotype.Service;

@Service
public class BlogDataServiceImpl extends ServiceImpl<BlogDataMapper, BlogData> implements BlogDataService {

    @Override
    public boolean add(long blogId, int likes, int browse) {
        BlogData blogData = new BlogData();
        blogData.setBlog_browse(0);
        blogData.setBlog_id(blogId);
        blogData.setBlog_likes(0);
       return save(blogData);
    }

    @Override
    public void deleteById(long blogId) {
        QueryWrapper<BlogData> blogDataQueryWrapper = new QueryWrapper<>();
        blogDataQueryWrapper.eq("blog_id",blogId);
        remove(blogDataQueryWrapper);
    }

    @Override
    public void updateBlogData(BlogData blogData) {
        QueryWrapper<BlogData> blogDataQueryWrapper = new QueryWrapper<>();
        blogDataQueryWrapper.eq("blog_id",blogData.getBlog_id());
        update(blogData,blogDataQueryWrapper);
    }

    @Override
    public BlogData getByBlog(long blog_id) {
        QueryWrapper<BlogData> blogDataQueryWrapper = new QueryWrapper<>();
        blogDataQueryWrapper.eq("blog_id",blog_id);
        return getOne(blogDataQueryWrapper);
    }
}
