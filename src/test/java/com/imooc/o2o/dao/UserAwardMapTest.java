package com.imooc.o2o.dao;

import com.imooc.o2o.BaseTest;
import com.imooc.o2o.entity.Award;
import com.imooc.o2o.entity.PersonInfo;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.entity.UserAwardMap;
import com.imooc.o2o.service.UserAwardMapService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

public class UserAwardMapTest extends BaseTest {
    @Autowired
    private UserAwardMapDao userAwardMapDao;
    @Autowired
    private UserAwardMapService userAwardMapService;
    @Test
    public void TestInsert(){
        UserAwardMap userAwardMap = new UserAwardMap();
        PersonInfo personInfo = new PersonInfo();
        personInfo.setUserId(1l);
        userAwardMap.setUser(personInfo);
        userAwardMap.setOperator(personInfo);
        Award award = new Award();
        award.setAwardId(2l);
        award.setShopId(1l);
        userAwardMap.setAward(award);
        Shop shop = new Shop();
        shop.setShopId(1l);
        userAwardMap.setShop(shop);
        userAwardMap.setAward(award);
        userAwardMap.setCreateTime(new Date());
        userAwardMap.setUsedStatus(0);
        userAwardMap.setPoint(100);
        int effect = userAwardMapDao.insertUserAwardMap(userAwardMap);
        System.out.println(effect);
    }
    @Test
    public void TestQuery(){
        UserAwardMap userAwardMap = new UserAwardMap();
        List<UserAwardMap> userAwardMapList = userAwardMapDao.queryUserAwardMapList(userAwardMap,0,100);
        for (UserAwardMap userAwardMap1:userAwardMapList) {
            System.out.println("------1-----" + userAwardMap1.getUserAwardId());
        }
        PersonInfo personInfo = new PersonInfo();
        personInfo.setUserId(3l);
        userAwardMap.setUser(personInfo);
        List<UserAwardMap> userAwardMapList2 = userAwardMapDao.queryUserAwardMapList(userAwardMap,0,100);
        for (UserAwardMap userAwardMap1:userAwardMapList2) {
            System.out.println("------2-----" + userAwardMap1.getUserAwardId());
        }
//        int count = userAwardMapDao.queryUserAwardMapCount(userAwardMap);
//        System.out.println("------2---"+count);
//        userAwardMap.setUsedStatus(1);
//        List<UserAwardMap> userAwardMaps = userAwardMapDao.queryUserAwardMapList(userAwardMap,0,100);
//        System.out.println("-----3----"+userAwardMaps.size());
//        userAwardMap = userAwardMapDao.queryUserAwardMapById(1);
//        System.out.println("---4-------"+userAwardMap.getUser().getName());
    }
    @Test
    public void testUpdate(){
        UserAwardMap userAwardMap = userAwardMapDao.queryUserAwardMapById(1);
        userAwardMap.setUsedStatus(0);
        int eff = userAwardMapDao.updateUserAwardMap(userAwardMap);
        System.out.println(eff);
    }

    @Test
    public void test4(){
        UserAwardMap userAwardMap = userAwardMapService.getUserAwardMapById(6l);
        System.out.println(userAwardMap.getUser().getName());
    }
}
