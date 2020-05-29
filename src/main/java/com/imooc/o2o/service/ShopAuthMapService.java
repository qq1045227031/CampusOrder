package com.imooc.o2o.service;

import com.imooc.o2o.dto.ShopAuthMapExecution;
import com.imooc.o2o.entity.ShopAuthMap;
import org.springframework.stereotype.Service;


public interface ShopAuthMapService {
    /**
     *
     * @param shopId
     * @param pageIndex
     * @param pageSize
     * @return
     */
    ShopAuthMapExecution listShopAuthMapByShopId(Long shopId,
                                                 Integer pageIndex, Integer pageSize);

    /**
     *添加授权信息
     * @param shopAuthMap
     * @return
     * @throws RuntimeException
     */
    ShopAuthMapExecution addShopAuthMap(ShopAuthMap shopAuthMap)
            throws RuntimeException;

    /**
     *更新授权信息
     * @param shopAuthMap
     * @return
     * @throws RuntimeException
     */
    ShopAuthMapExecution modifyShopAuthMap(ShopAuthMap shopAuthMap) throws RuntimeException;

//    /**
//     *删除
//     * @param shopAuthMapId
//     * @return
//     * @throws RuntimeException
//     */
//    ShopAuthMapExecution removeShopAuthMap(Long shopAuthMapId)
//            throws RuntimeException;

    /**
     *根据shopAuthId返回对应手段信息
     * @param shopAuthId
     * @return
     */
    ShopAuthMap getShopAuthMapById(Long shopAuthId);
}
