package com.user.Controller;

import com.user.Utils.Result;
import com.user.Vo.RedisAnalyze;
import com.user.Vo.RedisAnalyzePage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Controller
@ResponseBody
@RequestMapping("redis")

public class RedisAnalyzeController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    private Result result = new Result();

    // 获取所有key
    @GetMapping("getAll")
    public Result getAll(@RequestParam int page, @RequestParam int size) {
        Set<String> scan = scan("");
        List<String> list = new ArrayList<>(scan);
        List<RedisAnalyze> redisAnalyzeList = new ArrayList<>();
        int listSize = list.size();
        if((page - 1) * size >listSize) return result.ok();
        int firstIndex,lastIndex;
        firstIndex = (page - 1) * size;
        if(page * size < listSize) lastIndex = page * size;
        else lastIndex = listSize;
        for (int i = firstIndex; i < lastIndex; i++) {
            String s = list.get(i);
            RedisAnalyze analyze = getKeyInfo(s);
            redisAnalyzeList.add(analyze);
        }
        return result.ok(new RedisAnalyzePage(redisAnalyzeList,listSize));
    }

    // 根据前缀获取
    @GetMapping("prefix")
    public Result getByPrefix(@RequestParam String prefix) {
        Set<String> scan = scan(prefix);
        ArrayList<String> strings = new ArrayList<>(scan);
        List<RedisAnalyze> redisAnalyzeList = new ArrayList<>();
        for (String s : strings) {
            RedisAnalyze analyze = getKeyInfo(s);
            redisAnalyzeList.add(analyze);
        }
        return result.ok(redisAnalyzeList);
    }



    // 删除key
    @GetMapping("delete")
    public Result deleteKey(@RequestParam String key) {
        Boolean delete = stringRedisTemplate.delete(key);
        return result.ok(delete);
    }

    // 修改key的过期时间
    @GetMapping("updateExpire")
    public Result updateExpire(@RequestParam String key, @RequestParam long expireTime) {

        Boolean expire = stringRedisTemplate.expire(key, expireTime, TimeUnit.DAYS);

        return result.ok(expire);
    }

    // 获取key的相关信息,内部方法使用
    public RedisAnalyze getKeyInfo(String s) {
        RedisAnalyze analyze = new RedisAnalyze();
        DataType type = stringRedisTemplate.type(s);
        Long expire = stringRedisTemplate.getExpire(s);
        assert type != null;
        analyze.setType(type.name());
        analyze.setKey(s);
        analyze.setExpireTime(expire);
        analyze.setValue(getValue(s,type));
        return analyze;
    }

    // 获取每个key中字符串的长度
    public int getValue(String key, DataType type){
       if(type.name().equals("STRING")) {
           return stringRedisTemplate.opsForValue().get(key).length();
       } else if(type.name().equals("ZSET")) {
           int size = stringRedisTemplate.opsForZSet().size(key).intValue();
           return size * 2;
       } else {
           List<String> range = stringRedisTemplate.opsForList().range(key, 0, -1);
           int sum = 0;
           for(int i = 0; i < range.size(); i++) {
               sum = range.get(i).length() + sum;
           }
           return sum;
       }
    }

    // 获取Redis中所有的key
    public Set<String> scan(String matchKey) {
        Set<String> keys = (Set<String>) redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            Set<String> keysTmp = new HashSet<>();
            Cursor<byte[]> cursor = connection.scan(new ScanOptions.ScanOptionsBuilder().match("*" + matchKey + "*").count(1000).build());
            while (cursor.hasNext()) {
                keysTmp.add(new String(cursor.next()));
            }
            return keysTmp;
        });

        return keys;
    }

}
