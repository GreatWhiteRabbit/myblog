package com.user.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.user.entity.Link;

import java.util.List;

public interface LinkService extends IService<Link> {

    int addLink(Link link);

    boolean updateLink(Link link);

    List<Link> getMeetCondition();

    IPage<Link> getAll(int page, int size);

    // 将友链从首页上显示还是不显示
    boolean removeLink(int id, int show);
}
