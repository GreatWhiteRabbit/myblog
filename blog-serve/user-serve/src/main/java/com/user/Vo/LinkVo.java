package com.user.Vo;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class LinkVo {
    private int id;
    private String imgurl;
    private String link;
    private String name;
    private String info;
}
