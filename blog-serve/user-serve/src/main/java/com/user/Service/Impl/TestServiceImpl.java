package com.user.Service.Impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.user.Mapper.TestMapper;
import com.user.Service.TestService;
import com.user.entity.Test;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl extends ServiceImpl<TestMapper, Test> implements TestService {
}
