package com.xxxx.crm.query;

import com.xxxx.crm.base.BaseQuery;

public class CustomerOrderQuery extends BaseQuery {
    private Integer customerId;

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }
}
