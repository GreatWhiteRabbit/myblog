package com.user.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.Service.UserInfoService;
import com.user.Service.UsersService;
import com.user.Utils.Result;
import com.user.Vo.AdminInfo;
import com.user.Vo.Constant;
import com.user.Vo.User;
import com.user.entity.UserInfo;
import com.user.entity.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/userInfo")
@ResponseBody
public class UserInfoController {
    @Autowired
    private UsersService usersService;
    @Autowired
    private UserInfoService userInfoService;

    private Result result = new Result();

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    // 管理员获取个人信息
    @GetMapping ("/get")
    public Result getInfo(@RequestParam String user_id) {
        long id = Long.parseLong(user_id);
        Users users =  usersService.getByUserId(id);
        UserInfo userInfo = userInfoService.getByUserId(id);
        User user = new User();
        user.setUser_id(id);
        user.setUser_account(userInfo.getUser_account());
        user.setUser_birthday(userInfo.getUser_birthday());
        user.setUser_description(userInfo.getUser_description());
        user.setUser_email(users.getUser_email());
        user.setUser_image(userInfo.getUser_image());
        user.setUser_name(users.getUser_name());
        user.setUser_password(userInfo.getUser_password());
        user.setUser_status(users.getUser_status());
        user.setUser_sex(userInfo.getUser_sex());

        return result.ok(user);
    }

    @PostMapping
    public Result Login(@RequestBody UserInfo userInfo) {
       return userInfoService.Login(userInfo);
    }

    @PutMapping
    public Result updateInfo(@RequestBody UserInfo userInfo) {
        return userInfoService.updateInfo(userInfo);
    }

    // 获取网站管理员信息，包括描述、头像
    @GetMapping("/getAdminInfo/{user_id}")
    public Result getAdminInfo(@PathVariable Long user_id) throws JsonProcessingException {
        String json = stringRedisTemplate.opsForValue().get(Constant.adminInfo + user_id);
        if(json == null) {
            UserInfo userInfo = userInfoService.getByUserId(user_id);
            AdminInfo adminInfo = new AdminInfo();
            adminInfo.setUser_description(userInfo.getUser_description());
            adminInfo.setUser_image(userInfo.getUser_image());

            String string = objectMapper.writeValueAsString(adminInfo);
            stringRedisTemplate.opsForValue().set(Constant.adminInfo + user_id,string);
            return result.ok(adminInfo);
        } else {
            AdminInfo adminInfo = objectMapper.readValue(json, AdminInfo.class);
            return result.ok(adminInfo);
        }

    }
}
