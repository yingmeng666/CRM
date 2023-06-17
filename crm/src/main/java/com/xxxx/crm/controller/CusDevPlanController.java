package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.dao.SaleChanceMapper;
import com.xxxx.crm.enums.StateStatus;
import com.xxxx.crm.query.CusDevPlanQuery;
import com.xxxx.crm.query.SaleChanceQuery;
import com.xxxx.crm.service.CusDevPlanService;
import com.xxxx.crm.service.SaleChanceService;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.LoginUserUtil;
import com.xxxx.crm.vo.CusDevPlan;
import com.xxxx.crm.vo.SaleChance;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/cus_dev_plan")
public class CusDevPlanController extends BaseController {
    @Resource
    private SaleChanceService saleChanceService;
    @Resource
    private CusDevPlanService cusDevPlanService;

    /**
     * 开发项多条件分页查询
     *
     * @param cusDevPlanQuery
     * @return
     */
    @RequestMapping("/list")
    public ResultInfo queryCusDevPlanByParams(CusDevPlanQuery cusDevPlanQuery) {
        ResultInfo resultInfo = new ResultInfo();
        resultInfo.success(cusDevPlanService.queryCusDevPlanByParams(cusDevPlanQuery));
        return resultInfo;
    }

    /**
     * 添加计划项
     *
     * @param cusDevPlan
     * @return
     */
    @PostMapping("/add")
    public ResultInfo addCusDevPlan(CusDevPlan cusDevPlan) {
        cusDevPlanService.addCusDevPlan(cusDevPlan);
        return success("添加成功！");
    }

    /**
     * 修改开发计划
     *
     * @param cusDevPlan
     * @return
     */
    @PostMapping("/update")
    public ResultInfo updateCusDevPlan(CusDevPlan cusDevPlan) {
        cusDevPlanService.updateCusDevPlan(cusDevPlan);
        return success("修改成功！");
    }

    @PostMapping("/delete")
    public ResultInfo deleteCusDevPlan(Integer id) {
        cusDevPlanService.deleteCusDevPlan(id);
        return success("删除成功！");
    }
    /**
     * 进入客户计划管理
     *
     * @return
     */
    @RequestMapping("/index")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("/cusDevPlan/cus_dev_plan");
        return modelAndView;
    }

    /**
     * 开发/详情页面
     *
     * @param saleChanceId
     * @return
     */
    @RequestMapping("/toCusDevPlanPage")
    public ModelAndView toCusDevPlanPage(Integer saleChanceId) {
        SaleChance saleChance = saleChanceService.selectByPrimaryKey(saleChanceId);
        ModelAndView modelAndView = new ModelAndView("/cusDevPlan/cus_dev_plan_data");
        modelAndView.addObject("saleChance", saleChance);
        return modelAndView;
    }

    /**
     * 进入修改和添加开发计划
     *
     * @return
     */
    @RequestMapping("/toAddOrUpdateCusDevPlanPage")
    public ModelAndView addOrUpdateCusDevPlanPage(Integer sId, Integer id) {
        ModelAndView modelAndView = new ModelAndView("/cusDevPlan/add_update");
        if (id != null) {
            CusDevPlan cusDevPlan = cusDevPlanService.selectByPrimaryKey(id);
            AssertUtil.isTrue(cusDevPlan==null,"系统异常请重试");
            modelAndView.addObject("cusDevPlan", cusDevPlan);
        }
        modelAndView.addObject("sId", sId);
        return modelAndView;
    }



}
