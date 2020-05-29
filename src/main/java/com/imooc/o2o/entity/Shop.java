package com.imooc.o2o.entity;

import com.imooc.o2o.dto.FShop;
import com.imooc.o2o.dto.Person;

import java.util.Date;

public class Shop {
    private Long shopId;
    private String shopName;
    private String shopDesc;
    private String shopAddr;
    private String phone;
    private String shopImg;
    private String priority;
    private Date createTime;
    private Date lastEditTime;
    private Integer enableStatus;//-1不可用 2.审核中 3.可用
    private String advice;//超级管理员给商家的提醒
    private Area area;
    private PersonInfo owner;//用户信息。表示是由谁创建的
    private ShopCategory shopCategory;//店铺类别

    public Shop() {
    }
    public Shop(FShop fShop) {
        if (fShop.getShopId()!=null) {
            this.shopId = fShop.getShopId();
        }
        if (fShop.getShopName()!=null){
            this.shopName= fShop.getShopName();
        }
        if (fShop.getShopDesc()!=null){
            this.shopDesc = fShop.getShopDesc();
        }
        if (fShop.getShopAddr()!=null){
            this.shopAddr = fShop.getShopAddr();
        }
        if (fShop.getPhone()!=null){
            this.phone = fShop.getPhone();
        }
        if (fShop.getShopImg()!=null){
            this.shopImg = fShop.getShopImg();
        }
        if (fShop.getPriority()!=null){
            this.priority = ""+fShop.getPriority();
        }
        if (fShop.getCreateTime()!=null){
            this.createTime = fShop.getCreateTime();
        }
        if (fShop.getLastEditTime()!=null){
            this.lastEditTime = fShop.getLastEditTime();
        }
        if (fShop.getEnableStatus()!=null){
            this.enableStatus = fShop.getEnableStatus();
        }
        if (fShop.getAdvice()!=null){
            this.advice = fShop.getAdvice();
        }
        if (fShop.getArea()!=null&&fShop.getArea().getAreaId()!=null){
        this.area = fShop.getArea();
        }
        if (fShop.getShopCategory()!=null){
            ShopCategory shopCategory  = new ShopCategory(fShop.getShopCategory());
            this.shopCategory = shopCategory;
        }
        if (fShop.getShopCategoryId()!=null){
            ShopCategory shopCategory= new ShopCategory();
            shopCategory.setShopCategoryId(fShop.getShopCategoryId());
            this.shopCategory = shopCategory;
        }
        if (fShop.getOwnerId()!=null){
            PersonInfo  personInfo = new PersonInfo();
            personInfo.setUserId(fShop.getOwnerId());
            this.owner=personInfo;
        }
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopDesc() {
        return shopDesc;
    }

    public void setShopDesc(String shopDesc) {
        this.shopDesc = shopDesc;
    }

    public String getShopAddr() {
        return shopAddr;
    }

    public void setShopAddr(String shopAddr) {
        this.shopAddr = shopAddr;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getShopImg() {
        return shopImg;
    }

    public void setShopImg(String shopImg) {
        this.shopImg = shopImg;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
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

    public Integer getEnableStatus() {
        return enableStatus;
    }

    public void setEnableStatus(Integer enableStatus) {
        this.enableStatus = enableStatus;
    }

    public String getAdvice() {
        return advice;
    }

    public void setAdvice(String advice) {
        this.advice = advice;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public PersonInfo getOwner() {
        return owner;
    }

    public void setOwner(PersonInfo owner) {
        this.owner = owner;
    }

    public ShopCategory getShopCategory() {
        return shopCategory;
    }

    public void setShopCategory(ShopCategory shopCategory) {
        this.shopCategory = shopCategory;
    }
}
