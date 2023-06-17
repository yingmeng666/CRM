package com.xxxx.crm.config;

import com.xxxx.crm.interceptor.NoLoginInterceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration//配置类
public class MvcConfig extends WebMvcConfigurerAdapter {

    //获取拦截器实体
    @Bean//返回值交给IOC容器管理
    public NoLoginInterceptor noLoginInterceptor() {
        return new NoLoginInterceptor();
    }

    /**
     * 添加拦截器方法
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //需要一个实现了拦截器功能的实例对象，这里是noLoginInterceptor
        registry.addInterceptor(noLoginInterceptor())
                //设置需要拦截的请求
                .addPathPatterns("/**")//默认所有
                //设置放行请求
                .excludePathPatterns("/css/**","/images/**","/js/**","/lib/**","/index","/users/login");
    }
}