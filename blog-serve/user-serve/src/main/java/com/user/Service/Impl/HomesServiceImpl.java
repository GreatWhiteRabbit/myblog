package com.user.Service.Impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.user.Mapper.HomesMapper;
import com.user.Service.HomesService;
import com.user.entity.Homes;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HomesServiceImpl extends ServiceImpl<HomesMapper, Homes> implements HomesService {
    @Override
    public boolean add(Homes homes) {
            homes.setHome_show(0);
        return save(homes);
    }

    @Override
    public boolean updateShow(int id, int show) {
        UpdateWrapper<Homes> homesUpdateWrapper = new UpdateWrapper<>();
        homesUpdateWrapper.set("home_show",show);
        homesUpdateWrapper.eq("id",id);
        return update(null, homesUpdateWrapper);
    }

    @Override
    public List<Homes> get() {
        QueryWrapper<Homes> homesQueryWrapper = new QueryWrapper<>();
        homesQueryWrapper.eq("home_show",1);
        return list(homesQueryWrapper);
    }

    @Override
    public IPage<Homes> getAll(int page, int size) {
        Page<Homes> page1 = new Page<>(page,size);
        return page(page1);
    }
}
