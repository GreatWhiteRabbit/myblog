package com.user.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.user.Mapper.BlogMapper;
import com.user.Service.BlogService;
import com.user.Vo.BlogVo;
import com.user.Vo.PreAndNextBlog;
import com.user.entity.Blog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService {
    @Autowired
   private BlogMapper blogMapper;

    @Override
    public long addBlog( BlogVo blogVo) {
        Blog blog = new Blog();
        blog.setBlog_content(blogVo.getBlog_content());
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        blog.setBlog_date(timestamp);
        blog.setBlog_cover(blogVo.getBlog_cover());
        blog.setBlog_description(blogVo.getBlog_description());
        blog.setBlog_show(blogVo.isBlog_show());
        blog.setBlog_title(blogVo.getBlog_title());
        blog.setUser_id(blogVo.getUser_id());
         blogMapper.insert(blog);
         return blog.getBlog_id();
    }
    @Override
    public void deleteById(long blogId) {
        QueryWrapper<Blog> blogQueryWrapper = new QueryWrapper<>();
        blogQueryWrapper.eq("blog_id",blogId);
        remove(blogQueryWrapper);
    }

    @Override
    public boolean updateBlog(BlogVo blogVo) {
        long blog_id = blogVo.getBlog_id();
        QueryWrapper<Blog> blogQueryWrapper = new QueryWrapper<>();
        blogQueryWrapper.eq("blog_id",blog_id);
        Blog blog = new Blog();

        blog.setBlog_cover(blogVo.getBlog_cover());
        blog.setBlog_date(blogVo.getBlog_date());
        blog.setBlog_show(blogVo.isBlog_show());
        blog.setBlog_title(blogVo.getBlog_title());
        blog.setBlog_description(blogVo.getBlog_description());
        blog.setUser_id(blogVo.getUser_id());
        blog.setBlog_content(blogVo.getBlog_content());


        return update(blog,blogQueryWrapper);
    }

    @Override
    public IPage<Blog> queryFromMysql(String info, int page, int size) {
        QueryWrapper<Blog> blogQueryWrapper = new QueryWrapper<>();
        blogQueryWrapper.like("blog_title",info);
        Page<Blog> blogPage = new Page<>(page,size);
        IPage<Blog> blogIPage = page(blogPage,blogQueryWrapper);
        return blogIPage;
    }



    @Override
    public List<Long> getAllBlogId() {
        QueryWrapper<Blog> blogQueryWrapper = new QueryWrapper<>();
        blogQueryWrapper.orderBy(true,true,"blog_id");
        blogQueryWrapper.select("blog_id");
        List<Map<String, Object>> maps = listMaps(blogQueryWrapper);
        List<Long> longList = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            Long blog_id = Long.parseLong(map.get("blog_id").toString());
            longList.add(blog_id);
        }
        return longList;
    }

    @Override
    public String getTitleById(long blog_id) {
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("blog_id",blog_id);
        String blog_title = getOne(queryWrapper).getBlog_title();
        return blog_title;
    }

    public List<Integer> getBlogIdList() {
        QueryWrapper<Blog> blogQueryWrapper = new QueryWrapper<>();
        blogQueryWrapper.select("blog_id");
        blogQueryWrapper.orderBy(true,false,"blog_date");
        List<Map<String, Object>> maps = listMaps(blogQueryWrapper);
        List<Integer> integerList = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            int blog_id = Integer.parseInt(map.get("blog_id").toString());
            integerList.add(blog_id);
        }
        return integerList;
    }


}
