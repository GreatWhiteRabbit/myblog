package com.user.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.user.entity.Blog;
import com.user.entity.BlogData;

public interface BlogDataService extends IService<BlogData> {
    boolean add(long blogId, int likes, int browse);
    void deleteById(long blogId);

    void updateBlogData(BlogData blogData);
    BlogData getByBlog(long blog_id);
}
