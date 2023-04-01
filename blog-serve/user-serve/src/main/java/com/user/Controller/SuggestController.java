package com.user.Controller;

import com.user.Utils.Result;
import com.user.Vo.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/*
* 网站用户给管理员提交网站意见
* */
@Controller
@RequestMapping("/suggestion")
@ResponseBody
public class SuggestController {
    private Result result = new Result();
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // 网站用户提交建议
    @PostMapping("submit/{suggestion}")
    public Result submitSuggestion(@PathVariable("suggestion") String suggestion) {
        stringRedisTemplate.opsForList().leftPush(Constant.suggestion, suggestion);
        return result.ok();
    }

    // 管理员查看所有建议
    @GetMapping("getAll")
    public Result getALL() {
        List<String> range = stringRedisTemplate.opsForList().range(Constant.suggestion, 0, -1);
        return result.ok(range);
    }

    // 删除建议
    @DeleteMapping("{suggestion}")
    public Result deleteSuggest(@PathVariable("suggestion") String suggestion){
        List<String> range = stringRedisTemplate.opsForList().range(Constant.suggestion, 0, -1);
        List<String> newSuggest = new ArrayList<>();
        for(int i = 0; i < range.size(); i++) {
            if( ! range.get(i).equals(suggestion)) {
                newSuggest.add(range.get(i));
            }
        }
        stringRedisTemplate.delete(Constant.suggestion);
        stringRedisTemplate.opsForList().leftPushAll(Constant.suggestion,newSuggest);
        return result.ok();
    }
}
