package com.xxxx.crm.service;

import com.xxxx.crm.Model.UserModel;
import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.UserMapper;
import com.xxxx.crm.dao.UserRoleMapper;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.Md5Util;
import com.xxxx.crm.utils.PhoneUtil;
import com.xxxx.crm.utils.UserIDBase64;
import com.xxxx.crm.vo.User;
import com.xxxx.crm.vo.UserRole;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Service
public class UserService extends BaseService<User, Integer> {
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserRoleMapper userRoleMapper;

    /**
     * 登录功能
     * 1.参数判断(非空)
     * 参数校验不通过抛出自定义异常(异常被全局异常处理模式处理)
     * 2.调用dao层的通过用户名查询用户的方法，返回用户对象
     * 3.判断用户是否为空
     * 为空，抛异常(异常被全局异常处理模式处理)
     * 4.不为空，判断密码是否正确
     * 密码不正确抛出异常(异常被全局异常处理模式处理)
     * 5.密码正确，返回登录成功的统一结果模型
     *
     * @param userName
     * @param userPwd
     * @return
     */
    public UserModel login(String userName, String userPwd) {
        //参数判断
        checkParams(userName, userPwd);
        //2.调用dao层的通过用户名查询用户的方法，返回用户对象
        User user = userMapper.queryUserByUserName(userName);
        AssertUtil.isTrue(user == null, "该用户不存在");
        AssertUtil.isTrue(user.getIsValid()!=1,"该用户已失效请联系管理员");
        //判断密码
        checkPwd(userPwd, user.getUserPwd());
        return getUserModel(user);
    }

    /**
     * 修改密码功能
     * 1.参数的非空判断
     * 2.通过userId查询是否有该用户
     * 3.判断oldPwd是否和用户对象密码相同
     * 4.存在判断oldPwd和newPwd是否相同
     * 5.不相同判断repeatPwd是否正确
     * 6.正确调用dao层方法修改密码，返回受影响行数row
     * 7.判断row，判断是否修改成功
     *
     * @param userId
     * @param oldPwd
     * @param newPwd
     * @param repeatPwd
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updatePassword(Integer userId, String oldPwd, String newPwd, String repeatPwd) {
        //登录状态下就有该用户，未查到说明时系统错误
        User user = userMapper.selectByPrimaryKey(userId);
        AssertUtil.isTrue(user == null, "系统错误，修改失败！");
        //参数的非空判断
        AssertUtil.isTrue(StringUtils.isBlank(oldPwd)
                || StringUtils.isBlank(newPwd)
                || StringUtils.isBlank(repeatPwd), "必填项不能为空");
        //密码逻辑判断
        checkPwdParam(user, oldPwd, newPwd, repeatPwd);
        //正确调用dao层方法修改密码(需要加密)，返回受影响行数row
        //判断row，判断是否修改成功
        user.setUserPwd(Md5Util.encode(newPwd));
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user) < 1, "修改密码失败！");
    }

    /**
     * 修改密码逻辑判断
     * 修改密码不能和老密码相同
     * 确定密码正确
     *
     * @param oldPwd
     * @param newPwd
     * @param repeatPwd
     */
    private void checkPwdParam(User user, String oldPwd, String newPwd, String repeatPwd) {
        AssertUtil.isTrue(!user.getUserPwd().equals(Md5Util.encode(oldPwd)), "原始密码不正确！");
        AssertUtil.isTrue(oldPwd.equals(newPwd), "新密码不能和原密码相同！");
        AssertUtil.isTrue(!newPwd.equals(repeatPwd), "新密码和确认密码不一致！");
    }

    /**
     * 登录返回的用户模型
     *
     * @param user
     * @return
     */
    private UserModel getUserModel(User user) {
        UserModel userModel = new UserModel();
        userModel.setTrueName(user.getTrueName());
        userModel.setUserName(user.getUserName());
        userModel.setUserIdStr(UserIDBase64.encoderUserID(user.getId()));
        return userModel;
    }

    /**
     * 密码判断
     *
     * @param userPwd
     * @param truePwd
     */
    private void checkPwd(String userPwd, String truePwd) {
        userPwd = Md5Util.encode(userPwd);
        AssertUtil.isTrue(!userPwd.equals(truePwd), "用户密码不正确");
    }

