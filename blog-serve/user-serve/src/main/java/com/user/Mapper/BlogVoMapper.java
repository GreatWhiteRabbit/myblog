package com.user.Mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.baomidou.mybatisplus.core.metadata.IPage;


import com.user.Vo.BlogVo;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface BlogVoMapper extends BaseMapper<BlogVo> {

    IPage<BlogVo> selectBlogByPage(IPage<BlogVo> blogVoIPage);
    BlogVo selectBlogById(@Param("blog_id") long blog_id);

    List<BlogVo> getBlogShow();
}
