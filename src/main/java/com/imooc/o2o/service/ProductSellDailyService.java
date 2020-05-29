package com.imooc.o2o.service;

import com.imooc.o2o.entity.ProductSellDaily;

import java.util.Date;
import java.util.List;

public interface ProductSellDailyService {
    //每日定时对所有店铺商品进行统计
    void dailyCalculate();

    List<ProductSellDaily> listProductSellDaily(ProductSellDaily productSellDailyCondition, Date beginTime,Date endTime);
/**
 * 如果没有销量，补全信息将其销量为零
 */
}
