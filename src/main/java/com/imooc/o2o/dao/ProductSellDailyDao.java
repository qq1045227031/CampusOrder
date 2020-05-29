package com.imooc.o2o.dao;

import com.imooc.o2o.entity.ProductSellDaily;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface ProductSellDailyDao {
    /**
     * 根据查询条件返回商品销量统计表
     * @param productSellDaily
     * @param beginTime
     * @param endTime
     * @return
     */
    List<ProductSellDaily> queryProductSellDailyList(
            @Param("productSellDailyCondition")ProductSellDaily productSellDaily, @Param("beginTime")Date beginTime,@Param("endTime")Date endTime);
    int insertProductSellDaily();
    //无销量  置为零
    int insertDefaultProductSellDaily();
}
