package com.user.Vo;

import com.user.entity.Essay;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class EssayVo {
    private List<Essay> essayList;
    private int total;
}
