package com.imooc.o2o.service.impl;

import com.imooc.o2o.dao.ProductSellDailyDao;
import com.imooc.o2o.entity.ProductSellDaily;
import com.imooc.o2o.service.ProductSellDailyService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
@Service
public class ProductSellDailyServiceImpl implements ProductSellDailyService {
    @Autowired
    private ProductSellDailyDao productSellDailyDao;
    @Override
    public void dailyCalculate() {
        productSellDailyDao.insertProductSellDaily();

    }

    @Override
    public List<ProductSellDaily> listProductSellDaily(ProductSellDaily productSellDailyCondition, Date beginTime, Date endTime) {
        return productSellDailyDao.queryProductSellDailyList(productSellDailyCondition,beginTime,endTime);
    }
}
