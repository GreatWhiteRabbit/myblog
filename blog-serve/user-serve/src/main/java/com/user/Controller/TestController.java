package com.user.Controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.Service.TestService;
import com.user.Utils.Result;
import com.user.entity.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("test")
@Controller
@ResponseBody
public class TestController {
    @Autowired
    private TestService testService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ObjectMapper objectMapper;
    private Result result = new Result();

    @GetMapping("/all")
    public Result getAll() throws JsonProcessingException {
        int test = stringRedisTemplate.opsForList().size("test").intValue();
        if(test == 0) {
            List<Test> list = testService.list();
            List<String> stringList = new ArrayList<>();
            for (Test test1 : list) {
                String s = objectMapper.writeValueAsString(test1);
                stringList.add(s);
            }
            stringRedisTemplate.opsForList().rightPushAll("test",stringList);
            return result.ok(list);
        } else {
            List<Test> testList = new ArrayList<>();
            List<String> test1 = stringRedisTemplate.opsForList().range("test", 0, -1);
            for (String s : test1) {
                Test test2 = objectMapper.readValue(s, Test.class);
                testList.add(test2);
            }
            return result.ok(testList);
        }
    }
}
