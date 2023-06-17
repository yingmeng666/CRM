package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.vo.User;

import java.util.List;
import java.util.Map;

public interface UserMapper extends BaseMapper<User,Integer> {
    //通过用户名查询用户对象，返回用户对象
    User queryUserByUserName(String userName);

    //查询所有销售人员的userName和id
    List<Map<String,Object>> queryAllSales();

    List<Map<String, Object>> queryAllCustomerManager();
}