package com.imooc.o2o.service;

import com.imooc.o2o.BaseTest;
import com.imooc.o2o.entity.ShopCategory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

public class ShopCategoryServiceTest extends BaseTest {
    @Autowired
    ShopCategoryService shopCategoryService ;
    @Test
    public void testA() throws IOException {
        List<ShopCategory>  list=shopCategoryService.getAllSecondLevelShopCategory();
        for (ShopCategory shopCategory:list){
            System.out.println(shopCategory.getShopCategoryName());
        }
    }
}
