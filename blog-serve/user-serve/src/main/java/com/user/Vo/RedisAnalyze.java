package com.user.Vo;

import lombok.Data;

/*
* redis粗略内存估计、过期时间
* */
@Data
public class RedisAnalyze {
    private String key; // 键值
    private int value; // key的值，直接传到浏览器去解析长度吧，放在后台解析太耗性能了
    private long expireTime; // 过期时间
    private String type; // key的类型
}
