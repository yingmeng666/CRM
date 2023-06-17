package com.xxxx.crm.task;

import com.xxxx.crm.service.CustomerService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

//@Component
public class JobTask {
    @Resource
    private CustomerService customerService;
    //@Scheduled(cron = "0/2 * * * * ?")
    public void job(){
        customerService.updateState();
    }
}
