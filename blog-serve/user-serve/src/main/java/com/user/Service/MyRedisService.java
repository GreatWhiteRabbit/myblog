package com.user.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.user.entity.MyRedis;

public interface MyRedisService extends IService<MyRedis> {


    int getByKeyName(String expireKeyName);
}

