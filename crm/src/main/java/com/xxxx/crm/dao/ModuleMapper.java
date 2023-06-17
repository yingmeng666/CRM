package com.xxxx.crm.dao;

import com.xxxx.crm.Model.TreeModel;
import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.vo.Module;
import java.util.List;
public interface ModuleMapper extends BaseMapper<Module,Integer> {
    List<TreeModel> queryAllModelus();

    List<Module> queryModuleList();

    Module queryModuleByGradeAndModuleName(Integer grade, String moduleName);

    Module queryModuleByGradeAndUrl(Integer grade, String url);

    Module queryModuleByOptValue(String optValue);

    int selectModuleCountByParentId(Integer id);
}