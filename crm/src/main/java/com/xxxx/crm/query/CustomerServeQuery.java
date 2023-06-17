package com.xxxx.crm.query;

import com.xxxx.crm.base.BaseQuery;

public class CustomerServeQuery extends BaseQuery {
    private String customer;
    private Integer serveType;
    private String state;
    private Integer assigner;

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public Integer getServeType() {
        return serveType;
    }

    public void setServeType(Integer serveType) {
        this.serveType = serveType;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setAssigner(Integer assigner) {
        this.assigner = assigner;
    }
    public Integer getAssigner(){
        return assigner;
    }
}
