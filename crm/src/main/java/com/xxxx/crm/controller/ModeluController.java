package com.xxxx.crm.controller;

import com.xxxx.crm.Model.TreeModel;
import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.service.ModuleService;
import com.xxxx.crm.vo.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/modelus")
public class ModeluController extends BaseController {
    @Autowired
    private ModuleService moduleService;

    @GetMapping("/queryAllModelus")
    public List<TreeModel> queryAllModelus(Integer roleId) {
        return moduleService.queryAllModelus(roleId);
    }

    @RequestMapping("/toAddGrantPage")
    public ModelAndView toAddGrantPage(Integer roleId) {
        ModelAndView modelAndView = new ModelAndView("role/grant");
        modelAndView.addObject("roleId", roleId);
        return modelAndView;
    }

    @GetMapping("/list")
    public Map<String, Object> queryModuleList() {
        return moduleService.queryModuleList();
    }

    @RequestMapping("/index")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("module/module");
        return modelAndView;
    }
    @PostMapping("/add")
    public ResultInfo addModule(Module module){
        moduleService.addModule(module);
        return success("添加成功");
    }

    @PostMapping("/update")
    public ResultInfo updateModule(Module module){
        moduleService.updateModule(module);
        return success("更新成功");
    }

    @PostMapping("/delete")
    public ResultInfo deleteModule(Integer id){
        moduleService.deleteModule(id);
        return success("删除成功");
    }
    @RequestMapping("/toAddModulePage")
    public ModelAndView toAddModuleDialog(Integer grade,Integer parentId){
        ModelAndView modelAndView = new ModelAndView("module/add");
        modelAndView.addObject("grade",grade);
        modelAndView.addObject("parentId",parentId);
        return modelAndView;
    }
    @RequestMapping("/toUpdateModulePage")
    public ModelAndView toUpdateModulePage(Integer id){
        ModelAndView modelAndView = new ModelAndView("module/update");
        modelAndView.addObject("module",moduleService.selectByPrimaryKey(id));
        return modelAndView;
    }
}
