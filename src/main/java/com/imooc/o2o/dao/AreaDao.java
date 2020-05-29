package com.imooc.o2o.dao;

import com.imooc.o2o.entity.Area;

import java.util.List;
public interface AreaDao {
    //区域列表 areaList
    List <Area> queryArea();
    /**
     *
     * @param area
     * @return
     */
    int insertArea(Area area);

    /**
     *
     * @param area
     * @return
     */
    int updateArea(Area area);

    /**
     *
     * @param areaId
     * @return
     */
    int deleteArea(long areaId);

    /**
     *
     * @param areaIdList
     * @return
     */
    int batchDeleteArea(List<Long> areaIdList);

}
