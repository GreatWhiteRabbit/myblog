package com.user.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.user.Mapper.CategoryBlogMapper;
import com.user.Service.CategoryBlogService;
import com.user.entity.CategoryBlog;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryBlogServiceImpl extends ServiceImpl<CategoryBlogMapper, CategoryBlog> implements CategoryBlogService {
    @Override
    public boolean add(int categoryId, long blogId) {
       CategoryBlog categoryBlog = new CategoryBlog();
       categoryBlog.setCategory_id(categoryId);
       categoryBlog.setBlog_id(blogId);
       return save(categoryBlog);
    }

    @Override
    public void delete(int categoryId, long blogId) {
        QueryWrapper<CategoryBlog> categoryBlogQueryWrapper = new QueryWrapper<>();
        categoryBlogQueryWrapper.eq("blog_id",blogId).eq("category_id",categoryId);
        remove(categoryBlogQueryWrapper);
    }

    @Override
    public List<CategoryBlog> getBlogIdByCategoryId(int category_id) {
        QueryWrapper<CategoryBlog> categoryBlogQueryWrapper = new QueryWrapper<>();
        categoryBlogQueryWrapper.eq("category_id",category_id);
        return list(categoryBlogQueryWrapper);
    }

    @Override
    public CategoryBlog getCategoryId(long blog_id) {
        QueryWrapper<CategoryBlog> categoryBlogQueryWrapper = new QueryWrapper<>();
        categoryBlogQueryWrapper.eq("blog_id",blog_id);
        return getOne(categoryBlogQueryWrapper);
    }

    @Override
    public int getCategorySum(int category_id) {
        QueryWrapper<CategoryBlog> categoryBlogQueryWrapper = new QueryWrapper<>();
        categoryBlogQueryWrapper.eq("category_id",category_id);
        return list(categoryBlogQueryWrapper).size();

    }
}
