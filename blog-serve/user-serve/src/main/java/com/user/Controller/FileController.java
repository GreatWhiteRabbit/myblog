package com.user.Controller;


import com.user.Service.BlogFileService;
import com.user.Utils.MyInfoLog;
import com.user.Utils.Result;
import com.user.entity.BlogFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@RequestMapping("file")
@Controller
@ResponseBody
public class FileController {

    @Autowired
    private BlogFileService blogFileService;

    // 文件保存的路径
    private String uploadFilePath = "";

    // 文件访问路径
    private String accessFilePath = "";

    private final Result result = new Result();

    @RequestMapping("/uploadWithAdmin")
    public Result fileUpload(@RequestParam("files")MultipartFile files[]) {
        for(int i = 0; i < files.length; i++) {
            String fileName = files[i].getOriginalFilename();
            File saveFile = new File(uploadFilePath + '/' + fileName);
            if(!saveFile.getParentFile().exists()) {
                saveFile.getParentFile().mkdirs();
            }
            try {
                files[i].transferTo(saveFile);
            } catch (Exception e) {
                MyInfoLog.LogOut("*****文件上传失败****",false);
                return result.fail("文件上传失败");
            }
        }
        return result.ok();
    }

    // 删除下载的文件
    @PostMapping("/removeWithAdmin")
    public Result fileDelete(@PathVariable("path") String path) {
        File deleteFile = new File(path);
        if(!deleteFile.exists()) {
            return result.ok(false);
        }
        boolean delete = deleteFile.delete();
        return result.ok(delete);
    }

    // 获取所有的文件路径
    @GetMapping("/allFile")
    public Result getAllFile() {
        // 后续再写将路径存入到Redis的代码
        List<BlogFile> list = blogFileService.list();
        return result.ok(list);
    }
}
