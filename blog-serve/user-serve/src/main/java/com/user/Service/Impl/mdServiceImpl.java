package com.user.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.user.Mapper.mdMapper;
import com.user.Service.mdService;
import com.user.entity.md;
import org.springframework.stereotype.Service;
/*
* Author:
* description: markdown格式存储
* */

@Service
public class mdServiceImpl extends ServiceImpl<mdMapper, md> implements mdService {

    @Override
    public boolean addMarkDown(long md_id, String md_content) {
        md md = new md();
        md.setMd_content(md_content);
        md.setMd_id(md_id);
        return save(md);
    }

    @Override
    public void deleteById(long blogId) {
        QueryWrapper<md> mdQueryWrapper = new QueryWrapper<>();
        mdQueryWrapper.eq("md_id",blogId);
        remove(mdQueryWrapper);
    }

    @Override
    public String getContent(long blog_id) {
        QueryWrapper<md> mdQueryWrapper = new QueryWrapper<>();
        mdQueryWrapper.eq("md_id",blog_id);
        return getOne(mdQueryWrapper).getMd_content();
    }


}
