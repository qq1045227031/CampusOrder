package com.imooc.o2o.service;

import com.imooc.o2o.dto.ImageHolder;
import com.imooc.o2o.dto.ShopExecution;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.exceptions.ShopOperationException;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.InputStream;

public interface ShopService {
    /**
     * 根据shopCondition分页返回相应店铺列表
     * @param shopCondition
     * @param pageIndex
     * @param pageSize
     * @return
     */
    ShopExecution getShopList(Shop shopCondition,int pageIndex,int pageSize);
    Shop getByShopId(long shopId);
    ShopExecution modifyShop(Shop shop, ImageHolder imageHolder) throws ShopOperationException;
    ShopExecution addShop(Shop shop, ImageHolder imageHolder) throws  ShopOperationException;
}
