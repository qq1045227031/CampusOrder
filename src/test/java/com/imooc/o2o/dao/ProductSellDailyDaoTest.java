package com.imooc.o2o.dao;

import com.imooc.o2o.BaseTest;
import com.imooc.o2o.entity.ProductSellDaily;
import com.imooc.o2o.entity.Shop;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ProductSellDailyDaoTest extends BaseTest {
    @Autowired
    private ProductSellDailyDao productSellDailyDao;
    @Test
    public void testAInsertProductSellDaily()throws Exception{
        int eff = productSellDailyDao.insertProductSellDaily();
        System.out.println(eff);
    }
    @Test
    public void testBQuery()throws Exception{
        productSellDailyDao.insertDefaultProductSellDaily();
    }
    @Test
    public void testQueryC()throws  Exception{
        ProductSellDaily productSellDaily = new ProductSellDaily();
        Shop shop = new Shop();
        shop.setShopId(1l);
        productSellDaily.setShop(shop);
        Calendar calendar = Calendar.getInstance();
        //获取昨天的日期
        calendar.add(Calendar.DATE,-1);
        Date endTime  = calendar.getTime();
        //获取七天前的日期  再次-6    -1-6=-7
        calendar.add(Calendar.DATE,-6);
        Date beginTime = calendar.getTime();
        List<ProductSellDaily> productSellDailyList  = productSellDailyDao.queryProductSellDailyList(productSellDaily,beginTime,endTime);
        for (ProductSellDaily p:productSellDailyList){
            System.out.println(p.getTotal());
        }
    }

}
