package com.user.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.Service.UpdateLogService;
import com.user.Utils.Constant;
import com.user.Utils.Result;
import com.user.Vo.UpdateLogVo;
import com.user.entity.UpdateLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("updateLog")
@Controller
@ResponseBody
public class UpdateLogController {
    @Autowired
    private UpdateLogService updateLogService;

    private Result result = new Result();

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // 添加更新记录
    @PostMapping
    public Result addUpdateLog(@RequestBody UpdateLog updateLog) {
        boolean b = updateLogService.addUpdateLog(updateLog);
        // 删除Redis中的相关数据
        deleteUpdateInfoInRedis(b);
        return result.ok(b);
    }

    // 删除更新记录
    @DeleteMapping("{id}")
    public Result deleteUpdateLog(@PathVariable("id") int id) {
        boolean b = updateLogService.deleteUpdateLog(id);
        // 删除Redis中的相关数据
        deleteUpdateInfoInRedis(b);
        return result.ok(b);
    }
    // 修改更新记录
    @PutMapping
    public Result modify(@RequestBody UpdateLog updateLog) {
        boolean modify = updateLogService.modify(updateLog);
        // 删除Redis中的相关数据
        deleteUpdateInfoInRedis(modify);
        return result.ok(modify);
    }

    // 获取更新记录
    @GetMapping("getAll")
    public Result getAll(@RequestParam int page, @RequestParam int size)
            throws JsonProcessingException {
        // 先从Redis中查找数据
        int num = stringRedisTemplate.opsForList().size(Constant.updateInfo).intValue();
        if(num == 0) {
            List<UpdateLog> all = updateLogService.getAll();
            int listSize = all.size();
            if(listSize == 0) return result.ok();
            UpdateLogVo updateLogVo = new UpdateLogVo();
            List<UpdateLog> updateLogList = new ArrayList<>();
            // 获取的数据超过实际数据，返回空
            if((page - 1) * size > listSize) return result.ok();
            int firstIndex,lastIndex;
            if(page * size >= listSize) lastIndex = listSize;
            else lastIndex = page * size;
            firstIndex = (page - 1) * size;

            List<String> stringList = new ArrayList<>();
            for(int i = 0; i < listSize; i++) {
                if(i >= firstIndex && i < lastIndex) {
                    // 获取返回值数据
                    updateLogList.add(all.get(i));
                }
                // 将实体类转成json
                String json = objectMapper.writeValueAsString(all.get(i));
                stringList.add(json);
            }
            // 将所有的更新日志存到Redis中
            stringRedisTemplate.opsForList().leftPushAll(Constant.updateInfo,stringList);
            updateLogVo.setUpdateLogList(updateLogList);
            updateLogVo.setTotal(listSize);
            return result.ok(updateLogVo);
        }
       else {
            List<String> range = stringRedisTemplate.opsForList().range(Constant.updateInfo, 0, -1);
            UpdateLogVo updateLogVo = new UpdateLogVo();
            List<UpdateLog> updateLogList = new ArrayList<>();

            int listSize,firstIndex,lastIndex;
            assert range != null;
            listSize = range.size();
            // 获取的数据超过实际数据，返回空
            if((page - 1) * size > listSize) return result.ok();
            if(page * size >= listSize) lastIndex = listSize;
            else lastIndex = page * size;
            firstIndex = (page - 1) * size;
            for(int i = firstIndex; i < lastIndex; i++) {
                UpdateLog entity = objectMapper.readValue(range.get(i), UpdateLog.class);
                updateLogList.add(entity);
            }
            updateLogVo.setTotal(listSize);
            updateLogVo.setUpdateLogList(updateLogList);
            return result.ok(updateLogVo);
        }

    }

    // 删除Redis中的更新日志
    public void deleteUpdateInfoInRedis(boolean execute) {
        // 操作失败，不执行后面的程序
        if(!execute) return;
        Boolean exist = stringRedisTemplate.hasKey(Constant.updateInfo);
        if(Boolean.TRUE.equals(exist)) {
            stringRedisTemplate.delete(Constant.updateInfo);
        }
    }
}
