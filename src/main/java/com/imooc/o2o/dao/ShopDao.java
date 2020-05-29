package com.imooc.o2o.dao;

import com.imooc.o2o.entity.Shop;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShopDao {
    int insertShop(Shop shop);//返回1代表插入成功
    int updateShop(Shop shop);//更改店铺信息
    Shop queryByShopId(long shopId);

    /**
     * 分页查询店铺，可输入的条件有：店铺名（模糊），店铺状态，店铺类别，区域id，owner创建者
     * @param shopCondition 查询的条件
     * @param rowIndex  从第几行开始取数据
     * @param pagesize  返回的条数
     * @return
     */
    List<Shop> queryShopList(@Param("shopCondition")Shop shopCondition,@Param("rowIndex")int rowIndex,@Param("pageSize")int pagesize);

    /**
     *返回queryShopList总数
     * @param shopCondition  查找田间
     * @return  返回查找店铺的总条数
     */
    int queryShopCount(@Param("shopCondition")Shop shopCondition);
}
