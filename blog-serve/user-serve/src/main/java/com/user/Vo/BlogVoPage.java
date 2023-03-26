package com.user.Vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BlogVoPage {
    private List<BlogVo> blogVoList = new ArrayList<>();
    private int total ;

}
