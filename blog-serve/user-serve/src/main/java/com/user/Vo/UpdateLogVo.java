package com.user.Vo;

import com.user.entity.UpdateLog;
import lombok.Data;

import java.util.List;

@Data
public class UpdateLogVo {
    private  List<UpdateLog> updateLogList;
    private int total;
}
