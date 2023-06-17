package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.dao.PermissionMapper;
import com.xxxx.crm.service.PermissionService;
import com.xxxx.crm.service.UserService;
import com.xxxx.crm.utils.CookieUtil;
import com.xxxx.crm.utils.LoginUserUtil;
import com.xxxx.crm.vo.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Controller
public class IndexController extends BaseController {
    @Resource
    private UserService userService;
    @Resource
    private PermissionService permissionService;
    /**
     * 系统登录页
     *
     * @return
     */
    @RequestMapping("index")
    public ModelAndView index(ModelAndView modelAndView) {
        modelAndView.setViewName("index");
        return modelAndView;
    }


    // 系统界面欢迎页
    @RequestMapping("welcome")
    public ModelAndView welcome(ModelAndView modelAndView) {
        modelAndView.setViewName("welcome");
        return modelAndView;
    }

    /**
     * 后端管理主页面
     *
     * @return
     */
    @RequestMapping("main")
    public ModelAndView main(HttpServletRequest request,ModelAndView modelAndView) {
        //获取cookie中的user对象的userIdStr值并解密成integer类型
        int userId= LoginUserUtil.releaseUserIdFromCookie(request);
        //通过userId查询用户
        User user = userService.selectByPrimaryKey(userId);
        //存入session作用域中
        request.getSession().setAttribute("user",user);

        //通过userId查询用户拥有的权限
        List<String> permissions = permissionService.queryUserHasPermissonsByUserId(userId);

        request.getSession().setAttribute("permissions",permissions);

        modelAndView.setViewName("main");
        return modelAndView;
    }
}
