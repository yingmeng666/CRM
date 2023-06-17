package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.vo.Role;

import java.util.List;
import java.util.Map;

public interface RoleMapper extends BaseMapper<Role,Integer> {
    /**
     * 查询所有有效角色名和id
     * @return
     */
   List<Map<String,Object>> queryAllRoles(Integer userId);

    Role queryRoleByRoleName(String roleName);
}