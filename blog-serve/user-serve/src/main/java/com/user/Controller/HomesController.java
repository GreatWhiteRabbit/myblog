package com.user.Controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.Service.HomesService;
import com.user.Service.MyRedisService;
import com.user.Utils.Result;
import com.user.Utils.Constant;
import com.user.entity.Homes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RequestMapping("home")
@Controller
@ResponseBody
public class HomesController {

    @Autowired
    private HomesService homesService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MyRedisService myRedisService;

    private Result result = new Result();

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
        stringRedisTemplate.delete(Constant.homeImage);
        return  result.ok(b);
    }

    // 获取满足条件的图片到首页显示
    @GetMapping("/get")
    public Result get() throws JsonProcessingException {
        // 首先从Redis中查找，如果Redis中不存在，再从MySQL中查找
        int size = stringRedisTemplate.opsForList().size(Constant.homeImage).intValue();
        if(size == 0) {
            // 从MySQL中查找
            List<Homes> homes = homesService.get();
            int listSize = homes.size();
            // 将实体类转成json字符串
            List<String> stringList = new ArrayList<>();
            for(int i = 0; i < listSize; i++) {
                String string = objectMapper.writeValueAsString(homes.get(i));
                stringList.add(string);
            }
            // 将json字符串存入到Redis中
            stringRedisTemplate.opsForList().leftPushAll(Constant.homeImage,stringList);
            // 设置过期时间
            setExpireTime(Constant.homeImage,Constant.homeImageExpireTime);
            // 返回数据
            return result.ok(homes);
        } else {
            List<String> range = stringRedisTemplate.opsForList().range(Constant.homeImage, 0, -1);
            int rangeSize = range.size();
            List<Homes> homesList = new ArrayList<>();
            for(int i = 0; i < rangeSize; i++) {
                Homes homes = objectMapper.readValue(range.get(i), Homes.class);
                homesList.add(homes);
            }
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
    public void setExpireTime(String keyName,String expireKeyName) {
        // 首先查找过期时间
        String expire = stringRedisTemplate.opsForValue().get(expireKeyName);
        int expireTime; // 过期时间
        // 如果expire为空，从数据库中查找
        if(expire == null) {
            expireTime = myRedisService.getByKeyName(expireKeyName);
            // 将过期时间存放到Redis中
            stringRedisTemplate.opsForValue().set(expireKeyName, String.valueOf(expireTime));
        } else {
            // 将expire 转成int
            expireTime = Integer.parseInt(expire);
        }
        stringRedisTemplate.expire(keyName,expireTime, TimeUnit.DAYS);
    }
}
