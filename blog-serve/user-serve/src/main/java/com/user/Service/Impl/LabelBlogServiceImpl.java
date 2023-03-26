package com.user.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.user.Mapper.LabelBlogMapper;
import com.user.Service.LabelBlogService;
import com.user.entity.labelBlog;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabelBlogServiceImpl extends ServiceImpl<LabelBlogMapper, labelBlog> implements LabelBlogService {
    @Override
    public boolean add(int labelId, long blogId) {
       labelBlog labelBlog = new labelBlog();
       labelBlog.setBlog_id(blogId);
       labelBlog.setLabel_id(labelId);
       return save(labelBlog);
    }

    @Override
    public void delete(int labelId, long blogId) {
        QueryWrapper<labelBlog> labelBlogQueryWrapper = new QueryWrapper<>();
        labelBlogQueryWrapper.eq("label_id",labelId).eq("blog_id",blogId);
        remove(labelBlogQueryWrapper);
    }

    // 根据label_id获取所有的blog_id
    @Override
    public List<labelBlog> getBlogIdByLabelId(int label_id) {
        QueryWrapper<labelBlog> labelBlogQueryWrapper = new QueryWrapper<>();
        labelBlogQueryWrapper.eq("label_id",label_id);
        List<labelBlog> list = list(labelBlogQueryWrapper);
        return list;
    }

    @Override
    public labelBlog getLabelId(long blog_id) {

        QueryWrapper<labelBlog> labelBlogQueryWrapper = new QueryWrapper<>();
        labelBlogQueryWrapper.eq("blog_id",blog_id);
        return getOne(labelBlogQueryWrapper);
    }

    @Override
    public int getLabelSum(int label_id) {
        QueryWrapper<labelBlog> labelBlogQueryWrapper = new QueryWrapper<>();
        labelBlogQueryWrapper.eq("label_id",label_id);
        return list(labelBlogQueryWrapper).size();
    }
}
