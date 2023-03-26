package com.user.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.user.Mapper.ImgMapper;
import com.user.Service.ImgService;
import com.user.entity.img;
import org.springframework.stereotype.Service;

@Service
public class imgServiceImpl extends ServiceImpl<ImgMapper, img> implements ImgService {
}
