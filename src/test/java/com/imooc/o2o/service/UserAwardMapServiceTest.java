package com.imooc.o2o.service;

import com.imooc.o2o.BaseTest;
import com.imooc.o2o.dao.UserAwardMapDao;
import com.imooc.o2o.dto.UserAwardMapExecution;
import com.imooc.o2o.dto.UserProductMapExecution;
import com.imooc.o2o.entity.Award;
import com.imooc.o2o.entity.PersonInfo;
import com.imooc.o2o.entity.UserAwardMap;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class UserAwardMapServiceTest extends BaseTest {
    @Autowired
    private UserAwardMapService userAwardMapService;
    @Autowired
    private PersonInfoService personInfoService;
    @Autowired
    private AwardService awardService;
    @Autowired
    private UserAwardMapDao userAwardMapDao;
    @Test
    public void testquery(){
        long userAwardId = 21l;
//        UserAwardMap userAwardMap = userAwardMapService.getUserAwardMapById(userAwardId);//?
//        UserAwardMap userAwardMap = userAwardMapDao.queryUserAwardMapById(userAwardId);
        UserAwardMap userAwardMap = new UserAwardMap();
        userAwardMap.setUserAwardId(21l);
        UserAwardMapExecution userAwardMapExecution = userAwardMapService.listUserAwardMap(userAwardMap,0,999);
        userAwardMap=userAwardMapExecution.getUserAwardMapList().get(0);
        Award award =  awardService.getAwardById(userAwardMap.getAward().getAwardId());
        System.out.println(award.getAwardName());

//        PersonInfo personInfo = personInfoService.getPersonInfoById(3l);
//        UserAwardMap userAwardMap = new UserAwardMap();
//        userAwardMap.setUser(personInfo);
//        UserAwardMapExecution ue = userAwardMapService.listUserAwardMap(userAwardMap,1,100);
//        ue.getUserAwardMapList();
//        for (UserAwardMap us :ue.getUserAwardMapList()){
//            System.out.println(us.getUserAwardId());
//        }
    }
    @Test
    public void testb(){
       UserAwardMap userAwardMap =  userAwardMapService.getUserAwardMapById(3);
       System.out.println(userAwardMap.getUser().getName());
    }
}
