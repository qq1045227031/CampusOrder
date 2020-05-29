package com.imooc.o2o.dao;

import com.imooc.o2o.entity.ProductCategory;
import com.imooc.o2o.entity.ShopCategory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShopCategoryDao {
    //传进一个ShopCategory，如果是空返回所有，如果不是，返回和他同级的相同父类的店铺种类
    List<ShopCategory> queryShopCategory(@Param("shopCategoryCondition")ShopCategory shopCategory);
    ShopCategory getShopCategoryById(long shopCategoryId);

    /**
     *
     * @param shopCategoryIdList
     * @return
     */
    List<ShopCategory> queryShopCategoryByIds(List<Long> shopCategoryIdList);

    /**
     *
     * @param shopCategory
     * @return
     */
    int insertShopCategory(ShopCategory shopCategory);

    /**
     *
     * @param shopCategory
     * @return
     */
    int updateShopCategory(ShopCategory shopCategory);

    /**
     *
     * @param shopCategoryId
     * @return
     */
    int deleteShopCategory(long shopCategoryId);

    /**
     *
     * @param shopCategoryIdList
     * @return
     */
    int batchDeleteShopCategory(List<Long> shopCategoryIdList);
}
