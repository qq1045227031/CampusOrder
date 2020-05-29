package com.imooc.o2o.dto;

import com.imooc.o2o.entity.PersonInfo;
import com.imooc.o2o.enums.PersonInfoStateEnum;

import java.util.List;

public class PersonInfoExecution {
    // 结果状态
    private int state;

    // 状态标识
    private String stateInfo;

    // 店铺数量
    private int count;

    // 操作的personInfo（增删改店铺的时候用）
    private PersonInfo personInfo;

    // 获取的personInfo列表(查询店铺列表的时候用)
    private List<PersonInfo> personInfoList;

    public PersonInfoExecution() {
    }

    // 失败的构造器
    public PersonInfoExecution(PersonInfoStateEnum stateEnum) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
    }

    // 成功的构造器
    public PersonInfoExecution(PersonInfoStateEnum stateEnum,
                               PersonInfo personInfo) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
        this.personInfo = personInfo;
    }

    // 成功的构造器
    public PersonInfoExecution(PersonInfoStateEnum stateEnum,
                               List<PersonInfo> personInfoList) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
        this.personInfoList = personInfoList;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public PersonInfo getPersonInfo() {
        return personInfo;
    }

    public void setPersonInfo(PersonInfo personInfo) {
        this.personInfo = personInfo;
    }

    public List<PersonInfo> getPersonInfoList() {
        return personInfoList;
    }

    public void setPersonInfoList(List<PersonInfo> personInfoList) {
        this.personInfoList = personInfoList;
    }
}
