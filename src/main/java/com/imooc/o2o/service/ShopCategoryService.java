package com.imooc.o2o.service;

import com.imooc.o2o.dto.ImageHolder;
import com.imooc.o2o.dto.ShopCategoryExecution;
import com.imooc.o2o.entity.ShopCategory;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.IOException;
import java.util.List;

public interface ShopCategoryService {
    public static final String SCLISTKEY="shopcategorylist";
    /**
     * 根据查询条件获取shopCategory列表
     * @param shopCategoryCondition
     * @return
     */
    List<ShopCategory> getShopCategoryList(ShopCategory shopCategoryCondition);
    ShopCategory getShopCategoryById(long shopCategoryId);

    List<ShopCategory> getFirstLevelShopCategoryList() throws IOException;
    /**
     *
     * @return
     * @throws IOException
     */
    List<ShopCategory> getAllSecondLevelShopCategory() throws IOException;

    /**
     *
     * @param shopCategoryId
     * @return
     */
    ShopCategory getShopCategoryById(Long shopCategoryId);

    /**
     *
     * @param shopCategory
     * @param thumbnail
     * @return
     */
    ShopCategoryExecution addShopCategory(ShopCategory shopCategory,
                                          ImageHolder thumbnail);

    /**
     *
     * @param shopCategory
     * @param thumbnail
     * @param thumbnailChange
     * @return
     */
    ShopCategoryExecution modifyShopCategory(ShopCategory shopCategory,
                                             ImageHolder thumbnail, boolean thumbnailChange);

    /**
     *
     * @param shopCategoryId
     * @return
     */
    ShopCategoryExecution removeShopCategory(long shopCategoryId);

    /**
     *
     * @param shopCategoryIdList
     * @return
     */
    ShopCategoryExecution removeShopCategoryList(List<Long> shopCategoryIdList);
}
