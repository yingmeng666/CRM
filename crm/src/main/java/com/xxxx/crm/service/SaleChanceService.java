package com.xxxx.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.SaleChanceMapper;
import com.xxxx.crm.enums.DevResult;
import com.xxxx.crm.enums.StateStatus;
import com.xxxx.crm.query.SaleChanceQuery;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.PhoneUtil;
import com.xxxx.crm.vo.SaleChance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class SaleChanceService extends BaseService<SaleChance, Integer> {
    @Resource
    private SaleChanceMapper saleChanceMapper;

    /**
     * 多条件查询营销机会
     *
     * @param saleChanceQuery
     * @return
     */
    public Map<String, Object> querySaleChanceByParams(SaleChanceQuery saleChanceQuery) {
        Map<String, Object> map = new HashMap<>();
        PageHelper.startPage(saleChanceQuery.getPage(), saleChanceQuery.getLimit());
        PageInfo<SaleChance> pageInfo = new PageInfo<>(saleChanceMapper.selectByParams(saleChanceQuery));
        //设置map参数
        map.put("count", pageInfo.getTotal());
        map.put("data", pageInfo.getList());
        return map;
    }

    /**
     * 添加营销机会
     * 1.非空字段customerName，linkMan，linkPhone(电话号码格式正确)
     * 2.默认值
     * 1).createMan（当前登录用户）
     * 2).isValid（状态码刚添加，1有效）
     * 3).assignMan（指派人分情况）
     * 有指派人：
     * 1.assignTime(指派时间当前系统时间)
     * 2.state(分配状态，1=已分配，0=无效)
     * 3.devResult(开发状态，1=开发中)
     * 无指派人
     * 1.assignTime(null)
     * 2.state(分配状态，0=未分配)
     * 3.devResult(开发状态，0=未开发)
     * 4).createDate（创建时间当前时间）
     * 5).updateDate（更新时间当前时间）
     * 3.执行dao层的添加方法，判断受影响行数
     *
     * @param saleChance
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addSaleChance(SaleChance saleChance) {
        //1.非空字段customerName，linkMan，linkPhone(电话号码格式正确)
        checkNoNullParams(saleChance.getCustomerName(), saleChance.getLinkMan(), saleChance.getLinkPhone());
        //默认值设置
        //isValid（状态码刚添加，1有效）
        saleChance.setIsValid(1);
        //创建时间和更新时间设置
        saleChance.setCreateDate(new Date());
        saleChance.setUpdateDate(new Date());
        //是否有指派人判断
        if (StringUtils.isBlank(saleChance.getAssignMan())) {//无
            //1.assignTime(null)
            saleChance.setAssignTime(null);
            //2.state(分配状态，0=未分配)
            saleChance.setState(StateStatus.UNSTATE.getType());
            //3.devResult(开发状态，0=未开发)
            saleChance.setDevResult(DevResult.UNDEV.getStatus());
        } else {//有
            //1.assignTime(指派时间当前系统时间)
            saleChance.setAssignTime(new Date());
            //2.state(分配状态，1=已分配，0=无效)
            saleChance.setState(StateStatus.STATED.getType());
            //3.devResult(开发状态，1=开发中)
            saleChance.setDevResult(DevResult.DEVING.getStatus());
        }
        //3.执行dao层的添加方法，判断受影响行数
        AssertUtil.isTrue(saleChanceMapper.insertSelective(saleChance) != 1, "添加营销机会失败！");
    }

    /**
     * 修改营销机会
     * 参数校验
     * 1.saleChanceId不能为空且通过id查询数据库有记录
     * 2.非空字段customerName，linkMan，linkPhone(电话号码格式正确)
     * 3.createDate不变
     * 4.updateDate当前系统时间
     * 5.assign指派人
     * 修改前无
     * 修改后无不做操作
     * 修改后有
     * 1.assignTime(指派时间当前系统时间)
     * 2.state(分配状态，1=已分配)
     * 3.devResult(开发状态，1=开发中)
     * 修该前有
     * 修改后有
     * 1.assignTime(指派时间当前系统时间)//需要判断是否是同一人
     * 2.state(分配状态，1=已分配)//不做操作
     * 3.devResult(开发状态，1=开发中)//不做操作
     * 修改后无
     * 1.assignTime(null)
     * 2.state(分配状态0=无效)
     * 3.devResult(开发状态，0=未开发)
     *
     * @param saleChance
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateSaleChance(SaleChance saleChance) {
        Integer id = saleChance.getId();
        SaleChance oldSaleChance = saleChanceMapper.selectByPrimaryKey(id);
        //参数判断
        //1.id和数据库记录查询
        AssertUtil.isTrue(id == null ||null == oldSaleChance, "该记录不存在！");
        //2.非空判断和电话号码格式判断
        checkNoNullParams(saleChance.getCustomerName(), saleChance.getLinkMan(), saleChance.getLinkPhone());
        //3.updateDate当前系统时间
        saleChance.setUpdateDate(new Date());
        //4.指派人判断
        if (StringUtils.isBlank(oldSaleChance.getAssignMan())) {//修改前无
            if (StringUtils.isBlank(saleChance.getAssignMan())) {//修改后无不做操作
            } else {//修改后有
                //1.assignTime(指派时间当前系统时间)
                saleChance.setAssignTime(new Date());
                //2.state(分配状态，1=已分配)
                saleChance.setState(StateStatus.STATED.getType());
                //3.devResult(开发状态，1=开发中)
                saleChance.setDevResult(DevResult.DEVING.getStatus());
            }
        } else {//修改前有
            if (StringUtils.isBlank(saleChance.getAssignMan())) {//修改后无
                //1.assignTime(null)
                saleChance.setAssignTime(null);
                //2.state(分配状态0=无效)
                saleChance.setState(StateStatus.UNSTATE.getType());
                //3.devResult(开发状态，0=未开发)
                saleChance.setDevResult(DevResult.UNDEV.getStatus());
            } else {//修改后有
                //1.assignTime当前系统时间需要判断修改前后是否是同一人
                if(!oldSaleChance.getAssignMan().equals(saleChance.getAssignMan())) {//不是
                    saleChance.setAssignTime(new Date());
                }else{//是
                    saleChance.setAssignTime(oldSaleChance.getAssignTime());
                }
                //2.state(分配状态，1=已分配)
                saleChance.setState(StateStatus.STATED.getType());
                //3.devResult(开发状态，1=开发中)
                saleChance.setDevResult(DevResult.DEVING.getStatus());
            }
        }
        //调用dao层方法更新，判断受影响行数
        AssertUtil.isTrue(saleChanceMapper.updateByPrimaryKeySelective(saleChance) != 1, "营销机会修改失败！");
    }

    /**
     * 营销机会的删除(更新)修改is_valid值
     * @param ids
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteSaleChance(Integer[] ids){
        //参数非空判断
        AssertUtil.isTrue(ids==null||ids.length<1,"请选择需要删除的数据！");
        AssertUtil.isTrue(saleChanceMapper.deleteBatch(ids)!=ids.length,"营销机会删除失败！");
    }

    /**
     * 添加/修改营销机会的非空字段以及电话号码格式判断
     *
     * @param customerName
     * @param linkMan
     * @param linkPhone
     */
    private void checkNoNullParams(String customerName, String linkMan, String linkPhone) {
        AssertUtil.isTrue(StringUtils.isBlank(customerName), "客户名称不能为空！");
        AssertUtil.isTrue(StringUtils.isBlank(linkMan), "联系人不能为空！");
        AssertUtil.isTrue(StringUtils.isBlank(linkPhone), "联系电话不能为空！");
        //电话号码格式判断
        AssertUtil.isTrue(!PhoneUtil.isMobile(linkPhone), "手机号码格式不正确！");
    }

    /**
     * 修改开发状态
     * @param id
     * @param devResult
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateSaleChanceDevResult(Integer id, Integer devResult) {
        //id不为空且数据库有该记录
        AssertUtil.isTrue(id==null,"系统错误请重试...");
        SaleChance saleChance = saleChanceMapper.selectByPrimaryKey(id);
        AssertUtil.isTrue(saleChance==null,"系统错误请重试...");
        //设置devResult的值
        saleChance.setDevResult(devResult);
        //调用dao层的更新方法判断受影响行数
        AssertUtil.isTrue(saleChanceMapper.updateByPrimaryKeySelective(saleChance)!=1,"修改开发状态失败");
    }
}
