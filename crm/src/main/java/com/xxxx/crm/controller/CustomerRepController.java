package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.query.CustomerRepQuery;
import com.xxxx.crm.service.CustomerRepService;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.vo.CustomerRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.IdentityHashMap;
import java.util.Map;

@RestController
@RequestMapping("/customer_rep")
public class CustomerRepController extends BaseController {
    @Autowired
    private CustomerRepService customerRepService;
    @GetMapping("/list")
    public Map<String,Object> queryCustomerRepList(CustomerRepQuery customerRepQuery){
        return customerRepService.queryByParamsForTable(customerRepQuery);
    }

    @PostMapping("/add")
    public ResultInfo addCustomerRep(CustomerRep customerRep){
        customerRepService.addCustomerRep(customerRep);
        return success("添加成功");
    }

    @PostMapping("/update")
    public ResultInfo updateCustomerRep(CustomerRep customerRep){
        customerRepService.updateCustomerRep(customerRep);
        return success("修改成功");
    }
    @RequestMapping("/toAddOrUpdateCustomerRepPage")
    public ModelAndView toAddOrUpdateCustomerRepPage(Integer lossId,Integer id){
        ModelAndView modelAndView = new ModelAndView("customerLoss/customer_rep_add_update");
        modelAndView.addObject("lossId",lossId);
        if(id!=null){
            CustomerRep customerRep = customerRepService.selectByPrimaryKey(id);
            AssertUtil.isTrue(customerRep==null,"系统错误记录不存在");
            modelAndView.addObject("customerRep",customerRep);
        }

        return modelAndView;
    }
    @PostMapping("delete")
    public ResultInfo deleteCustomerRep(Integer id){
        customerRepService.deleteCustomerRep(id);
        return success("删除成功");
    }

    /**
     * 修改客户流失状态lossId
     * @param id
     * @return
     */
    @PostMapping("/updateCustomerLossStateById")
    public ResultInfo updateCustomerLossStateById(Integer id,String lossReason){
        customerRepService.updateCustomerLossStateById(id,lossReason);
        return success("确认流失成功");
    }
}
