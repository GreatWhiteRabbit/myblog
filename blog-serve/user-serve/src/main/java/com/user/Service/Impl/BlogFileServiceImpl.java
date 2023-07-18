package com.user.Service.Impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.user.Mapper.BlogFileMapper;
import com.user.Service.BlogFileService;
import com.user.entity.BlogFile;
import org.springframework.stereotype.Service;

@Service
public class BlogFileServiceImpl extends ServiceImpl<BlogFileMapper, BlogFile> implements BlogFileService {
}
