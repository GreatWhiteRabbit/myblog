package com.user.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.user.Mapper.SysmessMapper;
import com.user.Service.SysmessService;
import com.user.entity.Sysmess;
import org.springframework.stereotype.Service;

@Service
public class SysmessServiceImpl extends ServiceImpl<SysmessMapper, Sysmess> implements SysmessService {
    @Override
    public boolean addInfo(Sysmess systemMessage) {
        systemMessage.setSys_show(false);
        return save(systemMessage);
    }

    @Override
    public IPage<Sysmess> getPage(int page, int size) {
        Page<Sysmess> page1 = new Page<>(page,size);
        IPage<Sysmess> iPage = page(page1);
        return iPage;
    }

    @Override
    public boolean updateShow(int id,boolean show) {
        UpdateWrapper<Sysmess> sysmessUpdateWrapper = new UpdateWrapper<>();
        sysmessUpdateWrapper.set("sys_show",show);
        sysmessUpdateWrapper.eq("id",id);
        return update(null,sysmessUpdateWrapper);
    }

    @Override
    public boolean updateInfo(Sysmess sysmess) {
        UpdateWrapper<Sysmess> sysmessUpdateWrapper = new UpdateWrapper<>();
        sysmessUpdateWrapper.eq("id",sysmess.getId());
        sysmessUpdateWrapper.set("title",sysmess.getTitle());
        sysmessUpdateWrapper.set("content",sysmess.getContent());
        return update(null, sysmessUpdateWrapper);
    }

    @Override
    public Sysmess getSystemInfo() {
        QueryWrapper<Sysmess> sysmessQueryWrapper = new QueryWrapper<>();
        sysmessQueryWrapper.eq("sys_show",true);
        return getOne(sysmessQueryWrapper);
    }
}
