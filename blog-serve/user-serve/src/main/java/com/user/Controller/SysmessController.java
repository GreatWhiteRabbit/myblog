package com.user.Controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.Service.MyRedisService;
import com.user.Service.SysmessService;
import com.user.Utils.Result;
import com.user.Utils.Constant;
import com.user.entity.Sysmess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RequestMapping("system")
@Controller
@ResponseBody
public class SysmessController {
    @Autowired
    private SysmessService sysmessService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private MyRedisService myRedisService;

    @Autowired
    private ObjectMapper objectMapper;
    private Result result = new Result();

    // 添加系统公告
    @PostMapping()
    public Result addSystemInfo(@RequestBody Sysmess systemMessage) {
        boolean b = sysmessService.addInfo(systemMessage);
        return result.ok(b);
    }

    // 管理员获取所有的系统公告
    @GetMapping("get/{page}/{size}")
    public Result getAll(@PathVariable("page") int page, @PathVariable("size") int size) {
        IPage<Sysmess> page1 = sysmessService.getPage(page, size);
        return result.ok(page1);
    }

    // 删除系统公告
    @DeleteMapping("/{id}")
    public Result remove(@PathVariable("id") int id) {
        boolean b = sysmessService.removeById(id);
        stringRedisTemplate.delete(Constant.systemInfo);
        return result.ok(b);
    }

    // 设置系统公告是否显示到首页
    @PostMapping("/{id}/{sys_show}")
    public Result updateShow(@PathVariable("id") int id,@PathVariable("sys_show") boolean sys_show) {
        boolean b = sysmessService.updateShow(id, sys_show);
        stringRedisTemplate.delete(Constant.systemInfo);
        return result.ok(b);
    }

    // 修改系统公告
    @PutMapping("/update")
    public Result update(@RequestBody Sysmess sysmess) {
        boolean b = sysmessService.updateInfo(sysmess);
        stringRedisTemplate.delete(Constant.systemInfo);
        return result.ok(b);
    }

    // 获取能够首页显示的系统公告
    @GetMapping("/getSystemInfo")
    public Result getSystemInfo() throws JsonProcessingException {
        // 首先从Redis中获取数据
        String json = stringRedisTemplate.opsForValue().get(Constant.systemInfo);
        if(json == null) {
            // Redis中不存在数据，从MySQL中找
            Sysmess systemInfo = sysmessService.getSystemInfo();
            if(systemInfo == null) {
                return result.ok();
            }
            //将实体类转成字符串
            String string = objectMapper.writeValueAsString(systemInfo);
            // 将字符串写入redis
            stringRedisTemplate.opsForValue().set(Constant.systemInfo,string);
            // 设置过期时间
            setExpireTime(Constant.systemInfo,Constant.systemInfoExpireTime);
            return result.ok(systemInfo);
        }
      // 将Redis中的数据返回
        else {
            Sysmess sysmess = objectMapper.readValue(json, Sysmess.class);
            return result.ok(sysmess);
        }
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
