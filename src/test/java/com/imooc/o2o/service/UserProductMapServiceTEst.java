package com.imooc.o2o.service;

import com.imooc.o2o.BaseTest;
import com.imooc.o2o.dto.UserProductMapExecution;
import com.imooc.o2o.entity.PersonInfo;
import com.imooc.o2o.entity.UserProductMap;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class UserProductMapServiceTEst extends BaseTest {
    @Autowired
    UserProductMapService userProductMapService;
    @Test
    public void a() {
        UserProductMap userProductMap = new UserProductMap();
        PersonInfo user = new PersonInfo();
        user.setUserId(1l);
        userProductMap.setUser(user);
        UserProductMapExecution ue = userProductMapService.listUserProductMap(userProductMap,1,100);
        for (UserProductMap u:ue.getUserProductMapList()){
            System.out.println(u.getShop().getPhone());
        }
    }
    @Test
    public void b(){

        UserProductMapExecution userProductMapExecution=userProductMapService.overUserProductMap(1,1);
        System.out.println(userProductMapExecution.getState()+userProductMapExecution.getStateInfo());
    }
}
