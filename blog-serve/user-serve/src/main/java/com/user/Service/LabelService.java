package com.user.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.user.Vo.BlogVo;
import com.user.entity.label;

public interface LabelService extends IService<label> {
    int addLabel( BlogVo blogVo);
   void delete(int labelId);

    boolean updateLabel(int label_id, String label_name);

    String getLabelById(int label_id);
}
