package com.user.Controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.user.Service.SendEmailCodeIpService;
import com.user.Utils.Result;
import com.user.entity.SendEmailCodeIp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
    @RequestMapping("/email_code")
@ResponseBody
public class SendEmailCodeIpController {
    @Autowired
    private SendEmailCodeIpService sendEmailCodeIpService;

    private Result result = new Result();
    // 删除邮箱发送验证码记录
    @DeleteMapping("{id}")
    public Result deleteById(@PathVariable("id") int id) {
        boolean b = sendEmailCodeIpService.removeById(id);
        return result.ok(b);
    }

    // 修改是否禁用IP
    @PostMapping("{ip}/{allowed}")
    public Result updateIp(@PathVariable("ip") String ip, @PathVariable("allowed") int allowed) {
        boolean b = sendEmailCodeIpService.updateInfo(ip, allowed);
        return result.ok(b);
    }

    // 根据IP查询所有的邮件发送详情
    @GetMapping("/getIp")
    public Result getIp(@RequestParam String ip) {
        List<SendEmailCodeIp> ip1 = sendEmailCodeIpService.getIp(ip);
        return result.ok(ip1);
    }

    // 查询所有的邮件发送IP详情
    @GetMapping("/getAll")
    public Result getAll(@RequestParam int page, @RequestParam int size) {
        IPage<SendEmailCodeIp> all = sendEmailCodeIpService.getAll(page, size);
        return result.ok(all);
    }
}
