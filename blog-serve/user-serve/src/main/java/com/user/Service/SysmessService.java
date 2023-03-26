package com.user.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.user.entity.Sysmess;

public interface SysmessService extends IService<Sysmess> {
    boolean addInfo(Sysmess systemMessage);

    IPage<Sysmess> getPage(int page, int size);

    boolean updateShow(int id,boolean show);

    boolean updateInfo(Sysmess sysmess);

    Sysmess getSystemInfo();
}
