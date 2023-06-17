package com.xxxx.crm.service;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.xxxx.crm.Model.TreeModel;
import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.ModuleMapper;
import com.xxxx.crm.dao.PermissionMapper;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.vo.Module;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModuleService extends BaseService<Module, Integer> {
    @Resource
    private ModuleMapper moduleMapper;
    @Resource
    private PermissionMapper permissionMapper;

    public List<TreeModel> queryAllModelus(Integer roleId) {
        List<TreeModel> treeModelList = moduleMapper.queryAllModelus();
        List<Integer> permissionIds = permissionMapper.queryRoleHasModuleIdsByRoleId(roleId);
        if (permissionIds != null && permissionIds.size() > 0) {
            treeModelList.forEach(treeModel -> {
                if (permissionIds.contains(treeModel.getId())) {
                    treeModel.setChecked(true);
                }
            });
        }
        return treeModelList;
    }

    public Map<String, Object> queryModuleList() {
        Map<String, Object> map = new HashMap<>();
        List<Module> modules = moduleMapper.queryModuleList();
        map.put("code", 0);
        map.put("msg", "");
        map.put("count", modules.size());
        map.put("data", modules);
        return map;
    }

    /**
     * 添加
     *
     * @param module
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addModule(Module module) {
        //参数
        Integer grade = module.getGrade();

        //层级grade 非空 0|1|3
        AssertUtil.isTrue(null == grade || !(grade == 0 || grade == 1 || grade == 2), "菜单层级不合法");
        //模块名称非空
        AssertUtil.isTrue(StringUtils.isBlank(module.getModuleName()), "模块名称不能为空");
        //模块名称同一层级下模块名称唯一
        AssertUtil.isTrue(null != moduleMapper.queryModuleByGradeAndModuleName(grade, module.getModuleName()), "模块名称重复");
        //如果二级菜单
        if (grade == 1) {
            AssertUtil.isTrue(StringUtils.isBlank(module.getUrl()), "URl不能为空");
            //地址url 同一层级不可重复
            AssertUtil.isTrue(null != moduleMapper.queryModuleByGradeAndUrl(grade, module.getUrl()), "url地址重复");
        }
        //父级菜单，一级菜单目录 -1
        if (grade == 0) {
            module.setParentId(-1);
        }
        //父级菜单 parentId 2|3级菜单(菜单|按钮 garde=1|2) 非空且必须存在
        if (grade != 0) {
            AssertUtil.isTrue(null == module.getParentId(), "父级菜单不能为空");
            AssertUtil.isTrue(null == moduleMapper.selectByPrimaryKey(module.getParentId()), "请指定正确的父级菜单");
        }

        //权限码optValue非空
        AssertUtil.isTrue(StringUtils.isBlank(module.getOptValue()), "权限码不能为空");
        AssertUtil.isTrue(null != moduleMapper.queryModuleByOptValue(module.getOptValue()), "权限码已存在");

        //设置默认值
        module.setIsValid((byte) 1);
        module.setCreateDate(new Date());
        module.setUpdateDate(new Date());
        AssertUtil.isTrue(moduleMapper.insertSelective(module) < 1, "添加失败");
    }

    /**
     * 更新
     *
     * @param module
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateModule(Module module) {
        //参数判断
        //id非空数据存在
        AssertUtil.isTrue(null == module.getId(), "待更新的记录不存在");
        Module temp = moduleMapper.selectByPrimaryKey(module.getId());
        AssertUtil.isTrue(null == temp, "待更新记录不存在");
        //层级 grade 非空0|1|2
        Integer grade = module.getGrade();
        AssertUtil.isTrue(null == grade || !(grade == 1 || grade == 2 || grade == 0), "菜单层级不合规");
        //模块名不为空，唯一且不是自己
        AssertUtil.isTrue(StringUtils.isBlank(module.getModuleName()),"模块名不能为空");
        temp = moduleMapper.queryModuleByGradeAndModuleName(grade, module.getModuleName());
        AssertUtil.isTrue(temp!=null&&!(temp.getId().equals(module.getId())),"模块名不可用");
        //2级菜单，url非空，唯一且不是自己
        if(grade==1){
            AssertUtil.isTrue(StringUtils.isBlank(module.getUrl()),"url不能为空");
            temp = moduleMapper.queryModuleByGradeAndUrl(grade,module.getUrl());
            AssertUtil.isTrue(null!=temp&&!(temp.getUrl().equals(module.getUrl())),"url不可用");
        }
        //权限码，非空，且不是自己
        AssertUtil.isTrue(StringUtils.isBlank(module.getOptValue()),"权限码为空");
        temp = moduleMapper.queryModuleByOptValue(module.getOptValue());
        AssertUtil.isTrue(null!=temp&&!(temp.getOptValue().equals(module.getOptValue())),"权限码不可用");
        //设置默认值
        module.setUpdateDate(new Date());
        //更新，判断受影响的行数
        AssertUtil.isTrue(moduleMapper.updateByPrimaryKeySelective(module)!=1,"更新失败");



    }

    /**
     * 删除
     * @param id
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteModule(Integer id) {
        //参数判断 id非空且有数据
        AssertUtil.isTrue(null==id,"记录不存在");
        Module module = moduleMapper.selectByPrimaryKey(id);
        AssertUtil.isTrue(null==module,"记录不存在");
        //删除的记录不能存在子记录
        int count = moduleMapper.selectModuleCountByParentId(id);
        AssertUtil.isTrue(count>0,"菜单含有子菜单，请先删除子菜单");

        //查询是否有角色拥有这个资源
        count = permissionMapper.countPermissionByModuleId(id);
        if (count>0){
            AssertUtil.isTrue(permissionMapper.deletePermissionByModule(id)!=count,"删除失败");
        }
        //设置为无效
        module.setIsValid((byte)0);
        module.setUpdateDate(new Date());
        AssertUtil.isTrue(moduleMapper.updateByPrimaryKeySelective(module)<1,"删除失败");
    }
}
