package com.user.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.user.entity.labelBlog;

import java.util.List;

public interface LabelBlogService extends IService<labelBlog> {
    boolean add(int labelId, long blogId);
    void delete(int labelId,long blogId);

    List<labelBlog> getBlogIdByLabelId(int label_id);

    labelBlog getLabelId(long blog_id);

    // 获取label_id对应多少个blog
    int getLabelSum(int label_id);
}
