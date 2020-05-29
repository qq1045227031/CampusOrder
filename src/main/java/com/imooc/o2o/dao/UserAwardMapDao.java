package com.imooc.o2o.dao;

import com.imooc.o2o.entity.UserAwardMap;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserAwardMapDao {
    /**
     *根据查询条件分页查询用户兑换记录列表
     * @param userAwardCondition
	 * @param rowIndex
	 * @param pageSize
	 * @return
             */
    List<UserAwardMap> queryUserAwardMapList(
            @Param("userAwardCondition") UserAwardMap userAwardCondition,
            @Param("rowIndex") int rowIndex, @Param("pageSize") int pageSize);

    /**
     *条件下总数
     * @param userAwardCondition
     * @return
     */
    int queryUserAwardMapCount(
            @Param("userAwardCondition") UserAwardMap userAwardCondition);

    /**
     *查出某个id
     * @param userAwardId
     * @return
     */
    UserAwardMap queryUserAwardMapById(@Param("userAwardId") long userAwardId);

    /**
     *添加
     * @param userAwardMap
     * @return
     */
    int insertUserAwardMap(UserAwardMap userAwardMap);

    /**
     *修改
     * @param userAwardMap
     * @return
     */
    int updateUserAwardMap(UserAwardMap userAwardMap);
}