    /**
     * 登录参数判断非空
     *
     * @param userName
     * @param userPwd
     */
    private void checkParams(String userName, String userPwd) {
        AssertUtil.isTrue(StringUtils.isBlank(userName), "用户名不能为空！");
        AssertUtil.isTrue(StringUtils.isBlank(userPwd), "用户密码不能为空！");
    }

    /**
     * 查询所有销售人员的名字和id
     *
     * @return
     */
    public List<Map<String, Object>> queryAllSales() {
        return userMapper.queryAllSales();
    }

    /**
     * 用户添加
     *
     * @param user
     */

    @Transactional(propagation = Propagation.REQUIRED)
    public void addUser(User user) {
        //参数校验
        //1.用户名非空且唯一，email非空，phone格式正确且非空
        checkUserParams(user.getUserName(), user.getEmail(), user.getPhone(), null);
        //默认值添加isValid=1，createDate&updateDate为系统当前时间
        user.setIsValid(1);
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        //设置默认密码123456加密存入
        user.setUserPwd(Md5Util.encode("123456"));
        //调用dao层新增方法判断受影响行数
        AssertUtil.isTrue(userMapper.insertSelective(user) != 1, "添加新用户失败！");
        //用户角色关联
        relationUserRole(user.getId(), user.getRoleIds());
    }

    /**
     * 用户角色关联
     * 1.未传入roleIds不做操作
     * 2.传入了roleIds删除原有联系，添加传来的联系
     *
     * @param userId
     * @param roleIds
     */
    private void relationUserRole(Integer userId, String roleIds) {
        //查询数据库中有与该用户联系的角色数量
        Integer count = userRoleMapper.countUserRoleByUserId(userId);
        //判断记录是否存在
        if (count > 0) {
            AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId) != count, "角色分配失败请重试！");
        }
        //判断是否有新分配的角色
        if (StringUtils.isNotBlank(roleIds)) {
            List<UserRole> userRoleList = new ArrayList<>();
            String[] roleIdsArray = roleIds.split(",");
            for (String roleId : roleIdsArray) {
                UserRole userRole = new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(Integer.parseInt(roleId));
                userRoleList.add(userRole);
            }
            AssertUtil.isTrue(userRoleMapper.insertBatch(userRoleList) != userRoleList.size(), "角色分配失败请重试！");
        }
    }

    /**
     * 用户更新
     *
     * @param user
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUser(User user) {
        //参数校验
        //1.用户id不为空且数据库有记录
        AssertUtil.isTrue(user.getId() == null, "系统错误请重试...");
        User temp = userMapper.selectByPrimaryKey(user.getId());
        AssertUtil.isTrue(temp == null, "系统错误请重试...");
        //2.用户名非空且唯一，email非空，phone格式正确且非空
        checkUserParams(user.getUserName(), user.getEmail(), user.getPhone(), user.getId());
        //3.设置更新时间
        user.setUpdateDate(new Date());
        //调用dao层更新方法，判断受影响行数
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user) != 1, "用户更新失败！");
    }

    /**
     * 用户添加的参数校验
     *
     * @param userName
     * @param email
     * @param phone
     */
    private void checkUserParams(String userName, String email, String phone, Integer userId) {
        AssertUtil.isTrue(StringUtils.isBlank(userName), "用户名不能为空！");
        User user = userMapper.queryUserByUserName(userName);
        AssertUtil.isTrue(user != null && !user.getId().equals(userId), "用户名已存在！");
        AssertUtil.isTrue(StringUtils.isBlank(email), "邮箱不能为空！");
        AssertUtil.isTrue(StringUtils.isBlank(phone), "手机号码不能为空！");
        AssertUtil.isTrue(!PhoneUtil.isMobile(phone), "手机格式不正确!");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteByIds(Integer[] ids) {
        //判断参数是否有值
        AssertUtil.isTrue(ids == null || ids.length < 1, "系统错误请重试...");
        //调用dao层方法判断受影响行数
        AssertUtil.isTrue(userMapper.deleteBatch(ids) != ids.length, "删除失败");

        //用户角色关联删除
        for (Integer userId : ids) {
            //查询是否有记录
            Integer count = userRoleMapper.countUserRoleByUserId(userId);
            if (count > 0) {
                //删除
                AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId) != count, "删除失败！");
            }
        }
    }
}
