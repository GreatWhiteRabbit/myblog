package com.user.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.user.entity.Users;


public interface UsersService extends IService<Users> {
   boolean  addUser(Users users);

    Users getByUserId(long user_id);

    Users isExist(String email);

    Users getUserByEmail(String user_email);

    IPage<Users> getAll(int page, int size);

    boolean deteteUser(long user_id);
}
