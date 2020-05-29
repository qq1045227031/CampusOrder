package com.imooc.o2o.entity;

import java.util.Date;
//顾客在某个店铺总积分映射
public class UserShopMap {
    private Long userShopId;
    private Date createTime;
    //顾客在该商铺的积分
    private Integer point;
    //用户
    private PersonInfo user;
    //店铺
    private Shop shop;

    public Long getUserShopId() {
        return userShopId;
    }

    public void setUserShopId(Long userShopId) {
        this.userShopId = userShopId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    public PersonInfo getUser() {
        return user;
    }

    public void setUser(PersonInfo user) {
        this.user = user;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }
}
