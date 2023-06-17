package com.xxxx.crm;

import com.alibaba.fastjson.JSON;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.exceptions.AuthException;
import com.xxxx.crm.exceptions.NoLoginException;
import com.xxxx.crm.exceptions.ParamsException;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class GlobalExceptionResolver implements HandlerExceptionResolver {
    /**
     * 全局异常处理
     * 1.返回ResultInfo(json数据)
     * 2.返回ModelAndView(视图)
     * 判断HandlerMethod的返回值
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @return
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ModelAndView modelAndView=new ModelAndView();
        //判断异常是否是未登录异常
        if(ex instanceof NoLoginException){
            //重定向到登录页面
            modelAndView.setViewName("redirect:/index");
            return modelAndView;
        }

        //默认情况下的异常处理
        modelAndView.setViewName("error");
        modelAndView.addObject("code", 500);
        modelAndView.addObject("msg", "系统异常，请重试...");
        //判断handler是不是handlerMethod
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            //获取返回值类型
            Class<?> returnType = handlerMethod.getMethod().getReturnType();
            if (returnType == ModelAndView.class) {//返回视图
                //判断异常类型
                if (ex instanceof ParamsException) {
                    ParamsException e = (ParamsException) ex;
                    modelAndView.addObject("code", e.getCode());
                    modelAndView.addObject("msg", e.getMsg());
                }else if(ex instanceof AuthException){
                    AuthException e = (AuthException) ex;
                    modelAndView.addObject("code", e.getCode());
                    modelAndView.addObject("msg", e.getMsg());
                }
                return modelAndView;
            } else {//返回ResultInfo
                //设置默认异常处理
                ResultInfo resultInfo = new ResultInfo();
                resultInfo.error(500, "系统异常，请重试");
                //判断异常类型
                if (ex instanceof ParamsException) {
                    ParamsException e = (ParamsException) ex;
                    resultInfo.error(e.getCode(), e.getMsg());
                }else if(ex instanceof AuthException){
                    AuthException e = (AuthException) ex;
                    resultInfo.error(e.getCode(), e.getMsg());
                }
                //设置响应类型及编码格式Json类型
                response.setContentType("application/json;charset=UTF-8");
                //得到字符输出流
                PrintWriter out = null;
                try {
                    out = response.getWriter();
                    //将对象转换成Json格式的字符串
                    String resultInfoJson = JSON.toJSONString(resultInfo);
                    //输出数据
                    out.write(resultInfoJson);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    if (out != null) {
                        out.close();
                    }
                }
                return null;
            }
        }


        return modelAndView;
    }
}
