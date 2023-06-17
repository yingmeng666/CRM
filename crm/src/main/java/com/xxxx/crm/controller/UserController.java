package com.xxxx.crm.controller;

import com.xxxx.crm.Model.UserModel;
import com.xxxx.crm.annotation.RequiredPermission;
import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.dao.UserMapper;
import com.xxxx.crm.exceptions.ParamsException;
import com.xxxx.crm.query.UserQuery;
import com.xxxx.crm.service.UserService;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.LoginUserUtil;
import com.xxxx.crm.vo.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/users")
public class UserController extends BaseController {
    @Resource
    private UserService userService;
    @Resource
    private UserMapper userMapper;

    @PostMapping("/login")
    public ResultInfo login(String userName, String userPwd) {
        ResultInfo resultInfo = new ResultInfo();
        UserModel userModel = userService.login(userName, userPwd);
        resultInfo.success(userModel);
        /*
        try {
            UserModel userModel = userService.login(userName, userPwd);
            resultInfo.success(userModel);
        } catch (ParamsException ex) {
            resultInfo.error(ex.getCode(), ex.getMsg());
            ex.printStackTrace();
        } catch (Exception ex) {
            resultInfo.error(500, "登陆失败");
            ex.printStackTrace();
        }
         */
        return resultInfo;
    }

    @PostMapping("/updatePwd")
    public ResultInfo updateUserPwd(HttpServletRequest request,
                                    String oldPassword, String newPassword, String repeatPassword) {
        ResultInfo resultInfo = new ResultInfo();
        //从cookie中获取userIdSTtr解密出userId
        int userId = LoginUserUtil.releaseUserIdFromCookie(request);
        userService.updatePassword(userId, oldPassword, newPassword, repeatPassword);
        /*
        try {
            //从cookie中获取userIdSTtr解密出userId
            int userId = LoginUserUtil.releaseUserIdFromCookie(request);
            userService.updatePassword(userId, oldPassword, newPassword, repeatPassword);
        } catch (ParamsException ex) {
            resultInfo.error(ex.getCode(), ex.getMsg());
            ex.printStackTrace();
        } catch (Exception ex) {
            resultInfo.error(500, "修改密码失败");
            ex.printStackTrace();
        }
        */
        return resultInfo;
    }

    //进入修改密码的页面
    @RequestMapping("/toPasswordPage")
    public ModelAndView toPasswordPage(ModelAndView modelAndView) {
        modelAndView.setViewName("user/password");
        return modelAndView;
    }

    /**
     * 查询所有销售人员的姓名和id
     *
     * @return
     */
    @GetMapping("/querySales")
    public ResultInfo queryAllSales() {
        List<Map<String, Object>> mapList = userService.queryAllSales();
        ResultInfo resultInfo = new ResultInfo();
        resultInfo.success(mapList);
        return resultInfo;
    }
    @RequiredPermission(code = "601002")
    @RequestMapping("/list")
    public Map<String, Object> selectByParams(UserQuery userQuery) {
        return userService.queryByParamsForTable(userQuery);
    }

    @RequestMapping("/index")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("user/user");
        return modelAndView;
    }

    /**
     * 用户添加
     *
     * @param user
     * @return
     */
    @RequiredPermission(code = "601001")
    @PostMapping("/add")
    public ResultInfo addUser(User user) {
        userService.addUser(user);
        return success("添加新用户成功");
    }

    /**
     * 用户更新
     *
     * @param user
     * @return
     */
    @RequiredPermission(code = "601003")
    @PostMapping("/update")
    public ResultInfo updateUser(User user) {
        userService.updateUser(user);
        return success("更新成功");
    }

    /**
     * 进入添加或修改用户页面
     *
     * @return
     */
    @RequestMapping("/toAddOrUpdateUserPage")
    public ModelAndView toAddOrUpdateUserPage(Integer id) {
        ModelAndView modelAndView = new ModelAndView("user/add_update");
        if (id != null) {
            User user = userService.selectByPrimaryKey(id);
            AssertUtil.isTrue(user==null,"系统错误请重试...");
            modelAndView.addObject("userInfo",user);
        }
        return modelAndView;
    }
    @RequiredPermission(code = "601004")
    @PostMapping("/delete")
    public ResultInfo deleteUser(Integer[]ids){
        userService.deleteByIds(ids);
        return success("删除成功");
    }

    @PostMapping("/queryAllCustomerManager")
    public List<Map<String, Object>> queryAllCustomerManager(){
        return userMapper.queryAllCustomerManager();
    }
}
