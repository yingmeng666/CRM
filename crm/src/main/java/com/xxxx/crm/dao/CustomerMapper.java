package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.vo.Customer;

import java.util.List;

public interface CustomerMapper extends BaseMapper<Customer,Integer> {

    Customer queryCustomerByName(String name);

    /**
     * 查询流失客户
     * @return
     */
    List<Customer> queryLossCustomer();

    int updateLossStateByCustomerId(List<Integer> customerIds);
}