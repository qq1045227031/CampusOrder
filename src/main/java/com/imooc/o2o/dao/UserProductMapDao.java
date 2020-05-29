package com.imooc.o2o.dao;

import com.imooc.o2o.entity.UserProductMap;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserProductMapDao {
    /**
     *根据查询条件返回商品记录列表
     * @param userProductCondition
     * @param rowIndex
     * @param pageSize
     * @return
     */
    List<UserProductMap> queryUserProductMapList(
            @Param("userProductCondition") UserProductMap userProductCondition,
            @Param("rowIndex") int rowIndex, @Param("pageSize") int pageSize);

    /**
     *统计总数
     * @param userProductCondition
     * @return
     */
    int queryUserProductMapCount(
            @Param("userProductCondition") UserProductMap userProductCondition);

    /**
     *添加
     * @param userProductMap
     * @return
     */
    int insertUserProductMap(UserProductMap userProductMap);

    int updateUserProductMap(UserProductMap userProductMap);

    UserProductMap queryUserProductMapById(long userProductId);
}
