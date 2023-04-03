package com.user.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.user.entity.UpdateLog;

import java.util.List;


public interface UpdateLogService extends IService<UpdateLog> {
    boolean addUpdateLog(UpdateLog updateLog);

    boolean deleteUpdateLog(int id);

    boolean modify(UpdateLog updateLog);

    List<UpdateLog> getAll();
}
