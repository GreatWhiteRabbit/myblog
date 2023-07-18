package com.user.entity;

import lombok.Data;

@Data
public class Test {
    /*
    * 考试的选择题
    * */
    private int num;
    private String title;
    private String select_a;
    private String select_b;
    private String select_c;
    private String select_d;
    private String answer;
}
