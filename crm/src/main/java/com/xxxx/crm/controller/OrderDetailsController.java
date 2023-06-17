package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.query.OrderDetailsQuery;
import com.xxxx.crm.service.OrderDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/order_details")
public class OrderDetailsController extends BaseController {
    @Autowired
    private OrderDetailsService orderDetailsService;
    @GetMapping("/list")
    public Map<String,Object> queryOrderDetailsList(OrderDetailsQuery orderDetailsQuery){
        return orderDetailsService.queryOrderDetailsList(orderDetailsQuery);
    }
}
