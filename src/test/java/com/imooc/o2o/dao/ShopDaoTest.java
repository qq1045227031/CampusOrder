package com.imooc.o2o.dao;

import com.imooc.o2o.BaseTest;
import com.imooc.o2o.entity.Area;
import com.imooc.o2o.entity.PersonInfo;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.entity.ShopCategory;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

//继承baseTest，使用spring容器
public class  ShopDaoTest extends BaseTest {
    @Autowired
    private ShopDao shopDao;
    @Test
    public void testQ(){
//        Shop shop = new Shop();
//        Area area =  new Area();
//        area.setAreaId(2l);
//        shop.setArea(area);
//        List<Shop> shopList = shopDao.queryShopList(shop,0,100);
//        for (Shop s:shopList){
//            System.out.println(s.getShopName());
//        }
        Shop shop = new Shop();
        shop.setShopId(33l);
        Area area = new Area();
        area.setAreaId(-1l);
        shop.setArea(null);
        int eff = shopDao.updateShop(shop);
        System.out.println(eff);
    }
    @Test
    public void testQueryShopListAndCount(){
        Shop shopCondition = new Shop();
        ShopCategory childCategory = new ShopCategory();
        ShopCategory parentCategory = new ShopCategory();
        parentCategory.setShopCategoryId(1l);
        childCategory.setParent(parentCategory);
        shopCondition.setShopCategory(childCategory);//传入一个有夫类的模型，获取改夫类下的所有子类
        List<Shop> shopList = shopDao.queryShopList(shopCondition,0,5);
        for (Shop shop:shopList){
            System.out.println(shop.getShopName());
        }
}
    @Test
    public void testQueryShopList(){
        Shop shopCondition = new Shop();
//        PersonInfo personInfo = new PersonInfo();
//        personInfo.setUserId(1l);
//        shopCondition.setOwner(personInfo);
//        List<Shop> shopList = shopDao.queryShopList(shopCondition,0,10);
        ShopCategory shopCategory= new ShopCategory();
        shopCategory.setShopCategoryId(3l);
        shopCondition.setShopCategory(shopCategory);
        List<Shop> shopList = shopDao.queryShopList(shopCondition,0,10);
        for (Shop shop:shopList){
            System.out.println(shop.getShopId()+" "+shop.getShopName());
        }
        int id = shopDao.queryShopCount(shopCondition);
        System.out.println("查到的条数是"+id);
        //注意，有个缺点就是如果shop外键area_id或者shop_category_id为空，那么会查不出该条数据因为sql语句中要求必须要有这两数据否则查不出
    }
    @Test
    @Ignore  //运行测试不加载
    public void testInsertShop(){
        Shop shop=new Shop();
        PersonInfo owner = new PersonInfo();
        Area area = new Area();
        ShopCategory shopCategory= new ShopCategory();
        owner.setUserId(1L);
        area.setAreaId(2l);
        shopCategory.setShopCategoryId(1L);
        shop.setOwner(owner);
        shop.setArea(area);
        shop.setShopCategory(shopCategory);
        shop.setShopName("测试的店铺");
        shop.setShopDesc("描述");
        shop.setShopAddr("地址");
        shop.setPhone("13288214717");
        shop.setShopImg("头像");
        shop.setCreateTime(new Date());
        shop.setEnableStatus(1);
        shop.setAdvice("审核中");
        int effectedNum = shopDao.insertShop(shop);
        System.out.println(effectedNum);
    }
    @Test
    public void testUpdateShop(){
        Shop shop=new Shop();
        shop.setShopId(33L);
        shop.setShopName("update店铺");
        shop.setShopDesc("update描述");
        shop.setShopAddr("update地址");
        shop.setCreateTime(new Date());
        int effectedNum = shopDao.updateShop(shop);
        System.out.println(effectedNum);
    }
    @Test
    public void TestQuery(){
        Shop shop=shopDao.queryByShopId(1l);
        System.out.println(shop.getShopId()+shop.getShopName());
    }
}
