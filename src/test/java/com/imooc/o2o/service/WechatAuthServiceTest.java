package com.imooc.o2o.service;

import com.imooc.o2o.BaseTest;
import com.imooc.o2o.dao.WechatAuthDao;
import com.imooc.o2o.dto.WechatAuthExecution;
import com.imooc.o2o.entity.PersonInfo;
import com.imooc.o2o.entity.WechatAuth;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class WechatAuthServiceTest extends BaseTest {
    @Autowired
    private WechatAuthService wechatAuthService;
    @Test
    public void testRegister(){
        //测试通过传入wechat类其中包含personinfo但是没有userid来创建用户
        WechatAuth wechatAuth = new WechatAuth();

        PersonInfo personInfo = new PersonInfo();
        String openId = "qweqweqweqwe";
        personInfo.setCreateTime(new Date());
        personInfo.setName("测试");
        personInfo.setUserType(1);
        wechatAuth.setPersonInfo(personInfo);
        wechatAuth.setOpenId(openId);
        wechatAuth.setCreateTime(new Date());
        WechatAuthExecution we = wechatAuthService.register(wechatAuth);
        System.out.println(we.getWechatAuth().getWechatAuthId());
        wechatAuth = wechatAuthService.getWechatAuthByOpenId(openId);
        System.out.println(wechatAuth.getPersonInfo().getName());

    }


}
