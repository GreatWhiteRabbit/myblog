package com.user.Controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.core.JsonProcessingException;

import com.user.Service.HomesService;
import com.user.Service.MyRedisService;
import com.user.Utils.RedisHelper;
import com.user.Utils.Result;
import com.user.Utils.Constant;
import com.user.entity.Homes;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RequestMapping("home")
@Controller
@ResponseBody
public class HomesController {

    @Autowired
    private HomesService homesService;
    @Autowired
    private MyRedisService myRedisService;

    // 统一结果返回类
    private final Result result = new Result();

    // 自定义封装的Redis工具类
    private final RedisHelper redisHelper = new RedisHelper();

    // 添加主页轮播
    @PostMapping
    public Result add(@RequestBody Homes homes) {
        boolean add = homesService.add(homes);
        return result.ok(add);
    }

    // 修改图片是否首页显示
    @PostMapping("/{id}/{home_show}")
    public Result update(@PathVariable("id") int id, @PathVariable("home_show") int home_show) {
        boolean b = homesService.updateShow(id, home_show);
        // 删除Redis中的轮播信息
        redisHelper.delete(Constant.homeImage);
        return  result.ok(b);
    }

    // 获取满足条件的图片到首页显示
    @GetMapping("/get")
    public Result get() throws JsonProcessingException {
        // 首先从Redis中查找，如果Redis中不存在，再从MySQL中查找
        int size =  redisHelper.getListSize(Constant.homeImage).intValue();
        if(size == 0) {
            // 从MySQL中查找
            List<Homes> homes = homesService.get();
            // 将homes存入到Redis中
            redisHelper.setLeftPushAllKey(Constant.homeImage,homes,-1);
            // 设置过期时间
            setExpireTime(Constant.homeImage,Constant.homeImageExpireTime);
            // 返回数据
            return result.ok(homes);
        } else {
            // 从Redis中获取数据
            List<Homes> homesList = redisHelper.getListRange(Constant.homeImage,0,-1,Homes.class);
            return result.ok(homesList);
        }

    }

    // 管理员获取所有轮播信息
    @GetMapping("/{page}/{size}")
    public Result getAll(@PathVariable("page") int page, @PathVariable("size") int size) {
        IPage<Homes> all = homesService.getAll(page, size);
        return result.ok(all);
    }

    // 设置过期时间
    private void setExpireTime(String keyName,String expireKeyName) {
        // 首先查找过期时间
        Integer expireTime = redisHelper.getStringObject(expireKeyName, Integer.class);
        if(expireTime == null) {
            // 从MySQL中查找过期时间
            expireTime = myRedisService.getByKeyName(expireKeyName);
            // 将过期时间存到Redis中
            redisHelper.setStringKey(expireKeyName,expireTime,-1);
        }
        // 为keyName设置过期时间
        // 转换时间单位
        int seconds = expireTime * 24 * 60 * 60;
        redisHelper.setExpire(keyName,seconds);
    }
}
