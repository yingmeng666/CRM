package com.xxxx.crm.service;

import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.dao.CustomerLossMapper;

import com.xxxx.crm.dao.CustomerRepMapper;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.vo.CustomerLoss;
import com.xxxx.crm.vo.CustomerRep;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class CustomerRepService extends BaseService<CustomerRep,Integer> {
    @Resource
    private CustomerRepMapper customerRepMapper;
    @Resource
    private CustomerLossMapper customerLossMapper;

    /**
     * 添加
     * @param customerRep
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addCustomerRep(CustomerRep customerRep) {
        //参数判断loss_id非空且有记录，measure非空
        AssertUtil.isTrue(null==customerRep.getLossId()||null==customerLossMapper.selectByPrimaryKey(customerRep.getLossId()),"系统错误，记录不存在");
        AssertUtil.isTrue(StringUtils.isBlank(customerRep.getMeasure()),"流失措施不能为空！");
        //设置默认值
        customerRep.setIsValid(1);
        customerRep.setCreateDate(new Date());
        customerRep.setUpdateDate(new Date());
        //添加到数据库，判断受影响的行数
        AssertUtil.isTrue(customerRepMapper.insertSelective(customerRep)!=1,"添加失败");
    }

    /**
     * 修改
     * @param customerRep
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateCustomerRep(CustomerRep customerRep) {
        //id不为空
        AssertUtil.isTrue(null==customerRep.getId(),"系统错误，记录不存在！");
        CustomerRep temp = customerRepMapper.selectByPrimaryKey(customerRep.getId());
        AssertUtil.isTrue(null==temp,"系统错误，记录不存在！");
        //参数判断loss_id非空且有记录，measure非空
        AssertUtil.isTrue(null==customerRep.getLossId()||null==customerLossMapper.selectByPrimaryKey(customerRep.getLossId()),"系统错误，记录不存在");
        AssertUtil.isTrue(StringUtils.isBlank(customerRep.getMeasure()),"流失措施不能为空！");
        //设置默认值
        customerRep.setUpdateDate(new Date());
        //修改数据，判断受影响行数
        AssertUtil.isTrue(customerRepMapper.updateByPrimaryKeySelective(customerRep)!=1,"更新失败");
    }

    /**
     * 删除
     * @param id
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteCustomerRep(Integer id) {
        AssertUtil.isTrue(id==null,"系统错误记录不存在");
        CustomerRep customerRep = customerRepMapper.selectByPrimaryKey(id);
        AssertUtil.isTrue(customerRep==null,"系统错误记录不存在");
        //设置默认值
        customerRep.setIsValid(0);
        customerRep.setUpdateDate(new Date());
        //调用更新，判断受影响行数
        AssertUtil.isTrue(customerRepMapper.updateByPrimaryKeySelective(customerRep)!=1,"删除失败");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateCustomerLossStateById(Integer lossId,String lossReason) {
        //参数判断，id不能为空且有记录
        AssertUtil.isTrue(null==lossId,"系统错误记录不存在！");
        CustomerLoss customerLoss = customerLossMapper.selectByPrimaryKey(lossId);
        AssertUtil.isTrue(null==customerLoss,"系统错误记录不存在！");
        AssertUtil.isTrue(StringUtils.isBlank(lossReason),"流失原因不能为空！");
        //设置流失状态
        customerLoss.setState(1);
        customerLoss.setLossReason(lossReason);
        customerLoss.setUpdateDate(new Date());
        customerLoss.setConfirmLossTime(new Date());
        //更新，判断受影响行数
        AssertUtil.isTrue(customerLossMapper.insertSelective(customerLoss)!=1,"确认流失状态失败");
    }
}
