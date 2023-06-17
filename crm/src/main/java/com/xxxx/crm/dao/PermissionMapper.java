package com.xxxx.crm.dao;


import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.vo.Permission;

import java.util.List;

public interface PermissionMapper extends BaseMapper<Permission,Integer> {
    int countPermissionByRoleId(Integer roleId);

    int deletePermissionByRoleId(Integer roleId);

    List<Integer> queryRoleHasModuleIdsByRoleId(Integer roleId);

    List<String> queryUserHasPermissonsByUserId(int userId);

    int countPermissionByModuleId(Integer moduleId);

    int deletePermissionByModule(Integer moduleId);
}