package com.imooc.o2o.entity;

import com.imooc.o2o.dto.Person;

import java.util.Date;
//用户信息实体类
public class PersonInfo {
    private Long userId;
    private String name;
    private String profileImg;//头像地址
    private String email;
    private String gender;//性别
    private Integer enableStatus;//用户状态
    private Integer userType;//用户权限  1顾客 2.店家 3.超级管理员
    private Date createTime;//创建时间
    private Date lastEditTime;//最后修改时间
    private String phone;
    private String address;

    public PersonInfo() {
    }

    public PersonInfo(Person person) {
        this.userId = person.getUserId();
        this.name =person.getName() ;
        this.gender = person.getGender();
        this.profileImg = person.getProfileImg();
        this.email = person.getEmail();
        this.createTime = person.getCreateTime();
        this.lastEditTime = person.getLastEditTime();
        this.phone = person.getPhone();
        this.email = person.getEmail();
        if (person.getCustomerFlag()!=null&&person.getCustomerFlag()==1){
            this.enableStatus = 1;
        }else if (person.getCustomerFlag()!=null&&person.getCustomerFlag()==2){
            this.enableStatus = 2;
        }else   if (person.getCustomerFlag()!=null&&person.getCustomerFlag()==3){
            this.enableStatus = 3;
        }
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getEnableStatus() {
        return enableStatus;
    }

    public void setEnableStatus(Integer enableStatus) {
        this.enableStatus = enableStatus;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
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
}
