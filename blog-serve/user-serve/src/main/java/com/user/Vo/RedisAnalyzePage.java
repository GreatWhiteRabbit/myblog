package com.user.Vo;

import lombok.Data;

import java.util.List;

@Data
public class RedisAnalyzePage {
    private   List<RedisAnalyze> analyzeList;
    private int total;
    public RedisAnalyzePage(List<RedisAnalyze> list,int total) {
        this.analyzeList = list;
        this.total = total;
    }
}
