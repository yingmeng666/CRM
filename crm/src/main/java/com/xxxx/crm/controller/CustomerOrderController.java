package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.query.CustomerOrderQuery;
import com.xxxx.crm.service.CustomerOrderService;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.vo.CustomerOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@RestController
@RequestMapping("/orders")
public class CustomerOrderController extends BaseController {
    @Autowired
    private CustomerOrderService customerOrderService;

    @GetMapping("/list")
    public Map<String, Object> queryCustomerOrderList(CustomerOrderQuery customerOrderQuery) {
        return customerOrderService.queryCustomerOrderList(customerOrderQuery);
    }
    @RequestMapping("/toOrderDetailPage")
    public ModelAndView toOrderDetailPage(Integer orderId){
        ModelAndView modelAndView = new ModelAndView("customer/customer_order_detail");
        if(orderId!=null){
            Map<String,Object> map  = customerOrderService.queryOrderByOrderId(orderId);
            AssertUtil.isTrue(map==null,"系统错误记录不存在请重试");
            modelAndView.addObject("order",map);
        }
        return modelAndView;
    }


}
