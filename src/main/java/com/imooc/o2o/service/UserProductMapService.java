package com.imooc.o2o.service;

import com.imooc.o2o.dto.UserProductMapExecution;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.entity.UserProductMap;

public interface UserProductMapService {
    /**
     * 查出用户消费
     * @param userProductCondition
     * @param pageIndex
     * @param pageSize
     * @return
     */
    UserProductMapExecution listUserProductMap(
            UserProductMap userProductCondition, Integer pageIndex,
            Integer pageSize);

    /**
     *
     * @param userProductMap
     * @return
     * @throws RuntimeException
     */
    UserProductMapExecution addUserProductMap(UserProductMap userProductMap)
            throws RuntimeException;

    UserProductMapExecution overUserProductMap(long userProductId, long shopId);

}
