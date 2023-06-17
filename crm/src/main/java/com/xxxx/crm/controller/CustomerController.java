package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.query.CustomerQuery;
import com.xxxx.crm.service.CustomerService;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.vo.Customer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/customers")
public class CustomerController extends BaseController {
    @Resource
    private CustomerService customerService;

    @GetMapping("/list")
    public Map<String, Object> queryCustomerList(CustomerQuery customerQuery) {
        return customerService.queryCustomerList(customerQuery);
    }

    @RequestMapping("/index")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("customer/customer");
        return modelAndView;
    }

    @PostMapping("/add")
    public ResultInfo addCustomer(Customer customer) {
        customerService.addCustomer(customer);
        return success("添加成功");
    }

    @PostMapping("/update")
    public ResultInfo updateCustomer(Customer customer) {
        customerService.updateCustomer(customer);
        return success("修改成功");
    }
    @PostMapping("/delete")
    public ResultInfo deleteCustomer(Integer id) {
        customerService.deleteCustomer(id);
        return success("删除成功");
    }

    @RequestMapping("/toAddOrUpdateCustomerPage")
    public ModelAndView toAddOrUpdateCustomerPage(Integer id) {
        ModelAndView modelAndView = new ModelAndView("customer/add_update");
        if (id != null){
            Customer customer = customerService.selectByPrimaryKey(id);
            AssertUtil.isTrue(customer==null,"系统错误，编辑数据不存在请重试");
            modelAndView.addObject("customer",customer);
        }

        return modelAndView;
    }
    @RequestMapping("/toOrderInfoPage")
    public ModelAndView toOrderInfoPage(Integer customerId){
        ModelAndView modelAndView = new ModelAndView("customer/customer_order");
        if(customerId!=null) {
            Customer customer = customerService.selectByPrimaryKey(customerId);
            AssertUtil.isTrue(customer==null,"系统错误，编辑数据不存在请重试");
            modelAndView.addObject("customer",customer);
        }
        return modelAndView;
    }
}
