package com.xxxx.crm.controller;

import com.xxxx.crm.annotation.RequiredPermission;
import com.xxxx.crm.base.BaseController;

import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.dao.SaleChanceMapper;
import com.xxxx.crm.enums.StateStatus;
import com.xxxx.crm.query.SaleChanceQuery;
import com.xxxx.crm.service.SaleChanceService;
import com.xxxx.crm.utils.CookieUtil;
import com.xxxx.crm.utils.LoginUserUtil;
import com.xxxx.crm.vo.SaleChance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/sale_chance")
public class SaleChanceController extends BaseController {
    @Autowired
    private SaleChanceService saleChanceService;

    /**
     * 营销数据的多条件查询及分页
     * 客户开发计划查询传一个flag=1
     *
     * @param saleChanceQuery
     * @return
     */
    @RequestMapping("/list")
    @RequiredPermission(code = "101001")
    public ResultInfo querySaleChanceByParams(SaleChanceQuery saleChanceQuery, Integer flag, HttpServletRequest request) {
        ResultInfo resultInfo = new ResultInfo();
        if (flag != null && flag == 1) {
            //客户开发计划查询
            //获取当前用户id，即为指派人
            int userid = LoginUserUtil.releaseUserIdFromCookie(request);
            saleChanceQuery.setAssignMan(userid);
            //设置分配状态
            saleChanceQuery.setState(StateStatus.STATED.getType());
        }
        resultInfo.success(saleChanceService.querySaleChanceByParams(saleChanceQuery));
        return resultInfo;
    }

    /**
     * 进入营销机会管理模块
     *
     * @return
     */
    @RequestMapping("/index")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("/saleChance/sale_chance");
        return modelAndView;
    }

    /**
     * 添加营销机会
     *
     * @param saleChance
     * @return
     */
    @PostMapping("/add")
    @RequiredPermission(code = "101002")
    public ResultInfo addSaleChance(SaleChance saleChance, HttpServletRequest request) {
        //获取cookie中的用户名
        String userName = CookieUtil.getCookieValue(request, "userName");
        saleChance.setCreateMan(userName);
        //调用service层方法
        saleChanceService.addSaleChance(saleChance);
        //返回结果
        return success("添加营销机会成功！");
    }

    /**
     * 修改营销机会
     *
     * @param saleChance
     * @return
     */
    @PostMapping("/update")
    public ResultInfo updateSaleChance(SaleChance saleChance) {
        saleChanceService.updateSaleChance(saleChance);
        return success("修改营销机会成功！");
    }

    /**
     * 修改开发状态
     *
     * @param id
     * @param devResult
     * @return
     */
    @RequestMapping("/updateSaleChanceDevResult")
    public ResultInfo updateSaleChanceDevResult(Integer id, Integer devResult) {
        saleChanceService.updateSaleChanceDevResult(id, devResult);
        return success("修改开发状态成功");
    }

    /**
     * 删除营销机会
     *
     * @param ids
     * @return
     */
    @RequiredPermission(code = "101003")
    @RequestMapping("/delete")
    public ResultInfo deleteSaleChance(Integer[] ids) {
        saleChanceService.deleteSaleChance(ids);
        return success("删除营销机会成功");
    }

    /**
     * 进入添加/修改营销机会的页面
     *
     * @return
     */
    @RequestMapping("/toSaleChancePage")
    public ModelAndView toSaleChancePage(HttpServletRequest request, Integer saleChanceId) {
        ModelAndView modelAndView = new ModelAndView("saleChance/add_update");
        //通过id来判断时进入那个页面是否有回显数据
        if (saleChanceId != null) {
            SaleChance saleChance = saleChanceService.selectByPrimaryKey(saleChanceId);
            modelAndView.addObject("saleChance", saleChance);
        }
        return modelAndView;
    }
}
