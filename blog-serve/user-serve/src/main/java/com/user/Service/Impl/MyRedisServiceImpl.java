package com.user.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.user.Mapper.MyRedisMapper;
import com.user.Service.MyRedisService;
import com.user.entity.MyRedis;
import org.springframework.stereotype.Service;

@Service
public class MyRedisServiceImpl extends ServiceImpl<MyRedisMapper, MyRedis> implements MyRedisService {


    // 根据keyName获取过期时间
    @Override
    public int getByKeyName(String keyName) {
        QueryWrapper<MyRedis> myRedisQueryWrapper = new QueryWrapper<>();
        myRedisQueryWrapper.eq("key_name",keyName);
        MyRedis myRedis = getOne(myRedisQueryWrapper);
        return myRedis.getExpire_time();
    }
}
