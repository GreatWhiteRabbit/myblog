package com.user.Controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.Service.LinkService;
import com.user.Service.MyRedisService;
import com.user.Utils.EntityChange;
import com.user.Utils.Result;
import com.user.Vo.Constant;
import com.user.Vo.LinkVo;
import com.user.entity.Link;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RequestMapping("link")
@Controller
@ResponseBody
public class LinkController {

    @Autowired
    private LinkService linkService;

    @Autowired
    private MyRedisService myRedisService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private Result result = new Result();

    // 添加友链
    @PostMapping()
    public Result addLink(@RequestBody Link link){
        int i = linkService.addLink(link);
        if(i == 0) {
            return result.fail("添加失败，请重试");
        } else {
            return result.ok();
        }
    }

    // 管理员修改友链情况
    @PutMapping()
    public Result updateLink(@RequestBody Link link) {
        boolean update = linkService.updateLink(link);
        if(update) {
            // 修改成功的话需要将Redis中存储友链删除，重新更新
            Long size = stringRedisTemplate.opsForList().size(Constant.links);
            if(size == null) {
                stringRedisTemplate.delete(Constant.links);
            }
            return result.ok();
        } else {
            return result.fail("修改失败");
        }
    }

    // 将友链从首页上下架
    @PostMapping("/remove/{id}/{link_show}")
    public Result remove(@PathVariable("id") int id, @PathVariable("link_show") int link_show) {
        boolean b = linkService.removeLink(id, link_show);
        if(b) {
            Long size = stringRedisTemplate.opsForList().size(Constant.links);
            if(size == null) {
                stringRedisTemplate.delete(Constant.links);
            }
            return result.ok();
        } else {
            return result.fail("修改失败");
        }
    }

    // 将友链删除
    @DeleteMapping("/delete/{id}")
    public Result deleteLink(@PathVariable("id") int id) {

        boolean b = linkService.removeById(id);
        if(b) {
            Long size = stringRedisTemplate.opsForList().size(Constant.links);
            if(size == null) {
                stringRedisTemplate.delete(Constant.links);
            }
            return result.ok();
        } else {
            return result.fail("删除失败");
        }
    }

    // 将所有可以显示的友链查找出来返回到首页
    @GetMapping("/getLink")
    public Result getLink() throws JsonProcessingException {
        // 先从Redis中查找，如果Redis中不存在，那么从MySQL中查询
        int size = stringRedisTemplate.opsForList().size(Constant.links).intValue();
        List<LinkVo> linkVoList = new ArrayList<>();
        if(size == 0) {
            List<Link> meetCondition = linkService.getMeetCondition();
            List<String> stringList = new ArrayList<>();
            int listSize = meetCondition.size();
            if(listSize == 0) {
                return result.ok();
            }
            EntityChange entityChange = new EntityChange();
            for(int i = 0; i < listSize; i++) {
                LinkVo linkVo = entityChange.changeLink_ToLinkVo(meetCondition.get(i));
                linkVoList.add(linkVo);
                // 将linkVoList转成json存到Redis
                String json = objectMapper.writeValueAsString(linkVo);
                stringList.add(json);
            }
            // 将stringList字符串存到Redis中
            stringRedisTemplate.opsForList().leftPushAll(Constant.links,stringList);
            // 设置键值的过期时间
            setExpireTime(Constant.links,Constant.links);
            return result.ok(linkVoList);
        } else {
            // 将查询到的友链返回
            List<String> range = stringRedisTemplate.opsForList().range(Constant.links, 0, -1);
            for(int i = 0; i < size; i++) {
                // 将json字符串转换成相对应的实体类
                LinkVo linkVo = objectMapper.readValue(range.get(i), LinkVo.class);
                linkVoList.add(linkVo);
            }
            return result.ok(linkVoList);
        }
    }

    // 管理员获取所有的友链到后台显示
    @GetMapping("/getAll/{page}/{size}")
    public Result getAll(@PathVariable("page") int page, @PathVariable("size") int size) {
        IPage<Link> linkIPage = linkService.getAll(page, size);
        return result.ok(linkIPage);
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
