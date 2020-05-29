package com.imooc.o2o.dao;

import com.imooc.o2o.BaseTest;
import com.imooc.o2o.entity.ProductCategory;
import com.imooc.o2o.entity.ShopCategory;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@FixMethodOrder(MethodSorters.NAME_ASCENDING)//控制@Test执行顺序
public class ProductCategoryDaoTest extends BaseTest {
    @Autowired
    ProductCategoryDao productCategoryDao;

    @Test
    @Ignore
    public void testBQueryProductCategoyByShopId(){
        List<ProductCategory> productCategoryDaoList =productCategoryDao.queryProductCategoryByShopId(1L);
        for (ProductCategory productCategory:productCategoryDaoList)
        {
            System.out.println("查询"+productCategory.getProductCategoryName());
        }
    }
    @Test
    public void testABathInsertProductCategory(){
        ProductCategory productCategory1 = new ProductCategory();
        productCategory1.setCreateTime(new Date());
        productCategory1.setPriority(80);
        productCategory1.setProductCategoryName("DaoTest1");
        productCategory1.setShopId(1l);
        ProductCategory productCategory2 = new ProductCategory();
        productCategory2.setCreateTime(new Date());
        productCategory2.setPriority(80);
        productCategory2.setProductCategoryName("DaoTest2");
        productCategory2.setShopId(1l);
        List<ProductCategory> productCategoryList= new ArrayList<ProductCategory>();
        productCategoryList.add(productCategory1);
        productCategoryList.add(productCategory2);
       int effectNum =  productCategoryDao.batchInsertProductCategory(productCategoryList);
       System.out.println("添加的行数为"+effectNum);
    }
    @Test
    public void testCDeleteProductCategory()throws Exception{
        long shopId = 1l;
        List<ProductCategory> productCategoryList =productCategoryDao.queryProductCategoryByShopId(shopId);
        for (ProductCategory productCategory:productCategoryList){
            if("DaoTest2".equals(productCategory.getProductCategoryName())||"DaoTest1".equals(productCategory.getProductCategoryName())){
                int effectedNum =productCategoryDao.deleteProductCategory(productCategory.getProductCategoryId(),shopId);
             System.out.println("删除影响的行数为"+effectedNum);
            }
        }
    }


}
