package com.imooc.o2o.dao;

import com.imooc.o2o.BaseTest;
import com.imooc.o2o.entity.PersonInfo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

public class PersonInfoDaoTest extends BaseTest {
    @Autowired
    private PersonInfoDao personInfoDao;
    @Test
    public void TestInsert(){
        PersonInfo personInfo = new PersonInfo();
        personInfo.setName("我爱你");
        personInfo.setGender("女");
        personInfo.setCreateTime(new Date());
        personInfo.setLastEditTime(new Date());
        personInfo.setEnableStatus(1);
        personInfo.setProfileImg("没有图片");
        personInfo.setUserType(1);
        int effectedNum = personInfoDao.insertPersonInfo(personInfo);

    }
    @Test
    public void Testquery(){
        PersonInfo personInfo = personInfoDao.queryPersonInfoById(1l);
        System.out.println(personInfo.getName());
    }
    @Test
    public void TestC(){
        PersonInfo personInfo = new PersonInfo();
//        personInfo.setName("测试");
//        personInfo.setUserType(2);
//        personInfo.setEnableStatus(1);
//        personInfo.setAddress("的");
        personInfo.setPhone("11");
        List<PersonInfo> personInfos = personInfoDao.queryPersonInfoList(personInfo,0,100);
        for (PersonInfo p : personInfos){
            System.out.println(p.getName());
        }
        int eff = personInfoDao.queryPersonInfoCount(personInfo);
        System.out.println(eff);
    }
    @Test
    public void testD(){
        PersonInfo personInfo = new PersonInfo();
        personInfo.setUserId(6l);
        personInfo.setEnableStatus(0);
        int eff = personInfoDao.updatePersonInfo(personInfo);
        System.out.println(eff);
    }
    @Test
    public void testDel(){
//        int eff = personInfoDao.deletePersonInfo(6l);
//        System.out.println(eff);
        PersonInfo personInfo = new PersonInfo();
        personInfo.setName("测试插入");
        personInfo.setPhone("111");
        personInfo.setUserType(1);
        personInfo.setEnableStatus(1);
        int eff = personInfoDao.insertPersonInfo(personInfo);
        System.out.println(eff);
    }
}
