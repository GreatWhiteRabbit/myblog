package com.user.Utils;

// 打印日志信息

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyInfoLog {
    private static final Logger MyLog = LoggerFactory.getLogger("自定义日志");

    public static void LogOut(String message, boolean status) {
        if(status) MyLog.info(message);
        else MyLog.error(message + "       非法路径请求，请管理员注意服务器安全");
    }

}
