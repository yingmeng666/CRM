package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;

import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.query.CustomerServeQuery;
import com.xxxx.crm.service.CustomerServeService;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.LoginUserUtil;
import com.xxxx.crm.vo.CustomerServe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/customer_serve")
public class CustomerServeController extends BaseController {
    @Autowired
    private CustomerServeService customerServeService;

    @GetMapping("/list")
    public Map<String, Object> queryCustomerServeList(CustomerServeQuery customerServeQuery, Integer flag, HttpServletRequest request) {
        if(flag!=null&&flag==1){
            customerServeQuery.setAssigner(LoginUserUtil.releaseUserIdFromCookie(request));
        }
        return customerServeService.queryByParamsForTable(customerServeQuery);
    }

    @RequestMapping("/index/{type}")
    public ModelAndView index(@PathVariable Integer type) {
        ModelAndView modelAndView = new ModelAndView();
        if (type != null) {
            if (type == 1) {
                modelAndView.setViewName("customerServe/customer_serve");
            } else if (type == 2) {
                modelAndView.setViewName("customerServe/customer_serve_assign");
            } else if (type == 3) {
                modelAndView.setViewName("customerServe/customer_serve_proce");
            } else if (type == 4) {
                modelAndView.setViewName("customerServe/customer_serve_feed_back");
            } else if (type == 5) {
                modelAndView.setViewName("customerServe/customer_serve_archive");
            }
        }
        return modelAndView;
    }

    @RequestMapping("/toAddCustomerServePage")
    public ModelAndView toAddCustomerServePage() {
        ModelAndView modelAndView = new ModelAndView("customerServe/customer_serve_add");
        return modelAndView;
    }

    @RequestMapping("/toAddCustomerServeProcePage")
    public ModelAndView toAddCustomerServeProcePage(Integer id) {
        ModelAndView modelAndView = new ModelAndView("customerServe/customer_serve_proce_add");
        AssertUtil.isTrue(id==null,"记录不存在请重试");
        CustomerServe customerServe = customerServeService.selectByPrimaryKey(id);
        AssertUtil.isTrue(null==customerServe,"记录不存在请重试");
        modelAndView.addObject("customerServe",customerServe);
        return modelAndView;
    }

    @PostMapping("/add")
    public ResultInfo addCustomerSever(CustomerServe customerServe) {
        customerServeService.addCustomerServe(customerServe);
        return success("创建服务成功");
    }

    @PostMapping("/update")
    public ResultInfo updateCustomerServe(CustomerServe customerServe) {
        customerServeService.updateCustomerServe(customerServe);
        return success("服务更新成功");
    }
    @RequestMapping("/toAddCustomerServeAssignPage")
    public ModelAndView toAddCustomerServeAssignPage(Integer id){
        ModelAndView modelAndView = new ModelAndView("customerServe/customer_serve_assign_add");
        AssertUtil.isTrue(id==null,"记录不存在请重试");
        CustomerServe customerServe = customerServeService.selectByPrimaryKey(id);
        AssertUtil.isTrue(null==customerServe,"记录不存在请重试");
        modelAndView.addObject("customerServe",customerServe);
        return modelAndView;
    }

    @RequestMapping("/toAddCustomerServeBackPage")
    public ModelAndView toAddCustomerServeBackPage(Integer id){
        ModelAndView modelAndView = new ModelAndView("customerServe/customer_serve_feed_back_add");
        AssertUtil.isTrue(id==null,"记录不存在请重试");
        CustomerServe customerServe = customerServeService.selectByPrimaryKey(id);
        AssertUtil.isTrue(null==customerServe,"记录不存在请重试");
        modelAndView.addObject("customerServe",customerServe);
        return modelAndView;
    }
}
