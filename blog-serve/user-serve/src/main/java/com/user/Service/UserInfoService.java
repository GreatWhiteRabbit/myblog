package com.user.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.user.Utils.Result;
import com.user.entity.UserInfo;

public interface UserInfoService extends IService<UserInfo> {
    boolean addUserInfo(UserInfo userInfo);

    UserInfo getByUserId(long user_id);

    Result Login(UserInfo userInfo);

    Result updateInfo(UserInfo userInfo);
}
