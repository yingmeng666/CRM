package com.xxxx.crm.query;

import com.xxxx.crm.base.BaseQuery;

public class CustomerLossQuery extends BaseQuery {

    private Integer state;
    private String cusName;

    private Integer cusNo;

    public String getCusName() {
        return cusName;
    }

    public void setCusName(String cusName) {
        this.cusName = cusName;
    }

    public Integer getCusNo() {
        return cusNo;
    }

    public void setCusNo(Integer cusNo) {
        this.cusNo = cusNo;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }


}
