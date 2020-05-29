package com.imooc.o2o.dto;

import com.imooc.o2o.entity.Area;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.entity.ShopAuthMap;
import com.imooc.o2o.entity.ShopCategory;

import java.util.Date;
import java.util.List;

public class FShop {

    private Long shopId;
    private Long ownerId;
    private Long shopCategoryId;
    private String shopName;
    private String shopDesc;
    private String shopAddr;
    private String phone;
    private String shopImg;
    private Double longitude;
    private Double latitude;
    private Integer priority;
    private Date createTime;
    private Date lastEditTime;
    private Integer enableStatus;
    private String advice;

    private List<ShopAuthMap> staffList;
    private Area area;
    private ShopCate shopCategory;
    private ShopCate  parentCategory;

    public FShop() {
    }
    public FShop(Shop shop) {
        if (shop.getShopId()!=null) {
            this.shopId = shop.getShopId();
        }
        if (shop.getShopName() != null) {
            this.shopName = shop.getShopName();
        }
        if (shop.getShopAddr()!=null){
            this.shopAddr = shop.getShopAddr();
        }
        if (shop.getShopDesc() != null) {
            this.shopDesc = shop.getShopDesc();
        }
        if (shop.getPhone() != null) {
            this.phone = shop.getPhone();
        }
        if (shop.getShopImg() != null) {
            this.shopImg = shop.getShopImg();
        }
        if (shop.getPriority() != null) {
            this.priority = Integer.valueOf(shop.getPriority());
        }
        if (shop.getCreateTime() != null) {
            this.createTime = shop.getCreateTime();
        }
        if (shop.getLastEditTime() != null) {
            this.lastEditTime = shop.getLastEditTime();
        }
        if (shop.getEnableStatus() != null){
            this.enableStatus = shop.getEnableStatus();
       }if (shop.getAdvice()!=null) {
            this.advice = shop.getAdvice();
        }if (shop.getArea()!=null) {
            this.area = shop.getArea();
        }if (shop.getShopCategory()!=null&&shop.getShopCategory().getShopCategoryId()!=null) {
            this.shopCategory = new ShopCate(shop.getShopCategory());
            this.shopCategoryId = shop.getShopCategory().getShopCategoryId();
        }
        if (shop.getShopCategory()!=null&&shop.getShopCategory().getParent()!=null&&shop.getShopCategory().getParent().getShopCategoryId()!=null){
            this.parentCategory = new ShopCate(shop.getShopCategory().getParent());
        }
        if (shop.getOwner()!=null&&shop.getOwner().getUserId()!=null){
            this.ownerId = shop.getOwner().getUserId();
        }

    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getShopCategoryId() {
        return shopCategoryId;
    }

    public void setShopCategoryId(Long shopCategoryId) {
        this.shopCategoryId = shopCategoryId;
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

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
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

    public Integer getEnableStatus() {
        return enableStatus;
    }

    public void setEnableStatus(Integer enableStatus) {
        this.enableStatus = enableStatus;
    }

    public List<ShopAuthMap> getStaffList() {
        return staffList;
    }

    public void setStaffList(List<ShopAuthMap> staffList) {
        this.staffList = staffList;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }


    public String getAdvice() {
        return advice;
    }

    public void setAdvice(String advice) {
        this.advice = advice;
    }

    public String toString() {
        return "[shopId=" + shopId + ", shopName=" + shopName + "]";
    }

    public ShopCate getShopCategory() {
        return shopCategory;
    }

    public void setShopCategory(ShopCate shopCategory) {
        this.shopCategory = shopCategory;
    }

    public ShopCate getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(ShopCate parentCategory) {
        this.parentCategory = parentCategory;
    }
}

