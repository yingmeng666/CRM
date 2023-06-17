package com.xxxx.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.CustomerOrderMapper;
import com.xxxx.crm.query.CustomerOrderQuery;
import com.xxxx.crm.vo.CustomerOrder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


@Service
public class CustomerOrderService extends BaseService<CustomerOrder,Integer> {
    @Resource
    private CustomerOrderMapper customerOrderMapper;


    public Map<String, Object> queryCustomerOrderList(CustomerOrderQuery customerOrderQuery) {
        Map<String,Object> map = new HashMap<>();
        PageHelper.startPage(customerOrderQuery.getPage(),customerOrderQuery.getLimit());
        PageInfo<CustomerOrder> pageInfo = new PageInfo<>(customerOrderMapper.selectByParams(customerOrderQuery));
        map.put("code",0);
        map.put("msg","success");
        map.put("count",pageInfo.getTotal());
        map.put("data",pageInfo.getList());
        return map;
    }

    public Map<String, Object> queryOrderByOrderId(Integer orderId) {
        return customerOrderMapper.queryOrderByOrderId(orderId);
    }
}
