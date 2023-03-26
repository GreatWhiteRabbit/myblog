package com.user.Utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    //    用于判断请求是否成功
    private boolean success;
    //    用于返回处理后的数据
    private Object data;
    private String message;
    public Result fail(String ms) {
        return new Result(false,null,ms);
    }
    public Result ok() {
        return new Result(true,null,null);
    }
    public Result ok(Object data) {
        return new Result(true,data,null);
    }
}