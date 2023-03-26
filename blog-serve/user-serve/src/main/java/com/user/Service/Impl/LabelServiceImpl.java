package com.user.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.user.Mapper.LabelMapper;
import com.user.Service.LabelService;
import com.user.Vo.BlogVo;
import com.user.entity.label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LabelServiceImpl extends ServiceImpl<LabelMapper, label> implements LabelService {
    @Autowired
    private LabelMapper labelMapper;
    @Override
    public int addLabel( BlogVo blogVo) {
        label label = new label();
        label.setLabel_name(blogVo.getLabel_name());
        // 先查询是否存在label
        QueryWrapper<label> labelQueryWrapper = new QueryWrapper<>();
        labelQueryWrapper.eq("label_name",blogVo.getLabel_name());
        label one = getOne(labelQueryWrapper);
        if(one != null) {
            return one.getLabel_id();
        }
        labelMapper.insert(label);
      return label.getLabel_id();
    }

    // 通过ID删除标签
    @Override
    public void delete(int labelId) {
        QueryWrapper<label> labelQueryWrapper = new QueryWrapper<>();
        labelQueryWrapper.eq("label_id",labelId);
        remove(labelQueryWrapper);
    }

    // 更新标签
    @Override
    public boolean updateLabel(int label_id, String label_name) {
        label label = new label();
        label.setLabel_name(label_name);
        label.setLabel_id(label_id);
        QueryWrapper<label> labelQueryWrapper = new QueryWrapper<>();
        labelQueryWrapper.eq("label_id",label_id);
        return update(label,labelQueryWrapper);
    }

    @Override
    public String getLabelById(int label_id) {
        QueryWrapper<label> labelQueryWrapper = new QueryWrapper<>();
        labelQueryWrapper.eq("label_id",label_id);
        return getOne(labelQueryWrapper).getLabel_name();
    }


}
