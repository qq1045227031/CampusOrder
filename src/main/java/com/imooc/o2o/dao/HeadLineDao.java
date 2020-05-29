package com.imooc.o2o.dao;

import com.imooc.o2o.entity.HeadLine;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HeadLineDao {
//    按照可用状态查询，查出与其相同的可用状态的headLine，如果为空则查出所有
    List<HeadLine> queryHeadLine(@Param("headLineCondition")HeadLine headLineCondition);

    /**
     *
     * @param lineId
     * @return
     */
    HeadLine queryHeadLineById(long lineId);

    /**
     *
     * @param lineIdList
     * @return
     */
    List<HeadLine> queryHeadLineByIds(List<Long> lineIdList);

    /**
     *
     * @param headLine
     * @return
     */
    int insertHeadLine(HeadLine headLine);

    /**
     *
     * @param headLine
     * @return
     */
    int updateHeadLine(HeadLine headLine);

    /**
     *
     * @param headLineId
     * @return
     */
    int deleteHeadLine(long headLineId);

    /**
     *
     * @param lineIdList
     * @return
     */
    int batchDeleteHeadLine(List<Long> lineIdList);
}
