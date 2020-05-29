package com.imooc.o2o.entity;

import com.imooc.o2o.dto.ShopCate;

import java.util.Date;

public class ShopCategory {
    private Long shopCategoryId;
    private String shopCategoryName;
    private String shopCategoryDesc;
    private String shopCategoryImg;
    private Integer priority;
    private Date createTime;
    private Date lastEditTime;
    private ShopCategory parent;//店铺类别

    public ShopCategory() {
    }

    public ShopCategory(ShopCate shopCate) {
        this.shopCategoryId=shopCate.getShopCategoryId();
        this.shopCategoryName=shopCate.getShopCategoryName();
        this.shopCategoryDesc=shopCate.getShopCategoryDesc();
        this.shopCategoryImg = shopCate.getShopCategoryImg();
        this.priority = shopCate.getPriority();
        this.createTime=shopCate.getCreateTime();
        this.lastEditTime=shopCate.getLastEditTime();
        ShopCategory parent = new ShopCategory();
        parent.setShopCategoryId(shopCate.getParentId());
        this.parent = parent;

    }

    public ShopCategory(Long shopCategoryId) {
        this.shopCategoryId = shopCategoryId;
    }

    public Long getShopCategoryId() {
        return shopCategoryId;
    }

    public void setShopCategoryId(Long shopCategoryId) {
        this.shopCategoryId = shopCategoryId;
    }

    public String getShopCategoryName() {
        return shopCategoryName;
    }

    public void setShopCategoryName(String shopCategoryName) {
        this.shopCategoryName = shopCategoryName;
    }

    public String getShopCategoryDesc() {
        return shopCategoryDesc;
    }

    public void setShopCategoryDesc(String shopCategoryDesc) {
        this.shopCategoryDesc = shopCategoryDesc;
    }

    public String getShopCategoryImg() {
        return shopCategoryImg;
    }

    public void setShopCategoryImg(String shopCategoryImg) {
        this.shopCategoryImg = shopCategoryImg;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastEditTime() {
        return lastEditTime;
    }

    public void setLastEditTime(Date lastEditTime) {
        this.lastEditTime = lastEditTime;
    }

    public ShopCategory getParent() {
        return parent;
    }

    public void setParent(ShopCategory parent) {
        this.parent = parent;
    }
}
