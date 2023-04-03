package com.user.Controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.Service.MyRedisService;
import com.user.Service.ProjectService;
import com.user.Utils.Result;
import com.user.Utils.Constant;
import com.user.entity.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RequestMapping("project")
@Controller
@ResponseBody
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private MyRedisService myRedisService;

    private Result result = new Result();

    // 获取所有项目
    @GetMapping("getAll")
    public Result getAll() {
        List<Project> list = projectService.list();
        return result.ok(list);
    }
    // 获取所有满足条件的项目
    @GetMapping("getCondition")
    public Result getCondition() throws JsonProcessingException {
        int size = stringRedisTemplate.opsForList().size(Constant.project).intValue();
        if(size == 0) {
            List<Project> condition = projectService.getCondition();
            List<String> stringList = new ArrayList<>();
            for (Project project : condition) {
                String s = objectMapper.writeValueAsString(project);
                stringList.add(s);
            }
            stringRedisTemplate.opsForList().leftPushAll(Constant.project,stringList);
            setExpireTime(Constant.project,Constant.projectExpireTime);
            return result.ok(condition);
        }
        else {
            List<String> range = stringRedisTemplate.opsForList().range(Constant.project, 0, -1);
           List<Project> projectList = new ArrayList<>();
            for (String s : range) {
                Project project = objectMapper.readValue(s, Project.class);
                projectList.add(project);
            }
            return result.ok(projectList);
        }


    }
    // 添加项目
    @PostMapping
    public Result addProject(@RequestBody Project project) {
        boolean b = projectService.addProject(project);
        return result.ok(b);
    }

    // 删除项目
    @DeleteMapping
    public Result deleteProject(@RequestParam int id) {
        boolean b = projectService.removeById(id);
        if(b) {
            stringRedisTemplate.delete(Constant.project);
        }
        return result.ok(b);
    }

    // 修改是否首页显示
    @GetMapping("updateShow")
    public Result updateShow(@RequestParam int id, @RequestParam int project_show){
        boolean b = projectService.updateShow(id, project_show);
        if(b) {
            stringRedisTemplate.delete(Constant.project);
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
