package com.imooc.o2o.service;

import com.imooc.o2o.BaseTest;
import com.imooc.o2o.dto.LocalAuthExecution;
import com.imooc.o2o.entity.LocalAuth;
import com.imooc.o2o.entity.PersonInfo;
import com.imooc.o2o.util.MD5;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class LocalAuthServiceTest extends BaseTest {
    @Autowired
    private LocalAuthService localAuthService;
    String name = "testservice";
    String password = "testservice";
    @Test
    public void testBindLocalAuth(){
        LocalAuth localAuth = new LocalAuth();
        localAuth.setUsername(name);
        localAuth.setPassword(password);
        localAuth.setCreateTime(new Date());
        localAuth.setLastEditTime(new Date());
        PersonInfo personInfo = new PersonInfo();
        personInfo.setUserId(3l);
        localAuth.setPersonInfo(personInfo);
        LocalAuthExecution lae = localAuthService.bindLocalAuth(localAuth);
        localAuth = localAuthService.getLocalAuthByUserId(3l);
        System.out.println("名称为"+localAuth.getPersonInfo().getName());
        System.out.println("秘密为"+localAuth.getPassword());

    }
    @Test
    public void addassword(){
        LocalAuth localAuth = new LocalAuth();
        localAuth.setUsername("123111");
        localAuth.setPassword(MD5.getMd5("root"));
        PersonInfo personInfo = new PersonInfo();
        personInfo.setUserId(13l);
        localAuth.setPersonInfo(personInfo);
       LocalAuthExecution localAuthExecution=  localAuthService.addLocalAuth(localAuth);
       System.out.println(localAuthExecution.getStateInfo());
    }
    @Test
    public void TestModifyPassword(){
        long userId = 3;
        String newPassword = "newservicepassword";
        localAuthService.modifyLocalAuth(userId,name,password,newPassword);
        LocalAuthExecution lae = localAuthService.modifyLocalAuth(3l,"testservice","testservice","newservicepassword");//里面只有状态值
        LocalAuth localAuth = localAuthService.getLocalAuthByUserNameAndPwd(name,newPassword);
        System.out.println(localAuth.getPersonInfo().getName());
    }

}
