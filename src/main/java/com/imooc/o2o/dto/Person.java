package com.imooc.o2o.dto;

import com.imooc.o2o.entity.PersonInfo;

import java.util.Date;

public class Person {
    private Long userId;
    private String name;
    private Date birthday;
    private String gender;
    private String phone;
    private String email;
    private String profileImg;
    private Integer customerFlag;
    private Integer shopOwnerFlag;
    private Integer adminFlag;
    private Date createTime;
    private Date lastEditTime;
    private Integer enableStatus;

    public Person() {
    }

    public Person(Long userId, String name, Date birthday, String gender, String phone, String email, String profileImg, Integer customerFlag, Integer shopOwnerFlag, Integer adminFlag, Date createTime, Date lastEditTime, Integer enableStatus) {
        this.userId = userId;
        this.name = name;
        this.birthday = birthday;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
        this.profileImg = profileImg;
        this.customerFlag = customerFlag;
        this.shopOwnerFlag = shopOwnerFlag;
        this.adminFlag = adminFlag;
        this.createTime = createTime;
        this.lastEditTime = lastEditTime;
        this.enableStatus = enableStatus;
    }

    public Person(PersonInfo personInfo) {
        this.userId = personInfo.getUserId();
        this.name = personInfo.getName();
        this.gender = personInfo.getGender();
        this.createTime = personInfo.getCreateTime();
        this.lastEditTime = personInfo.getCreateTime();
        this.enableStatus = personInfo.getEnableStatus();
        this.phone = personInfo.getPhone();
        this.profileImg = personInfo.getProfileImg();
        if (personInfo.getEnableStatus()==1){
            customerFlag=1;
            shopOwnerFlag = 0;
            adminFlag = 0;
        }else if (personInfo.getEnableStatus()==2){
            customerFlag=0;
            shopOwnerFlag = 1;
            adminFlag = 0;
        }else  if (personInfo.getEnableStatus()==3) {
            customerFlag=0;
            shopOwnerFlag = 0;
            adminFlag = 1;
        }
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public Integer getCustomerFlag() {
        return customerFlag;
    }

    public void setCustomerFlag(Integer customerFlag) {
        this.customerFlag = customerFlag;
    }

    public Integer getShopOwnerFlag() {
        return shopOwnerFlag;
    }

    public void setShopOwnerFlag(Integer shopOwnerFlag) {
        this.shopOwnerFlag = shopOwnerFlag;
    }

    public Integer getAdminFlag() {
        return adminFlag;
    }

    public void setAdminFlag(Integer adminFlag) {
        this.adminFlag = adminFlag;
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

}
