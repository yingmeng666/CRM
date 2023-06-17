package com.xxxx.crm.service;

import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.CustomerMapper;
import com.xxxx.crm.dao.CustomerServeMapper;
import com.xxxx.crm.dao.UserMapper;
import com.xxxx.crm.enums.CustomerServeStatus;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.vo.CustomerServe;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class CustomerServeService extends BaseService<CustomerServe, Integer> {
    @Resource
    private CustomerServeMapper customerServeMapper;
    @Resource
    private CustomerMapper customerMapper;
    @Resource
    private UserMapper userMapper;

    /**
     * 创建服务
     * @param customerServe
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addCustomerServe(CustomerServe customerServe) {
        //参数判断客户名不能为空且有数据
        AssertUtil.isTrue(StringUtils.isBlank(customerServe.getCustomer()),"客户名不能为空！");
        AssertUtil.isTrue(customerMapper.queryCustomerByName(customerServe.getCustomer())==null,"客户不存在！");
        //服务类型非空，服务请求非空
        AssertUtil.isTrue(StringUtils.isBlank(customerServe.getServeType()),"请选择服务类型");
        AssertUtil.isTrue(StringUtils.isBlank(customerServe.getServiceRequest()),"服务请求不能为空");
        //设置默认值
        customerServe.setState(CustomerServeStatus.CREATED.getState());
        customerServe.setIsValid(1);
        customerServe.setCreateDate(new Date());
        customerServe.setUpdateDate(new Date());
        //更新，判断是否成功
        AssertUtil.isTrue(customerServeMapper.insertSelective(customerServe)!=1,"创建服务失败");
    }

    /**
     * 更新操作
     * @param customerServe
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateCustomerServe(CustomerServe customerServe){
        //参数判断 id存在且有数据
        AssertUtil.isTrue(null==customerServe.getId()||customerServeMapper.selectByPrimaryKey(customerServe.getId())==null,"代更新记录不存在");
        //设置更新时间
        customerServe.setUpdateDate(new Date());
        //判断是那种操作，分配|处理|反馈
        if(CustomerServeStatus.ASSIGNED.getState().equals(customerServe.getState())){//服务分配
            //分配人不能为空且存在用户
            AssertUtil.isTrue(StringUtils.isBlank(customerServe.getAssigner()),"分配人不能为空！");
            AssertUtil.isTrue(userMapper.selectByPrimaryKey(Integer.parseInt(customerServe.getAssigner()))==null,"分配人不存在");
            //设置分配时间
            customerServe.setAssignTime(new Date());

        }else if(CustomerServeStatus.PROCED.getState().equals(customerServe.getState())){//服务处理
            //服务处理内容非空
            AssertUtil.isTrue(StringUtils.isBlank(customerServe.getServiceProce()),"处理内容不能为空");
            customerServe.setServiceProceTime(new Date());
        }else if(CustomerServeStatus.FEED_BACK.getState().equals(customerServe.getState())){//服务反馈
            //服务反馈内容不为空
            AssertUtil.isTrue(StringUtils.isBlank(customerServe.getServiceProceResult()),"服务反馈内容不能为空");
            //服务满意度不能为空
            AssertUtil.isTrue(StringUtils.isBlank(customerServe.getMyd()),"请选择服务满意度");
            //设置服务归档状态
            customerServe.setState(CustomerServeStatus.ARCHIVED.getState());
        }
        //执行更新判断受影响行数
        AssertUtil.isTrue(customerServeMapper.updateByPrimaryKeySelective(customerServe)!=1,"更新失败");
    }
}
