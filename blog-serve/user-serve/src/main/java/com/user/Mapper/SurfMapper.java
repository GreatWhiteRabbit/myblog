package com.user.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.user.entity.Surf;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.Map;

@Mapper
public interface SurfMapper extends BaseMapper<Surf> {
    
}
