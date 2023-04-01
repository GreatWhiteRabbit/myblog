package com.user.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.user.Mapper.SendEmailCodeIpMapper;
import com.user.Service.SendEmailCodeIpService;
import com.user.entity.SendEmailCodeIp;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class SendEmailCodeIpServiceImpl extends ServiceImpl<SendEmailCodeIpMapper, SendEmailCodeIp>
        implements SendEmailCodeIpService {


    @Override
    /*
    * 禁用IP返回true，否则返回false
    * */
    public boolean banIp(String ipAddr) {
        // 首先判断管理员是否禁用此IP
        QueryWrapper<SendEmailCodeIp> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("ip",ipAddr);
        // 因为可能会获取到多个数据，只取其中一条数据即可
        List<SendEmailCodeIp> list = list(queryWrapper);


        // 如果获取为空，返回false
        if(list.size() == 0) {
            return false;
        }
        // 查看管理员是否禁用IP
        else {
            SendEmailCodeIp sendEmailCodeIp = list.get(0);
            if(sendEmailCodeIp.getAllowed() == 0) {
                return true;
            }
            else {
                // 判断IP是否在30分钟之内有三次以上发送验证码行为
                Timestamp before = new Timestamp(System.currentTimeMillis() - (30 * 60000));
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                queryWrapper.ge("surf_time",before);
                queryWrapper.le("surf_time",timestamp);
                queryWrapper.eq("ip",ipAddr);
                int size = list(queryWrapper).size();
                return size >= 3;
            }
        }
    }

    @Override
    public void addInfo(String user_email, String ip) {
        SendEmailCodeIp sendEmailCodeIp = new SendEmailCodeIp();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        sendEmailCodeIp.setEmail(user_email);
        sendEmailCodeIp.setIp(ip);
        sendEmailCodeIp.setSurf_time(timestamp);
        sendEmailCodeIp.setAllowed(1);
         save(sendEmailCodeIp);
    }

    @Override
    public boolean updateInfo(String ipAddr, int allowed) {
        UpdateWrapper<SendEmailCodeIp> sendEmailCodeIpUpdateWrapper = new UpdateWrapper<>();
        sendEmailCodeIpUpdateWrapper.eq("ip",ipAddr);
        sendEmailCodeIpUpdateWrapper.set("allowed",allowed);
       return update(null, sendEmailCodeIpUpdateWrapper);
    }

    @Override
    public List<SendEmailCodeIp> getIp(String ip) {
        QueryWrapper<SendEmailCodeIp> sendEmailCodeIpQueryWrapper = new QueryWrapper<>();
        sendEmailCodeIpQueryWrapper.eq("ip",ip);
        return list(sendEmailCodeIpQueryWrapper);
    }

    @Override
    public IPage<SendEmailCodeIp> getAll(int page, int size) {
        Page<SendEmailCodeIp> sendEmailCodeIpPage = new Page<>(page,size);
        IPage<SendEmailCodeIp> sendEmailCodeIpIPage = page(sendEmailCodeIpPage);
        return sendEmailCodeIpIPage;
    }
}
