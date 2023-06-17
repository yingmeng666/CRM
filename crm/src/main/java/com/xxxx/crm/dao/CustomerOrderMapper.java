package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.vo.CustomerOrder;

import java.util.Map;

public interface CustomerOrderMapper extends BaseMapper<CustomerOrder,Integer> {

    Map<String, Object> queryOrderByOrderId(Integer orderId);

    /**
     * 查客户的最后一个订单日期
     * @param id
     * @return
     */
    CustomerOrder queryLastOrderByCustomerId(Integer id);
}