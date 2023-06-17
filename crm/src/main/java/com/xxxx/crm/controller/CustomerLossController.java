package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.query.CustomerLossQuery;
import com.xxxx.crm.service.CustomerLossService;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.vo.CustomerLoss;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@RestController
@RequestMapping("/customer_loss")
public class CustomerLossController extends BaseController {
    @Autowired
    private CustomerLossService customerLossService;
    @RequestMapping("/index")
    public ModelAndView index(){
        ModelAndView modelAndView = new ModelAndView("customerLoss/customer_loss");
        return modelAndView;
    }

    @GetMapping("/list")
    public Map<String,Object> queryCustomerLossList(CustomerLossQuery customerLossQuery){
        return customerLossService.queryByParamsForTable(customerLossQuery);
    }

    @RequestMapping("/toCustomerRepPage")
    public ModelAndView toCustomerLossPage(Integer lossId){
        ModelAndView modelAndView = new ModelAndView("customerLoss/customer_rep");
        AssertUtil.isTrue(null==lossId,"系统错误，记录不存在请重试");
        CustomerLoss customerLoss = customerLossService.selectByPrimaryKey(lossId);
        AssertUtil.isTrue(null==customerLoss,"系统错误，记录不存在请重试");
        modelAndView.addObject("customerLoss",customerLoss);
        return modelAndView;
    }
}
