package com.user.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.user.Vo.BlogVo;
import com.user.entity.Category;

public interface CategoryService extends IService<Category> {


    int addCategory(BlogVo blogVo);

    void  deleteById(int categoryId);

    boolean updateCategory(int category_id, String category_name);

    String getCategoryById(int category_id);


}
