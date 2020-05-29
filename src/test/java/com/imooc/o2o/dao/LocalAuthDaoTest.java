package com.imooc.o2o.dao;

import com.imooc.o2o.BaseTest;
import com.imooc.o2o.entity.LocalAuth;
import com.imooc.o2o.entity.PersonInfo;
import com.imooc.o2o.service.LocalAuthService;
import com.imooc.o2o.util.MD5;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class LocalAuthDaoTest extends BaseTest {
    @Autowired
    private LocalAuthDao localAuthDao;
    @Autowired
            private LocalAuthService localAuthService;
    String name = "123456";
    String password = "123456";
    @Test
    public void TestAInsertLocalAuth(){
        LocalAuth localAuth = new LocalAuth();
        localAuth.setUsername(name);
        localAuth.setPassword(password);
        localAuth.setCreateTime(new Date());
        localAuth.setLastEditTime(new Date());
        PersonInfo personInfo = new PersonInfo();
        personInfo.setUserId(1l);
        localAuth.setPersonInfo(personInfo);
        int eff = localAuthDao.insertLocalAuth(localAuth);
        System.out.println(eff);
    }
    @Test
    public void queryByIdAndPassword(){
//       LocalAuth localAuth =  localAuthDao.queryLocalByUserNameAndPwd(name,password);
//       System.out.println(localAuth.getUsername()+" "+localAuth.getPassword());
        LocalAuth localAuth =  localAuthService.getLocalAuthByUserNameAndPwd("root","root");
        System.out.println(localAuth.getPersonInfo().getUserType());
    }
    @Test
    public void queryById(){
        //通过userid查
        LocalAuth localAuth = localAuthDao.queryLocalByUserId(1l);
        System.out.println(localAuth.getPersonInfo().getName());
    }
    @Test
    public void updateLocalAuth(){
        //修改密码
        Date now = new Date();
//        int effect = localAuthDao.updateLocalAuth(3l,name,password,password+"new ",now);
        String name = "testservice";
        String password = "newservicepassword";
        String newPassword = "testservice";
        int effect = localAuthDao.updateLocalAuth(3l,name, MD5.getMd5(password),
                MD5.getMd5(newPassword), now);
        System.out.println(effect);

    }

}
