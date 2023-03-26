package com.user.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.user.Mapper.UsersMapper;
import com.user.Service.UsersService;
import com.user.entity.Users;
import org.springframework.stereotype.Service;

@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService {

    @Override
    public boolean addUser(Users users) {
        return save(users);
    }

    @Override
    public Users getByUserId(long user_id) {
        QueryWrapper<Users> usersQueryWrapper = new QueryWrapper<>();
        usersQueryWrapper.eq("user_id",user_id);
        return getOne(usersQueryWrapper);
    }

    // 邮箱存在返回true，否则返回false
    @Override
    public Users isExist(String email) {
        QueryWrapper<Users> usersQueryWrapper = new QueryWrapper<>();
        usersQueryWrapper.eq("user_email",email);
        Users one = getOne(usersQueryWrapper);
        if(one != null) {
            return one;
        } else {
            return null;
        }

    }

    @Override
    public Users getUserByEmail(String user_email) {
        QueryWrapper<Users> usersQueryWrapper = new QueryWrapper<>();
        usersQueryWrapper.eq("user_email",user_email);
        return getOne(usersQueryWrapper);
    }

    @Override
    public IPage<Users> getAll(int page, int size) {
        Page<Users> usersPage = new Page<>(page,size);
        IPage<Users> usersIPage = page(usersPage);
        return usersIPage;
    }

    @Override
    public boolean deteteUser(long user_id) {
        QueryWrapper<Users> usersQueryWrapper = new QueryWrapper<>();
        usersQueryWrapper.eq("user_id",user_id);
        boolean remove = remove(usersQueryWrapper);
        return remove;
    }
}
