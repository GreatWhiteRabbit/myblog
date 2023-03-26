package com.user.Controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.Service.EssayService;
import com.user.Service.MyRedisService;
import com.user.Utils.Result;
import com.user.Vo.Constant;
import com.user.Vo.EssayVo;
import com.user.entity.Essay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RequestMapping("essay")
@Controller
@ResponseBody
public class EssayController {
    @Autowired
    private EssayService essayService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MyRedisService myRedisService;

    private Result result = new Result();

    // 添加随笔
    @PostMapping
    public Result addEssay(@RequestBody Essay essay) {
        boolean b = essayService.addEssay(essay);
        return result.ok(b);
    }

    // 修改随笔
    @PutMapping
    public Result updateEssay(@RequestBody Essay essay) {
        boolean b = essayService.updateEssay(essay);
        if(b) {
            stringRedisTemplate.delete(Constant.Essay);
        }
        return result.ok(b);
    }

    // 管理员分页查看所有随笔
    @GetMapping("getAll")
    public Result getAll(@RequestParam int page, @RequestParam int size) {
        IPage<Essay> all = essayService.getAll(page, size);
        return result.ok(all);
    }



    // 用户查看可以显示的随笔
    @GetMapping("show")
    public Result getByCondition(@RequestParam int page, @RequestParam int size) throws JsonProcessingException {
        // 首先从Redis中查找是否存在随笔
        int listSize = stringRedisTemplate.opsForList().size(Constant.Essay).intValue();
        if(listSize == 0) {
            // 从MySQL中查找
            List<Essay> byCondition = essayService.getByCondition();
            int getSize = byCondition.size();
            // 没有获取到数据直接返回
            if(getSize < (page - 1) * size) return result.ok();
            // 获取返回集合的起始下标
            int firstIndex,lastIndex;
            firstIndex = (page - 1) * size;
            if(getSize < page * size) {
                lastIndex = getSize;
            } else {
                lastIndex = page * size;
            }
            List<String> stringList = new ArrayList<>();
            // 需要返回的集合
            List<Essay> essayList = new ArrayList<>();
            // 返回的包装类
            EssayVo essayVo = new EssayVo();
            for(int i = 0; i < getSize; i++) {
                // 将在firstIndex和lastIndex范围内的数据返回，其余的数据存放的Redis中
                Essay one = byCondition.get(i);
                if(i >= firstIndex && i < lastIndex) {
                    essayList.add(one);
                }
                // 将one转成json
                String json = objectMapper.writeValueAsString(one);
                stringList.add(json);
            }
            stringRedisTemplate.opsForList().leftPushAll(Constant.Essay,stringList);
            setExpireTime(Constant.Essay,Constant.EssayExpireTime);
            essayVo.setTotal(getSize);
            essayVo.setEssayList(essayList);
            return result.ok(essayVo);
        }
        else {
            List<String> range = stringRedisTemplate.opsForList().range(Constant.Essay, 0, -1);
            // 返回Redis中的结果
            int firstIndex,lastIndex;
            // 请求数大于总数直接返回
            if((page - 1 ) * size > listSize) return result.ok();
            firstIndex = (page - 1) * size;
            if(page * size >listSize) lastIndex = listSize;
            else lastIndex = page * size;
            EssayVo essayVo = new EssayVo();
            List<Essay> essayList = new ArrayList<>();
            for(int i = firstIndex; i < lastIndex; i++) {
                Essay essay = objectMapper.readValue(range.get(i), Essay.class);
                essayList.add(essay);
            }
            essayVo.setEssayList(essayList);
            essayVo.setTotal(listSize);
            return result.ok(essayVo);
        }

    }

    // 修改随笔是否首页显示
    @GetMapping("updateShow")
    public Result updateShow(@RequestParam int id, @RequestParam int essay_show){
        boolean b = essayService.updateShow(id, essay_show);
        if(b) {
            stringRedisTemplate.delete(Constant.Essay);
        }
        return result.ok(b);
    }

    // 删除随笔
    @DeleteMapping
    public Result deleteEssay(@RequestParam int id) {
        boolean b = essayService.removeById(id);
        if(b) {
            stringRedisTemplate.delete(Constant.Essay);
        }
        return result.ok(b);
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
