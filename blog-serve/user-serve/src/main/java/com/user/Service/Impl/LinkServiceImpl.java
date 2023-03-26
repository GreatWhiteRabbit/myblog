package com.user.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.user.Mapper.LinkMapper;
import com.user.Service.LinkService;
import com.user.entity.Link;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class LinkServiceImpl extends ServiceImpl<LinkMapper, Link> implements LinkService {
    @Autowired
    private LinkMapper linkMapper;
    @Override
    public int addLink(Link link) {
        link.setLink_show(0);
        link.setApply_time(new Timestamp(System.currentTimeMillis()));
        int insert = linkMapper.insert(link);
        return insert;
    }

    @Override
    public boolean updateLink(Link link) {
        QueryWrapper<Link> linkQueryWrapper = new QueryWrapper<>();
        linkQueryWrapper.eq("id",link.getId());
        boolean update = update(link,linkQueryWrapper);
        return update;
    }

    @Override
    public List<Link> getMeetCondition() {
        QueryWrapper<Link> linkQueryWrapper = new QueryWrapper<>();
        linkQueryWrapper.eq("link_show",1);
        List<Link> list = list(linkQueryWrapper);
        return list;
    }

    @Override
    public IPage<Link> getAll(int page, int size) {
        Page<Link> linkPage = new Page<>(page,size);
        IPage<Link> linkIPage = page(linkPage);
       return linkIPage;

    }

    @Override
    public boolean removeLink(int id, int show) {
        UpdateWrapper<Link> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("link_show",show);
        updateWrapper.eq("id",id);
        boolean update = update(null, updateWrapper);
        return update;
    }
}
