package com.user.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.user.entity.Users;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UsersMapper extends BaseMapper<Users> {
}
