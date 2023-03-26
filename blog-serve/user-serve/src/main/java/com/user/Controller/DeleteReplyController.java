package com.user.Controller;


/*
* 查看被删除的留言
* */

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.user.Service.DeleteReplyService;
import com.user.Utils.Result;
import com.user.entity.DeleteReply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequestMapping("deleteReply")
@Controller
@ResponseBody
public class DeleteReplyController {
    @Autowired
    private DeleteReplyService deleteReplyService;
    private Result result = new Result();

    @GetMapping("getAll/{page}/{size}")
    public Result getAll(@PathVariable("page") int page, @PathVariable("size") int size) {
        IPage<DeleteReply> all = deleteReplyService.getAll(page, size);
        return result.ok(all);
    }

    @DeleteMapping("/{id}")
    public Result remove(@PathVariable("id") long id) {
        boolean b = deleteReplyService.removeById(id);
        return result.ok(b);
    }

    // 将被删除的留言恢复，待开发
}
