package com.imooc.o2o.dao;

import com.imooc.o2o.BaseTest;
import com.imooc.o2o.entity.PersonInfo;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.entity.ShopAuthMap;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

public class ShopAuthMapDaoTest extends BaseTest {
    @Autowired
    private ShopAuthMapDao shopAuthMapDao;
    @Test
    public void testInsert(){
        ShopAuthMap shopAuthMap = new ShopAuthMap();
        shopAuthMap.setCreateTime(new Date());
        PersonInfo personInfo = new PersonInfo();
        personInfo.setUserId(3l);
        shopAuthMap.setTitle("打工仔");
        shopAuthMap.setEnableStatus(1);
        shopAuthMap.setEmployee(personInfo);
        shopAuthMap.setLastEditTime(new Date());
        Shop shop = new Shop();
        shop.setShopId(1l);
        shopAuthMap.setShop(shop);
        shopAuthMap.setTitleFlag(1);
        int eff=shopAuthMapDao.insertShopAuthMap(shopAuthMap);
        System.out.println("++++++1++++++"+eff);
    }
    @Test
    public void testQuery(){
//       List<ShopAuthMap> list = shopAuthMapDao.queryShopAuthMapListByShopId(1l,0,3);
//       System.out.println("+++++++1++++++"+list.size());
//       int eff = shopAuthMapDao.queryShopAuthCountByShopId(1l);
//       System.out.println("+++++++2++++++"+eff);
       ShopAuthMap shopAuthMap = shopAuthMapDao.queryShopAuthMapById(1l);
       System.out.println("+++++++3++++++"+shopAuthMap.getEmployee().getName());
//       shopAuthMap.setTitle("假的ceo");
//        eff = shopAuthMapDao.updateShopAuthMap(shopAuthMap);
//        System.out.println("+++++++4++++++"+eff);
//        eff = shopAuthMapDao.deleteShopAuthMap(2l);
//        System.out.println("+++++++5++++++"+eff);
    }


}
