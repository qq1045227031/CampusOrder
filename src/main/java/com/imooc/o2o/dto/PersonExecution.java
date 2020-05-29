package com.imooc.o2o.dto;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PersonExecution {
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
    private String createTime;
    private String lastEditTime;
    private Integer enableStatus;
    public PersonExecution() {
    }
    public PersonExecution(Person person) {
        this.userId = person.getUserId();
        this.name = person.getName();
        this.birthday = person.getBirthday();
        this.gender = person.getGender();
        this.phone = person.getPhone();
        this.email = person.getEmail();
        this.profileImg = person.getProfileImg();
        this.customerFlag = person.getCustomerFlag();
        this.shopOwnerFlag = person.getShopOwnerFlag();
        this.adminFlag = person.getAdminFlag();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String create = formatter.format(person.getCreateTime());
        String last = formatter.format(person.getLastEditTime());
        this.createTime = create;
        this.lastEditTime = last;
        this.enableStatus = person.getEnableStatus();
    }
}
