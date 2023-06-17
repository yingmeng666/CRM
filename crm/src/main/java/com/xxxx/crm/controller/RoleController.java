package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.query.RoleQuery;
import com.xxxx.crm.service.RoleService;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.vo.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/roles")
public class RoleController extends BaseController {
    @Autowired
    private RoleService roleService;

    /**
     * 查询所有有效角色
     *
     * @param userId
     * @return
     */
    @RequestMapping("/queryAllRoles")
    public List<Map<String, Object>> queryAllRoles(Integer userId) {
        return roleService.queryAllRoles(userId);
    }

    /**
     * 多条件分页查询
     *
     * @param roleQuery
     * @return
     */
    @GetMapping("/list")
    public Map<String, Object> queryByParamsForTable(RoleQuery roleQuery) {
        return roleService.queryByParamsForTable(roleQuery);
    }

    @RequestMapping("/index")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("role/role");
        return modelAndView;
    }

    @PostMapping("/add")
    public ResultInfo addRole(Role role) {
        roleService.addRole(role);
        return success("添加成功");
    }

    @PostMapping("/update")
    public ResultInfo updateRole(Role role) {
        roleService.updateRole(role);
        return success("修改成功");
    }

    @RequestMapping("/toAddOrUpdateRolePage")
    public ModelAndView toAddOrUpdateRolePage(Integer id) {
        ModelAndView modelAndView = new ModelAndView("role/add_update");
        if (id != null) {
            AssertUtil.isTrue(roleService.selectByPrimaryKey(id) == null, "系统错误请重试...");
            modelAndView.addObject("role", roleService.selectByPrimaryKey(id));
        }
        return modelAndView;
    }

    @PostMapping("/delete")
    public ResultInfo deleteRole(Integer roleId) {
        roleService.deleteRole(roleId);
        return success("删除成功!");
    }
    @PostMapping("/addGrand")
    public ResultInfo addGrand(Integer roleId,Integer[] mIds){
        roleService.addGrand(roleId,mIds);
        return success("添加成功");
    }

}
