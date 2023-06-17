package com.xxxx.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.CustomerLossMapper;
import com.xxxx.crm.dao.CustomerMapper;
import com.xxxx.crm.dao.CustomerOrderMapper;
import com.xxxx.crm.query.CustomerQuery;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.PhoneUtil;
import com.xxxx.crm.vo.Customer;
import com.xxxx.crm.vo.CustomerLoss;
import com.xxxx.crm.vo.CustomerOrder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.*;

@Service
public class CustomerService extends BaseService<Customer,Integer> {
    @Resource
    private CustomerMapper customerMapper;
    @Resource
    private CustomerOrderMapper customerOrderMapper;
    @Resource
    private CustomerLossMapper customerLossMapper;

    /**
     * 分页查询；
     * @param customerQuery
     * @return
     */
    public Map<String, Object> queryCustomerList(CustomerQuery customerQuery) {
        Map<String ,Object> map = new HashMap<>();
        PageHelper.startPage(customerQuery.getPage(), customerQuery.getLimit());
        PageInfo<Customer> customerPageInfo = new PageInfo<>(customerMapper.selectByParams(customerQuery));
        map.put("code",0);
        map.put("msg","success");
        map.put("count",customerPageInfo.getTotal());
        map.put("data",customerPageInfo.getList());
        return map;
    }

    /**
     * 添加客户
     * @param customer
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addCustomer(Customer customer){
        //参数校验客户名不为空且唯一，电话号码不为空切唯一，法人非空
        AssertUtil.isTrue(StringUtils.isBlank(customer.getName()),"客户名不能为空");
        AssertUtil.isTrue(customerMapper.queryCustomerByName(customer.getName())!=null,"客户名已存在");
        checkParams(customer.getPhone(),customer.getFr());
        //设置默认值
        customer.setIsValid(1);
        customer.setState(0);
        customer.setCreateDate(new Date());
        customer.setUpdateDate(new Date());
        customer.setKhno("KH" + System.currentTimeMillis());
        AssertUtil.isTrue(customerMapper.insertSelective(customer)!=1,"添加失败");
    }

    /**
     * 修改更新
     * @param customer
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateCustomer(Customer customer){
        //参数判断id不为空，phone格式正确不为空，法人非空，客户名非空唯一且不是自己
        AssertUtil.isTrue(null==customer.getId(),"系统错误，记录不存在请重试...");
        AssertUtil.isTrue(customerMapper.selectByPrimaryKey(customer.getId())==null,"系统错误，记录不存在请重试...");
        AssertUtil.isTrue(StringUtils.isBlank(customer.getName()),"客户名称不能为空");
        Customer temp = customerMapper.queryCustomerByName(customer.getName());
        AssertUtil.isTrue(temp!=null&&!(temp.getId().equals(customer.getId())),"客户名不唯一");
        checkParams(customer.getPhone(),customer.getFr());
        //设置默认值
        customer.setState(0);
        customer.setIsValid(1);
        customer.setUpdateDate(new Date());
        //更新判断受影响的行数
        AssertUtil.isTrue(customerMapper.updateByPrimaryKeySelective(customer)!=1,"修改失败");
    }

    /**
     * 删除记录
     * @param id
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteCustomer(Integer id){
        AssertUtil.isTrue(null==id||customerMapper.selectByPrimaryKey(id)==null,"系统错误记录不存在，请重试");
        Customer customer = customerMapper.selectByPrimaryKey(id);
        customer.setIsValid(0);
        customer.setUpdateDate(new Date());
        AssertUtil.isTrue(customerMapper.updateByPrimaryKeySelective(customer)!=1,"删除失败");
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateState(){
        //查询处于流失状态的客户
        List<Customer> customerList  = customerMapper.queryLossCustomer();

        if(customerList!=null&&customerList.size()>0){
            List<Integer> customerIds = new ArrayList<>();
            List<CustomerLoss> customerLossList = new ArrayList<>();
            customerList.forEach(customer -> {
                CustomerLoss customerLoss = new CustomerLoss();
                customerLoss.setCreateDate(new Date());
                //0暂缓流失，1确认流失
                customerLoss.setState(0);
                customerLoss.setIsValid(1);
                customerLoss.setUpdateDate(new Date());
                customerLoss.setCusNo(customer.getKhno());
                customerLoss.setCusName(customer.getName());
                customerLoss.setCusManager(customer.getCusManager());
                CustomerOrder customerOrder = customerOrderMapper.queryLastOrderByCustomerId(customer.getId());
                if(customerOrder!=null){
                    customerLoss.setLastOrderTime(customerOrder.getOrderDate());
                }
                customerLossList.add(customerLoss);
                customerIds.add(customer.getId());
            });
            AssertUtil.isTrue(customerLossMapper.insertBatch(customerLossList)!=customerLossList.size(),"操作失败");
            AssertUtil.isTrue(customerMapper.updateLossStateByCustomerId(customerIds)!=customerIds.size(),"操作失败");
        }
    }

    private void checkParams( String phone, String fr) {
        AssertUtil.isTrue(StringUtils.isBlank(fr),"法人代表不能为空");
        AssertUtil.isTrue(!PhoneUtil.isMobile(phone),"电话号码不能为空且格式需要正确");
    }
}
