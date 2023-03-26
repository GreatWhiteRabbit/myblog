package com.user.Controller;

import com.user.Service.ImgService;
import com.user.entity.img;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;


@Controller
@RequestMapping("/image")
@ResponseBody
public class imageController {
    @Autowired
    private ImgService imgService;

    @PostMapping("/uploadAvatar")
    public String uploadimg(@RequestParam("file") MultipartFile mFile) throws IOException {
        // 定义存储图片的地址
        String folder = "/home/nginx/nginx/image/";
        // 文件夹
        File imgFolder = new File(folder);
        // 获取文件名
        String fname = mFile.getOriginalFilename();
        // 获取文件后缀
        String ext = "." + fname.substring(fname.lastIndexOf(".")+1);
        // 获取时间字符串
        String dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
        // 生成新的文件名
        String newFileName = dateTimeFormatter + UUID.randomUUID().toString().replaceAll("-","") + ext;
        // 文件在本机的全路径
        File filePath = new File(imgFolder, newFileName);
        if (!filePath.getParentFile().exists()){
            filePath.getParentFile().mkdirs();
        }
        try{
            mFile.transferTo(filePath);
            // 返回文件名

            img img =new img();
            img.setImg_url(filePath.getName());
            imgService.save(img);
            return filePath.getName();
        }catch (IOException e){
            e.printStackTrace();
            return "";
        }
    }

    @PostMapping("/uploadMarkDownImg")
    public String uploadMarkDownImg(@RequestParam("file") MultipartFile mFile) throws IOException {
        // 定义存储图片的地址
        String folder = "/home/nginx/nginx/image/";
        // 文件夹
        File imgFolder = new File(folder);
        // 获取文件名
        String fname = mFile.getOriginalFilename();
        // 获取文件后缀
        String ext = "." + fname.substring(fname.lastIndexOf(".")+1);
        // 获取时间字符串
        String dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
        // 生成新的文件名
        String newFileName = dateTimeFormatter + UUID.randomUUID().toString().replaceAll("-","") + ext;
        // 文件在本机的全路径
        File filePath = new File(imgFolder, newFileName);
        if (!filePath.getParentFile().exists()){
            filePath.getParentFile().mkdirs();
        }
        try{
            mFile.transferTo(filePath);
            // 返回文件名
            String path = "http://localhost:8080/apis/blog/static/img/";
            return   filePath.getName();
        }catch (IOException e){
            e.printStackTrace();
            return "";
        }
    }


}
