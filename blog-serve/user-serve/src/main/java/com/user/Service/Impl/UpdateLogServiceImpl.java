package com.user.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.user.Mapper.UpdateLogMapper;
import com.user.Service.UpdateLogService;
import com.user.entity.UpdateLog;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UpdateLogServiceImpl extends ServiceImpl<UpdateLogMapper, UpdateLog>
        implements UpdateLogService {

    @Override
    public boolean addUpdateLog(UpdateLog updateLog) {
        Date date = new Date(System.currentTimeMillis());
        updateLog.setUpdate_time(date);
        return save(updateLog);
    }

    @Override
    public boolean deleteUpdateLog(int id) {
        return removeById(id);
    }

    @Override
    public boolean modify(UpdateLog updateLog) {
        UpdateWrapper<UpdateLog> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id",updateLog.getId());
        updateWrapper.set("update_info",updateLog.getUpdate_info());
        return update(null,updateWrapper);
    }

    @Override
    public List<UpdateLog> getAll() {
        QueryWrapper<UpdateLog> updateLogQueryWrapper = new QueryWrapper<>();
        updateLogQueryWrapper.orderBy(true,false,"update_time");
        return list(updateLogQueryWrapper);
    }
}
