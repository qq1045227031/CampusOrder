package com.imooc.o2o.dao;

import com.imooc.o2o.BaseTest;
import com.imooc.o2o.dto.UserShopMapExecution;
import com.imooc.o2o.entity.PersonInfo;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.entity.UserShopMap;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

public class UserShopMapDaoTest extends BaseTest {
    @Autowired
    private UserShopMapDao userShopMapDao;
    @Test
    public void testInsert(){
        UserShopMap userShopMap = new UserShopMap();
        userShopMap.setCreateTime(new Date());
        userShopMap.setPoint(50);
        Shop shop = new Shop();
        shop.setShopId(1L);
        userShopMap.setShop(shop);
        PersonInfo personInfo = new PersonInfo();
        personInfo.setUserId(1L);
        userShopMap.setUser(personInfo);
        int eff=userShopMapDao.insertUserShopMap(userShopMap);
        System.out.println(eff);
    }
    @Test
    public void testB(){
        UserShopMap userShopMap = userShopMapDao.queryUserShopMap(1l,1l);
        System.out.println("++++++3++++++"+userShopMap.getUser().getName());
        UserShopMap userShopMap1 = new UserShopMap();
        PersonInfo personInfo = new PersonInfo();
        personInfo.setName("测试");
        userShopMap.setUser(personInfo);
        List<UserShopMap> userShopMaps = userShopMapDao.queryUserShopMapList(userShopMap1,0,3);
        System.out.println("++++++3++++++"+userShopMaps.size());
        int eff = userShopMapDao.queryUserShopMapCount(userShopMap1);
        System.out.println("++++++3++++++"+eff);
        userShopMap.setPoint(999);
        int effct = userShopMapDao.updateUserShopMapPoint(userShopMap);
        System.out.println("++++++4++++++"+effct);
    }
    @Test
    public void testC(){
        UserShopMap userShopMap = new UserShopMap();
        PersonInfo personInfo = new PersonInfo();
        personInfo.setUserId(1l);
        Shop shop = new Shop();
        shop.setShopId(1l);
        userShopMap.setPoint(999);
        userShopMap.setUser(personInfo);
        userShopMap.setShop(shop);
        int eff = userShopMapDao.updateUserShopMapPoint(userShopMap);
        System.out.println(eff);
    }
    @Test
    public void testD(){
        UserShopMap userShopMap = new UserShopMap();
        Shop shop = new Shop();
        shop.setShopId(1l);
        userShopMap.setShop(shop);
        List<UserShopMap> userShopMapList = userShopMapDao.queryUserShopMapList(userShopMap,0,3);
        for (UserShopMap us :userShopMapList){
            System.out.println(us.getUser().getName());
        }
    }

}
