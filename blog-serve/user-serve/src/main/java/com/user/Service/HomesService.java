package com.user.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.user.entity.Homes;

import java.util.List;

public interface HomesService extends IService<Homes>{
    boolean add(Homes homes);

    boolean updateShow(int id, int show);

    List<Homes> get();

    IPage<Homes> getAll(int page, int size);
}
