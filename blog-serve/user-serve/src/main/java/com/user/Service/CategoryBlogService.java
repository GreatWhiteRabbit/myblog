package com.user.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.user.entity.CategoryBlog;

import java.util.List;

public interface CategoryBlogService extends IService<CategoryBlog> {
    boolean add(int categoryId, long blogId);
    void delete(int categoryId,long blogId);

    List<CategoryBlog> getBlogIdByCategoryId(int category_id);
    CategoryBlog getCategoryId(long blog_id);

    // 获取category_id对应多少个blog
    int getCategorySum(int category_id);
}
