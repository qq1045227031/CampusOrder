package com.imooc.o2o.service;

import com.imooc.o2o.dto.AreaExecution;
import com.imooc.o2o.entity.Area;

import java.util.List;

public interface AreaService{
    public static final String AREALISTKEY="arealist";
    List<Area> getAreaList();
    /**
     *
     * @param area
     * @return
     */
    AreaExecution addArea(Area area);

    /**
     *
     * @param area
     * @return
     */
    AreaExecution modifyArea(Area area);

    /**
     *
     * @param areaId
     * @return
     */
    AreaExecution removeArea(long areaId);

    /**
     *
     * @param areaIdList
     * @return
     */
    AreaExecution removeAreaList(List<Long> areaIdList);
}
