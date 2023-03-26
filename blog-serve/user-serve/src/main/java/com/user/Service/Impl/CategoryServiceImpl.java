package com.user.Service.Impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.user.Mapper.CategoryMapper;
import com.user.Service.CategoryService;
import com.user.Vo.BlogVo;
import com.user.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
   @Autowired
   private CategoryMapper categoryMapper;
    @Override
    public int addCategory(BlogVo blogVo) {
        Category category = new Category();
        category.setCategory_name(blogVo.getCategory_name());
        // 先查询分类是否存在
        QueryWrapper<Category> categoryQueryWrapper = new QueryWrapper<>();
        categoryQueryWrapper.eq("category_name",blogVo.getCategory_name());
        Category one = getOne(categoryQueryWrapper);
        if(one != null) {
            return one.getCategory_id();
        }
        categoryMapper.insert(category);
       return category.getCategory_id();
    }

    @Override
    public void deleteById(int categoryId) {
        QueryWrapper<Category> categoryQueryWrapper = new QueryWrapper<>();
        categoryQueryWrapper.eq("category_id",categoryId);
        remove(categoryQueryWrapper);
    }

    @Override
    public boolean updateCategory(int category_id, String category_name) {
        Category category = new Category();
        category.setCategory_name(category_name);
        category.setCategory_id(category_id);
        QueryWrapper<Category> categoryQueryWrapper = new QueryWrapper<>();
        categoryQueryWrapper.eq("category_id",category_id);
        return update(category,categoryQueryWrapper);
    }

    @Override
    public String getCategoryById(int category_id) {
       QueryWrapper<Category> categoryQueryWrapper = new QueryWrapper<>();
       categoryQueryWrapper.eq("category_id",category_id);
        return getOne(categoryQueryWrapper).getCategory_name();
    }
}
