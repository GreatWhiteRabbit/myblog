package com.user.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.user.Mapper.ProjectMapper;
import com.user.entity.Project;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {
    @Override
    public List<Project> getCondition() {
        QueryWrapper<Project> projectQueryWrapper = new QueryWrapper<>();
        projectQueryWrapper.eq("project_show",1);
        return list(projectQueryWrapper);
    }

    @Override
    public boolean addProject(Project project) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        project.setCreate_time(timestamp);
        project.setProject_show(0);
        return save(project);
    }

    @Override
    public boolean updateShow(int id, int show) {
        UpdateWrapper<Project> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id",id);
        updateWrapper.set("project_show",show);
        return update(null, updateWrapper);
    }
}
