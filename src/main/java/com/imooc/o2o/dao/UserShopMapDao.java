package com.imooc.o2o.dao;

import com.imooc.o2o.entity.UserShopMap;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserShopMapDao {
    /**
     *根据条件查询
     * @param userShopCondition
     * @param rowIndex
     * @param pageSize
     * @return
     */
    List<UserShopMap> queryUserShopMapList(
            @Param("userShopCondition") UserShopMap userShopCondition,
            @Param("rowIndex") int rowIndex, @Param("pageSize") int pageSize);

    /**
     * 查询某个用户在某个商品的总积分
     * @param userId
     * @param shopId
     * @return
     */
    UserShopMap queryUserShopMap(@Param("userId") long userId,
                                    @Param("shopId") long shopId);

    /**
     *查询条件的总数
     * @param userShopCondition
     * @return
     */
    int queryUserShopMapCount(
            @Param("userShopCondition") UserShopMap userShopCondition);

    /**
     *插入
     * @param userShopMap
     * @return
     */
    int insertUserShopMap(UserShopMap userShopMap);

    /**
     *修改
     * @param userShopMap
     * @return
     */
    int updateUserShopMapPoint(UserShopMap userShopMap);
}
