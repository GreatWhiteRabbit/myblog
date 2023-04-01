package com.user.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.user.entity.SendEmailCodeIp;

import java.util.List;

public interface SendEmailCodeIpService extends IService<SendEmailCodeIp> {
    boolean banIp(String ipAddr);

    void addInfo(String user_email, String ip);

    boolean updateInfo(String ipAddr, int allowed);

    List<SendEmailCodeIp> getIp(String ip);

    IPage<SendEmailCodeIp> getAll(int page, int size);
}
