package com.xxxx.crm.service;

import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.ModuleMapper;
import com.xxxx.crm.dao.PermissionMapper;
import com.xxxx.crm.dao.RoleMapper;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.vo.Permission;
import com.xxxx.crm.vo.Role;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class RoleService extends BaseService<Role,Integer> {
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private PermissionMapper permissionMapper;
    @Resource
    private ModuleMapper moduleMapper;

    /**
     * 查询所有的角色信息
     * @param userId
     * @return
     */
    public List<Map<String,Object>> queryAllRoles(Integer userId){
        return roleMapper.queryAllRoles(userId);
    }

    /**
     * 添加角色
     * @param role
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addRole(Role role){
        //参数校验
        //roleName非空且唯一
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()),"角色名不能为空");
        Role temp = roleMapper.queryRoleByRoleName(role.getRoleName());
        AssertUtil.isTrue(temp!=null,"角色名不唯一");
        //默认值设置isValid=1，创建时间更新时间为系统当前时间
        role.setIsValid(1);
        role.setCreateDate(new Date());
        role.setUpdateDate(new Date());
        //调用dao层添加方法，判断受影响行数
        AssertUtil.isTrue(roleMapper.insertSelective(role)!=1,"添加失败");

    }

    /**
     * 角色更新
     * @param role
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateRole(Role role){
        //参数判断，更新有id且有记录
        AssertUtil.isTrue(role.getId()==null,"系统错误请重试！");
        AssertUtil.isTrue(roleMapper.selectByPrimaryKey(role.getId())==null,"系统错误请重试");
        //判断roleName非空且唯一
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()),"角色名不能为空！");
        Role temp = roleMapper.queryRoleByRoleName(role.getRoleName());
        AssertUtil.isTrue(temp!=null&&!temp.getId().equals(role.getId()),"角色名已存在！");
        //设置默认值
        role.setIsValid(1);
        role.setUpdateDate(new Date());
        //调用更新方法判断受影响行数
        AssertUtil.isTrue(roleMapper.updateByPrimaryKeySelective(role)!=1,"更新失败！");
    }

    /**
     * 删除实际更新isvalid=0
     * @param roleId
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteRole(Integer roleId){
        //参数判断roleId非空且有数据在
        AssertUtil.isTrue(roleId==null,"系统错误请重试...");
        Role role = roleMapper.selectByPrimaryKey(roleId);
        AssertUtil.isTrue(role==null,"系统错误请重试...");
        //设置默认值
        role.setIsValid(0);
        role.setUpdateDate(new Date());
        //执行更新判断受影响行数
        AssertUtil.isTrue(roleMapper.updateByPrimaryKeySelective(role)!=1,"删除失败！");

    }

    /**
     * 授权操作
     * @param roleId
     * @param mIds
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addGrand(Integer roleId, Integer[] mIds) {
        //判断 roleId不为空，判断数据库有对应的记录
        AssertUtil.isTrue(roleId==null,"系统错误请重试");
        int count =permissionMapper.countPermissionByRoleId(roleId);
        if(count>0){
            AssertUtil.isTrue(permissionMapper.deletePermissionByRoleId(roleId)!=count,"系统错误请重试");
        }
        //判断有mIds
        if(mIds!=null&&mIds.length>0){
            List<Permission> permissionsList = new ArrayList<>();
            for (int i = 0; i < mIds.length; i++) {
                Permission permission = new Permission();
                //设置默认值
                permission.setRoleId(roleId);
                permission.setModuleId(mIds[i]);
                permission.setCreateDate(new Date());
                permission.setUpdateDate(new Date());
                //设置授权码
                permission.setAclValue(moduleMapper.selectByPrimaryKey(mIds[i]).getOptValue());

                permissionsList.add(permission);
            }

                AssertUtil.isTrue(permissionMapper.insertBatch(permissionsList)!=permissionsList.size(),"添加失败");
        }


    }
}
