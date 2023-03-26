package com.user.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.user.entity.Project;

import java.util.List;

public interface ProjectService extends IService<Project> {
    List<Project> getCondition();

    boolean addProject(Project project);

    boolean updateShow(int id, int show);
}
