package com.user.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.user.Vo.BlogVo;
import com.user.Vo.PreAndNextBlog;
import com.user.entity.Blog;

import java.util.List;

public interface BlogService extends IService<Blog> {
    long addBlog(BlogVo blogVo);
    void deleteById(long blogId);

    boolean updateBlog(BlogVo blogVo);


    IPage<Blog> queryFromMysql(String info, int page, int size);



    List<Long> getAllBlogId();

    String getTitleById(long blog_id);
}
