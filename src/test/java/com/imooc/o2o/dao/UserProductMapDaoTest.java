package com.imooc.o2o.dao;

import com.imooc.o2o.BaseTest;
import com.imooc.o2o.entity.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

public class UserProductMapDaoTest extends BaseTest {
    @Autowired
    private UserProductMapDao userProductMapDao;
    @Test
    public void testUpdate(){
        UserProductMap userProductMap = new UserProductMap();
        userProductMap.setUserProductId(1l);
        PersonInfo personInfo = new PersonInfo();
        personInfo.setUserId(2l);
        userProductMap.setOperator(personInfo);
        int eff = userProductMapDao.updateUserProductMap(userProductMap);
        System.out.println(eff);
    }
    @Test
    public void testQueryById(){
        UserProductMap userProductMap = userProductMapDao.queryUserProductMapById(1l);
        System.out.println(userProductMap.getShop().getShopName());
    }
    @Test
    public void testAinsert() {
        UserProductMap userProductMap = new UserProductMap();
        userProductMap.setCreateTime(new Date());
        PersonInfo personInfo = new PersonInfo();
        personInfo.setUserId(1l);
        userProductMap.setUser(personInfo);
        userProductMap.setOperator(personInfo);
        Shop shop = new Shop();
        shop.setShopId(1l);
        userProductMap.setShop(shop);
        userProductMap.setPoint(1111);
        Product product = new Product();
        product.setProductId(1l);
        userProductMap.setProduct(product);
        int eff = userProductMapDao.insertUserProductMap(userProductMap);
        System.out.println(eff);
    }
    @Test
    public void testQuery(){
        UserProductMap userProductMap= new UserProductMap();
        PersonInfo personInfo= new PersonInfo();
        personInfo.setName("测试");
        userProductMap.setUser(personInfo);
        List<UserProductMap> userProductMaps = userProductMapDao.queryUserProductMapList(userProductMap,0,3);
        for (UserProductMap user:userProductMaps){
            System.out.println(user.getShop().getPhone());
        }
        System.out.println("-------------1--------------"+userProductMaps.size());
        int count = userProductMapDao.queryUserProductMapCount(userProductMap);
        System.out.println("-------------2--------------"+count);

    }
}
