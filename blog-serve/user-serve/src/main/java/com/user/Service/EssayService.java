package com.user.Service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.user.entity.Essay;

import java.util.List;

public interface EssayService extends IService<Essay> {
    boolean addEssay(Essay essay);

    boolean updateEssay(Essay essay);

    IPage<Essay> getAll(int page, int size);

    List<Essay> getByCondition();

    boolean updateShow(int id, int show);
}
