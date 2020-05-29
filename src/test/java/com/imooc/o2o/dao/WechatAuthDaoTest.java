package com.imooc.o2o.dao;


import com.imooc.o2o.BaseTest;
import com.imooc.o2o.entity.PersonInfo;
import com.imooc.o2o.entity.WechatAuth;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;


public class WechatAuthDaoTest extends BaseTest {
    @Autowired WechatAuthDao wechatAuthDao;
    @Test
    public void queryWechatInfoByOpenId() {
        WechatAuth wechatAuth = wechatAuthDao.queryWechatInfoByOpenId("dafahizhfdhaih");
        System.out.println(wechatAuth.getWechatAuthId());
        System.out.println(wechatAuth.getPersonInfo().getName());

    }

    @Test
    public void insertWechatAuth() {
        WechatAuth wechatAuth = new WechatAuth();
        PersonInfo personInfo = new PersonInfo();
        personInfo.setUserId(1L);

        wechatAuth.setPersonInfo(personInfo);
        wechatAuth.setOpenId("dafahizhfdhaih");
        wechatAuth.setCreateTime(new Date());
        int effectedNum = wechatAuthDao.insertWechatAuth(wechatAuth);
        System.out.println(effectedNum);
    }

    @Test
    public void deleteWechatAuth() {
    }
}
