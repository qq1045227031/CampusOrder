package com.imooc.o2o.dao;

import com.imooc.o2o.BaseTest;
import com.imooc.o2o.dto.ShopCate;
import com.imooc.o2o.entity.ShopCategory;
import com.imooc.o2o.service.ShopCategoryService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class ShopCategoryDaoTest extends BaseTest {
    @Autowired
    ShopCategoryDao shopCategoryDao;

    @Autowired
    ShopCategoryService shopCategoryService;
    @Test
    public void testQ(){
       List<ShopCategory> shopCategoryList  =  shopCategoryService.getShopCategoryList(null);
       for (ShopCategory shopCategory:shopCategoryList){
           System.out.println(shopCategory.getShopCategoryName());
       }
    }
    @Test
    public void testQueryProductCategory() {
        ShopCategory shopCategory = new ShopCategory();
        ShopCategory parent = new ShopCategory();
        parent.setShopCategoryId(-0l);
        shopCategory.setParent(parent);
        List<ShopCategory> shopCategoryList = shopCategoryDao.queryShopCategory(shopCategory);//null一级 -1二级  0全部
        List<ShopCate> shopCates = new ArrayList<>();
        for (ShopCategory s : shopCategoryList) {
            ShopCate shopCate = new ShopCate(s);
            shopCates.add(shopCate);
        }
        for (ShopCate s:shopCates){
            System.out.println(s.getShopCategoryName());
        }
    }
    @Test
    public void testList(){
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        List<Integer> list2 = new ArrayList<>();
        for (Integer integer:list){
            list2.add(integer);
        }
        for (Integer integer:list2){
            System.out.println(integer);
        }
    }

        @Test
        public void TestShopCategory () {
//            List<ShopCategory> shopCategoryList = shopCategoryDao.queryShopCategory(new ShopCategory());
//            for (ShopCategory shopCategory : shopCategoryList) {
//                System.out.println(shopCategory.getShopCategoryName());
//            }
            ShopCategory shopCategoryTest = new ShopCategory();
            ShopCategory shopCategoryParent = new ShopCategory();
            shopCategoryParent.setShopCategoryId(1L);
            shopCategoryTest.setParent(shopCategoryParent);
            List<ShopCategory> shopCategories = shopCategoryDao.queryShopCategory(shopCategoryTest);
            for (ShopCategory shopCategory : shopCategories) {
                System.out.println(shopCategory.getShopCategoryName());
            }
        }
    @Test
    public void TestShopCategory2 () {
        ShopCategory shopCategory = shopCategoryDao.getShopCategoryById(1l);
        System.out.println(shopCategory.getShopCategoryName());
    }
    @Test
    public void testD(){
        List<Long> list = new ArrayList<>();
        list.add(1l);
        list.add(3l) ;
        List<ShopCategory> shopCategoryList = shopCategoryDao.queryShopCategoryByIds(list);
        for (ShopCategory shopCategory : shopCategoryList){
            System.out.println(shopCategory.getShopCategoryName());
        }
    }
    @Test
    public void testInsert(){
        ShopCategory shopCategory = new ShopCategory();
        shopCategory.setParent(null);
        shopCategory.setShopCategoryName("测试插入");
        shopCategory.setPriority(100);
        int eff = shopCategoryDao.insertShopCategory(shopCategory);
        System.out.println(eff);
    }
    @Test
    public void testUpdate(){
//        ShopCategory shopCategory = shopCategoryDao.getShopCategoryById(1l);
//        System.out.println(shopCategory.getShopCategoryName());
        ShopCategory shopCategory = new ShopCategory();
        shopCategory.setShopCategoryId(34l);
        shopCategory.setShopCategoryName("测试修改");
        int eff=shopCategoryDao.updateShopCategory(shopCategory);
        System.out.println(eff);
    }
    @Test
    public void testDel(){
       int eff = shopCategoryDao.deleteShopCategory(34l);
       System.out.println(eff);
       List<Long> list =  new ArrayList<>();
       list.add(35l);
      int eff2 =  shopCategoryDao.batchDeleteShopCategory(list);
      System.out.println(eff2);
    }
    @Test
    public void testSecond(){
        ShopCategory shopCategory = new ShopCategory();
        ShopCategory parent = new ShopCategory();
        parent.setShopCategoryId(-1l);
        shopCategory.setParent(parent);
        List<ShopCategory> shopCategoryList = shopCategoryDao.queryShopCategory(shopCategory);
        for (ShopCategory s:shopCategoryList){
            System.out.println(s.getShopCategoryName());
        }
    }


}

