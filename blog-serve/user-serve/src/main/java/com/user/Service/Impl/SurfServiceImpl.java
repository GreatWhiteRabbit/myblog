package com.user.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.user.Mapper.SurfMapper;
import com.user.Service.SurfService;
import com.user.Vo.SurfVo;
import com.user.entity.Surf;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SurfServiceImpl extends ServiceImpl<SurfMapper, Surf> implements SurfService {
    @Override
    public boolean insert(Surf surf) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        surf.setSurf_time(timestamp);
        return save(surf);
    }

    @Override
    public int getSize() {
        return list().size();
    }

    @Override
    public IPage<Surf> getBaseInfoByPage(int page, int size) {

        Page<Surf> surfPage = new Page<>(page,size);
        QueryWrapper<Surf> surfQueryWrapper = new QueryWrapper<>();
        surfQueryWrapper.orderBy(true,false,"surf_time");
        IPage<Surf> surfIPage = page(surfPage,surfQueryWrapper);
        return surfIPage;
    }

    @Override
    public List<SurfVo> getSumGroupByTime(Timestamp startTime, Timestamp endTime) {
        QueryWrapper<Surf> surfQueryWrapper = new QueryWrapper<>();
        surfQueryWrapper.select("count(*) as sum,\n" +
                "           date_format(surf_time,'%Y-%m-%d')  as time");
        surfQueryWrapper.ge("surf_time",startTime);
        surfQueryWrapper.le("surf_time",endTime);
        surfQueryWrapper.groupBy("time");
        List<Map<String, Object>> maps = listMaps(surfQueryWrapper);
        List<SurfVo> surfVoList = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            int sum = Integer.parseInt(map.get("sum").toString());
            String s = map.get("time").toString();
            SurfVo surfVo = new SurfVo();
            surfVo.setName(s);
            surfVo.setValue(sum);
            surfVoList.add(surfVo);
        }
        return surfVoList;
    }

    @Override
    public List<SurfVo> getSumGroupByDevice(Timestamp startTime, Timestamp endTime) {
       return getSumGroupByName(startTime,endTime,"device");
    }

    @Override
    public List<SurfVo> getSumGroupByOS(Timestamp startTime, Timestamp endTime) {
        return getSumGroupByName(startTime,endTime,"device_system");
    }

    @Override
    public List<SurfVo> getSumGroupByBrowse(Timestamp startTime, Timestamp endTime) {
        return getSumGroupByName(startTime,endTime,"browsername");
    }

    @Override
    public List<SurfVo> getSumGroupByProvince(Timestamp startTime, Timestamp endTime) {
        return getSumGroupByName(startTime,endTime,"province");
    }

    @Override
    public boolean deleteInfo(long surf_id) {
        QueryWrapper<Surf> surfQueryWrapper = new QueryWrapper<>();
        surfQueryWrapper.eq("surf_id",surf_id);
        return remove(surfQueryWrapper);
    }


    // 一个统一的类
    List<SurfVo> getSumGroupByName(Timestamp startTime, Timestamp endTime,String name) {
        QueryWrapper<Surf> surfQueryWrapper = new QueryWrapper<>();
        surfQueryWrapper.select("count(*) as sum,\n" +
                name);
        surfQueryWrapper.ge("surf_time",startTime);
        surfQueryWrapper.le("surf_time",endTime);
        surfQueryWrapper.groupBy(name);
        List<Map<String, Object>> maps = listMaps(surfQueryWrapper);
        List<SurfVo> surfVoList = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            int sum = Integer.parseInt(map.get("sum").toString());
            String s = map.get(name).toString();
            SurfVo surfVo = new SurfVo();
            surfVo.setName(s);
            surfVo.setValue(sum);
            surfVoList.add(surfVo);
        }
        return surfVoList;
    }

}
