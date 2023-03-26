package com.user.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.user.Vo.SurfVo;
import com.user.entity.Surf;

import java.sql.Timestamp;
import java.util.List;

public interface SurfService extends IService<Surf> {
    boolean insert(Surf surf);

    // 获取数据库中surf表的记录条数
    int getSize();

    IPage<Surf> getBaseInfoByPage(int page, int size);

    List<SurfVo> getSumGroupByTime(Timestamp startTime, Timestamp endTime);

    List<SurfVo> getSumGroupByDevice(Timestamp startTime, Timestamp endTime);

    List<SurfVo> getSumGroupByOS(Timestamp startTime, Timestamp endTime);

    List<SurfVo> getSumGroupByBrowse(Timestamp startTime, Timestamp endTime);

    List<SurfVo> getSumGroupByProvince(Timestamp startTime, Timestamp endTime);

    boolean deleteInfo(long surf_id);
}
