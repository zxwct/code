package org.example.springboot.entity;

import java.math.BigDecimal;

/**
 * 图书信息
 */


public class Collect {

    private Integer id;
    private Integer userId;
    private Integer itemId;
    private String time;

    private BigDecimal collectPrice;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public BigDecimal getCollectPrice() {
        return collectPrice;
    }

    public void setCollectPrice(BigDecimal collectPrice) {
        this.collectPrice = collectPrice;
    }
}
