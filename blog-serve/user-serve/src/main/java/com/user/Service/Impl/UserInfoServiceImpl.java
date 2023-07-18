package com.user.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.user.Mapper.UserInfoMapper;
import com.user.Service.UserInfoService;
import com.user.Utils.Result;
import com.user.Utils.TokenUtil;
import com.user.entity.UserInfo;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {
    @Override
    public boolean addUserInfo(UserInfo userInfo) {
        return save(userInfo);
    }

    private Result result = new Result();
    @Override
    public UserInfo getByUserId(long user_id) {
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("user_id",user_id);
        return getOne(userInfoQueryWrapper);
    }

    @Override
    public Result Login(UserInfo userInfo) {
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("user_password",userInfo.getUser_password())
                .eq("user_account",userInfo.getUser_account());
        UserInfo info = getOne(userInfoQueryWrapper);
        if(info != null) {
            TokenUtil tokenUtil = new TokenUtil();
            String token = tokenUtil.creatToken(info.getUser_account(),info.getUser_password(), "3");
            return result.ok(info.getUser_id() + "@" + token);
        } else {
            return result.fail("账号密码错误");
        }


    }

    @Override
    public Result updateInfo(UserInfo userInfo) {
        long id = userInfo.getUser_id();
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("user_id",id);
        UserInfo userInfo1 = new UserInfo();
        userInfo1.setUser_id(id);
        userInfo1.setUser_birthday(userInfo.getUser_birthday());
        userInfo1.setUser_sex(userInfo.getUser_sex());
        userInfo1.setUser_password(userInfo.getUser_password());
        userInfo1.setUser_account(userInfo.getUser_account());
        userInfo1.setUser_description(userInfo.getUser_description());
        userInfo1.setUser_image(userInfo.getUser_image());
        boolean update = update(userInfo1, userInfoQueryWrapper);
        if(update) {
            return result.ok("修改成功");
        } else {
            return result.fail("修改失败");
        }
    }
}
