package com.user.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.user.Mapper.EssayMapper;
import com.user.Service.EssayService;
import com.user.entity.Essay;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class EssayServiceImpl extends ServiceImpl<EssayMapper, Essay> implements EssayService {
    @Override
    public boolean addEssay(Essay essay) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        essay.setCreate_time(timestamp);
        essay.setEssay_show(0);
        return save(essay);
    }

    @Override
    public boolean updateEssay(Essay essay) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        essay.setCreate_time(timestamp);
        return updateById(essay);
    }

    @Override
    public IPage<Essay> getAll(int page, int size) {
        Page<Essay> essayPage = new Page<>(page,size);
        IPage<Essay> essayIPage = page(essayPage);
        return essayIPage;
    }

    @Override
    public List<Essay> getByCondition() {
        QueryWrapper<Essay> essayQueryWrapper = new QueryWrapper<>();
        essayQueryWrapper.eq("essay_show",1);
        return list(essayQueryWrapper);
    }

    @Override
    public boolean updateShow(int id, int show) {
        UpdateWrapper<Essay> essayUpdateWrapper = new UpdateWrapper<>();
        essayUpdateWrapper.set("essay_show",show);
        essayUpdateWrapper.eq("id",id);
        return update(null, essayUpdateWrapper);
    }
}
